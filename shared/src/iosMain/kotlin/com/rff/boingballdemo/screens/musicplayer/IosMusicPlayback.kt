package com.rff.boingballdemo.screens.musicplayer

import boingball.shared.generated.resources.Res
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionInterruptionNotification
import platform.AVFAudio.AVAudioSessionInterruptionOptionKey
import platform.AVFAudio.AVAudioSessionInterruptionOptionShouldResume
import platform.AVFAudio.AVAudioSessionInterruptionTypeBegan
import platform.AVFAudio.AVAudioSessionInterruptionTypeEnded
import platform.AVFAudio.AVAudioSessionInterruptionTypeKey
import platform.AVFAudio.AVAudioSessionRouteChangeNotification
import platform.AVFAudio.AVAudioSessionRouteChangeReasonKey
import platform.AVFAudio.AVAudioSessionRouteChangeReasonOldDeviceUnavailable
import platform.AVFAudio.setActive
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNumber
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSObjectProtocol

@OptIn(ExperimentalForeignApi::class)
class IosMusicPlayback : MusicPlayback {
    private var player: AVAudioPlayer? = null
    private var started = false
    private var externalStateListener: ((Boolean) -> Unit)? = null
    // Set when an interruption (e.g. a phone call) paused playback.
    private var pausedByInterruption = false

    private val notificationCenter = NSNotificationCenter.defaultCenter
    private val observers: List<NSObjectProtocol> = listOf(
        notificationCenter.addObserverForName(
            name = AVAudioSessionInterruptionNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { notification -> onInterruption(notification) },
        notificationCenter.addObserverForName(
            name = AVAudioSessionRouteChangeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { notification -> onRouteChange(notification) },
    )

    override fun play(resourcePath: String, positionMs: Long) {
        pausedByInterruption = false
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)

        val url = NSURL.URLWithString(Res.getUri(resourcePath)) ?: return
        val next = AVAudioPlayer(url, error = null)
        val previous = player
        next.currentTime = positionMs.coerceAtLeast(0L).toDouble() / 1000.0
        next.prepareToPlay()
        next.play()
        previous?.stop()
        player = next
        started = true
    }

    override fun pause() {
        pausedByInterruption = false
        player?.pause()
        started = false
    }

    override fun stop() {
        pausedByInterruption = false
        player?.stop()
        player = null
        started = false
    }

    override fun seekTo(positionMs: Long) {
        player?.currentTime = positionMs.coerceAtLeast(0L).toDouble() / 1000.0
    }

    override fun currentPositionMs(): Long {
        val seconds = player?.currentTime ?: return 0L
        return (seconds * 1000.0).toLong()
    }

    override fun hasEnded(): Boolean {
        val current = player ?: return false
        if (!started) return false
        return current.duration > 0.0 &&
            !current.playing &&
            current.currentTime >= current.duration - 0.05
    }

    override fun setOnExternalStateChange(listener: ((isPlaying: Boolean) -> Unit)?) {
        externalStateListener = listener
    }

    override fun release() {
        externalStateListener = null
        observers.forEach { notificationCenter.removeObserver(it) }
        stop()
    }

    private fun onInterruption(notification: NSNotification?) {
        val info = notification?.userInfo ?: return
        val type = (info[AVAudioSessionInterruptionTypeKey] as? NSNumber)?.unsignedLongValue ?: return
        when (type) {
            AVAudioSessionInterruptionTypeBegan -> {
                // The system has already paused the player.
                if (!started) return
                started = false
                pausedByInterruption = true
                externalStateListener?.invoke(false)
            }
            AVAudioSessionInterruptionTypeEnded -> {
                if (!pausedByInterruption) return
                pausedByInterruption = false
                val options = (info[AVAudioSessionInterruptionOptionKey] as? NSNumber)?.unsignedLongValue ?: 0uL
                if (options and AVAudioSessionInterruptionOptionShouldResume == 0uL) return
                val current = player ?: return
                AVAudioSession.sharedInstance().setActive(true, error = null)
                if (current.play()) {
                    started = true
                    externalStateListener?.invoke(true)
                }
            }
        }
    }

    private fun onRouteChange(notification: NSNotification?) {
        val info = notification?.userInfo ?: return
        val reason = (info[AVAudioSessionRouteChangeReasonKey] as? NSNumber)?.unsignedLongValue ?: return
        // Headphones unplugged or a Bluetooth device disconnected: pause, as Apple requires.
        if (reason != AVAudioSessionRouteChangeReasonOldDeviceUnavailable || !started) return
        pause()
        externalStateListener?.invoke(false)
    }
}
