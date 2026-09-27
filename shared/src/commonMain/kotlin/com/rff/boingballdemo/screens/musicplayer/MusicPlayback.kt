package com.rff.boingballdemo.screens.musicplayer

interface MusicPlayback {
    fun play(resourcePath: String, positionMs: Long)
    fun pause()
    fun stop()
    fun seekTo(positionMs: Long)
    fun currentPositionMs(): Long
    fun hasEnded(): Boolean
    fun release()

    /**
     * Receives play/pause changes the system forces on playback, e.g. another app taking
     * audio focus, an incoming call, or headphones being unplugged. `true` means playback
     * resumed by itself, `false` means it was paused. Called on the main thread.
     */
    fun setOnExternalStateChange(listener: ((isPlaying: Boolean) -> Unit)?)
}
