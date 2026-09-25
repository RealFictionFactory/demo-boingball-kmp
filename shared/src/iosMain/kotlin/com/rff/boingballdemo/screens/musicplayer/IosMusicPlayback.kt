package com.rff.boingballdemo.screens.musicplayer

import boingball.shared.generated.resources.Res
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
class IosMusicPlayback : MusicPlayback {
    private var player: AVAudioPlayer? = null
    private var started = false

    override fun play(resourcePath: String, positionMs: Long) {
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
        player?.pause()
        started = false
    }

    override fun stop() {
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

    override fun release() {
        stop()
    }
}
