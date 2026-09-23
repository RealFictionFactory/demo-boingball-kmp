package com.rff.boingballdemo.di

import com.rff.boingballdemo.audio.BoingBallAudioPlayer
import com.rff.boingballdemo.musicplayer.AndroidMusicPlayback
import com.rff.boingballdemo.musicplayer.MusicPlayback
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::BoingBallAudioPlayer)
    factory<MusicPlayback> { AndroidMusicPlayback(get()) }
}
