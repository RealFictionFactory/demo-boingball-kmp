package com.rff.boingballdemo.screens.musicplayer

sealed interface MusicPlayerAction {
    data object Play : MusicPlayerAction
    data object Pause : MusicPlayerAction
    data object Stop : MusicPlayerAction
    data object Next : MusicPlayerAction
    data object Previous : MusicPlayerAction
    data class Seek(val deltaMs: Long) : MusicPlayerAction
    data class SelectTrack(val index: Int) : MusicPlayerAction
}
