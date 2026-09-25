package com.rff.boingballdemo.di

import com.rff.boingballdemo.screens.boingball.audio.BoingBallAudioPlayer
import com.rff.boingballdemo.screens.musicplayer.IosMusicPlayback
import com.rff.boingballdemo.screens.musicplayer.MusicPlayback
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::BoingBallAudioPlayer)
    single<MusicPlayback> { IosMusicPlayback() }
}
