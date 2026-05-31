package com.rff.boingballdemo.audio

internal const val BOUNCE_AUDIO_FILE_PATH = "files/boing.wav"

expect class BoingBallAudioPlayer {
    fun play()
    fun playLeft()
    fun playRight()
    fun release()
}
