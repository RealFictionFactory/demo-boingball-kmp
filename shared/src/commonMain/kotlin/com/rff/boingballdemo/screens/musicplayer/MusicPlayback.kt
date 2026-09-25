package com.rff.boingballdemo.screens.musicplayer

interface MusicPlayback {
    fun play(resourcePath: String, positionMs: Long)
    fun pause()
    fun stop()
    fun seekTo(positionMs: Long)
    fun currentPositionMs(): Long
    fun hasEnded(): Boolean
    fun release()
}
