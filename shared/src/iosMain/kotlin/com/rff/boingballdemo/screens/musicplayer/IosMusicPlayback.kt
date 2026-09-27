package com.rff.boingballdemo.screens.musicplayer

import boingball.shared.generated.resources.Res
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
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
import platform.MediaPlayer.MPChangePlaybackPositionCommandEvent
import platform.MediaPlayer.MPMediaItemPropertyArtist
import platform.MediaPlayer.MPMediaItemPropertyPlaybackDuration
import platform.MediaPlayer.MPMediaItemPropertyTitle
import platform.MediaPlayer.MPNowPlayingInfoCenter
import platform.MediaPlayer.MPNowPlayingInfoPropertyElapsedPlaybackTime
import platform.MediaPlayer.MPNowPlayingInfoPropertyPlaybackRate
import platform.MediaPlayer.MPRemoteCommand
import platform.MediaPlayer.MPRemoteCommandCenter
import platform.MediaPlayer.MPRemoteCommandEvent
import platform.MediaPlayer.MPRemoteCommandHandlerStatus
import platform.MediaPlayer.MPRemoteCommandHandlerStatusCommandFailed
import platform.MediaPlayer.MPRemoteCommandHandlerStatusSuccess
import platform.darwin.NSObject
import platform.darwin.NSObjectProtocol

/**
 * AVAudioPlayer-based playlist. Publishes Now Playing info and handles lock-screen /
 * Control Center / headphone remote commands, audio session interruptions (calls,
 * Siri, alarms) and route changes (headphones unplugged).
 *
 * Background playback also needs the `audio` entry in `UIBackgroundModes` (Info.plist).
 */
@OptIn(ExperimentalForeignApi::class)
class IosMusicPlayback : MusicPlayback {
    private val _status = MutableStateFlow(PlaybackStatus())
    override val status: StateFlow<PlaybackStatus> = _status.asStateFlow()

    private var playlist: List<MusicTrack> = emptyList()
    private var currentIndex = 0
    private var player: AVAudioPlayer? = null
    private var playWhenReady = false
    // Set when an interruption (e.g. a phone call) paused playback.
    private var pausedByInterruption = false

    private val finishDelegate = FinishDelegate { skipToNext() }

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

    private val commandCenter = MPRemoteCommandCenter.sharedCommandCenter()
    private val remoteTargets: List<Pair<MPRemoteCommand, Any>> = listOf(
        commandCenter.playCommand.handle { play() },
        commandCenter.pauseCommand.handle { pause() },
        commandCenter.togglePlayPauseCommand.handle { if (playWhenReady) pause() else play() },
        commandCenter.nextTrackCommand.handle { skipToNext() },
        commandCenter.previousTrackCommand.handle { skipToPrevious() },
        commandCenter.changePlaybackPositionCommand.let { command ->
            command to command.addTargetWithHandler { event -> onChangePosition(event) }
        },
    )

    override fun currentPositionMs(): Long {
        val seconds = player?.currentTime ?: return 0L
        return (seconds * 1000.0).toLong()
    }

    override fun setPlaylist(tracks: List<MusicTrack>) {
        playWhenReady = false
        pausedByInterruption = false
        playlist = tracks
        currentIndex = 0
        load(currentIndex)
    }

