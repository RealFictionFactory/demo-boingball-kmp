package com.rff.boingballdemo.di

import com.rff.boingballdemo.audio.BoingBallAudioPlayer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::BoingBallAudioPlayer)
}