    override fun play() {
        if (playlist.isEmpty()) return
        pausedByInterruption = false
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)
        val current = player ?: load(currentIndex) ?: return
        playWhenReady = current.play()
        publish()
    }

    override fun pause() {
        pausedByInterruption = false
        playWhenReady = false
        player?.pause()
        publish()
    }

    override fun stop() {
        pausedByInterruption = false
        playWhenReady = false
        player?.pause()
        player?.currentTime = 0.0
        publish()
    }

    override fun seekTo(positionMs: Long) {
        player?.currentTime = positionMs.coerceAtLeast(0L).toDouble() / 1000.0
        publish()
    }

    override fun skipToTrack(index: Int) {
        if (index !in playlist.indices) return
        currentIndex = index
        load(index)
    }

    override fun skipToNext() {
        if (playlist.isEmpty()) return
        skipToTrack((currentIndex + 1) % playlist.size)
    }

    override fun skipToPrevious() {
        if (playlist.isEmpty()) return
        skipToTrack((currentIndex - 1 + playlist.size) % playlist.size)
    }

    override fun moveTrack(from: Int, to: Int) {
        if (from !in playlist.indices || to !in playlist.indices) return
        playlist = playlist.moved(from, to)
        currentIndex = indexAfterMove(currentIndex, from, to)
        publish()
    }

    override fun release() {
        playWhenReady = false
        player?.stop()
        player?.delegate = null
        player = null
        observers.forEach { notificationCenter.removeObserver(it) }
        remoteTargets.forEach { (command, target) -> command.removeTarget(target) }
        MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = null
    }

    /** Loads track [index] at its start, playing it if playback is active. */
    private fun load(index: Int): AVAudioPlayer? {
        player?.stop()
        player?.delegate = null
        player = null
        val track = playlist.getOrNull(index)
        val url = track?.let { NSURL.URLWithString(Res.getUri(it.resourcePath)) }
        val next = url?.let { AVAudioPlayer(it, error = null) }
        if (next != null) {
            next.delegate = finishDelegate
            next.prepareToPlay()
            if (playWhenReady) playWhenReady = next.play()
            player = next
        } else {
            playWhenReady = false
        }
        publish()
        return next
    }

    private fun publish() {
        _status.value = PlaybackStatus(
            tracks = playlist,
            isPlaying = playWhenReady,
            currentIndex = currentIndex,
            positionMs = currentPositionMs(),
        )
        updateNowPlaying()
    }

    /** The system extrapolates elapsed time from the rate, so this runs on changes only. */
    private fun updateNowPlaying() {
        val track = playlist.getOrNull(currentIndex)
        val current = player
        MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = if (track == null || current == null) {
            null
        } else {
            mapOf<Any?, Any?>(
                MPMediaItemPropertyTitle to track.title,
                MPMediaItemPropertyArtist to track.composer,
                MPMediaItemPropertyPlaybackDuration to current.duration,
                MPNowPlayingInfoPropertyElapsedPlaybackTime to current.currentTime,
                MPNowPlayingInfoPropertyPlaybackRate to if (playWhenReady) 1.0 else 0.0,
            )
        }
    }

    private fun onChangePosition(event: MPRemoteCommandEvent?): MPRemoteCommandHandlerStatus {
        val positionEvent = event as? MPChangePlaybackPositionCommandEvent
            ?: return MPRemoteCommandHandlerStatusCommandFailed
        seekTo((positionEvent.positionTime * 1000.0).toLong())
        return MPRemoteCommandHandlerStatusSuccess
    }

    private fun onInterruption(notification: NSNotification?) {
        val info = notification?.userInfo ?: return
        val type = (info[AVAudioSessionInterruptionTypeKey] as? NSNumber)?.unsignedLongValue ?: return
        when (type) {
            AVAudioSessionInterruptionTypeBegan -> {
                // The system has already paused the player.
                if (!playWhenReady) return
                playWhenReady = false
                pausedByInterruption = true
                publish()
            }
            AVAudioSessionInterruptionTypeEnded -> {
                if (!pausedByInterruption) return
                pausedByInterruption = false
                val options = (info[AVAudioSessionInterruptionOptionKey] as? NSNumber)?.unsignedLongValue ?: 0uL
                if (options and AVAudioSessionInterruptionOptionShouldResume != 0uL) play()
            }
        }
    }

    private fun onRouteChange(notification: NSNotification?) {
        val info = notification?.userInfo ?: return
        val reason = (info[AVAudioSessionRouteChangeReasonKey] as? NSNumber)?.unsignedLongValue ?: return
        // Headphones unplugged or a Bluetooth device disconnected: pause, as Apple requires.
        if (reason == AVAudioSessionRouteChangeReasonOldDeviceUnavailable && playWhenReady) pause()
    }
}

/** Registers [action] for this remote command and returns the pair needed to unregister it. */
@OptIn(ExperimentalForeignApi::class)
private fun MPRemoteCommand.handle(action: () -> Unit): Pair<MPRemoteCommand, Any> =
    this to addTargetWithHandler {
        action()
        MPRemoteCommandHandlerStatusSuccess
    }

/** Advances to the next track when one finishes. */
private class FinishDelegate(
    private val onFinished: () -> Unit,
) : NSObject(), AVAudioPlayerDelegateProtocol {
    override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
        onFinished()
    }
}
