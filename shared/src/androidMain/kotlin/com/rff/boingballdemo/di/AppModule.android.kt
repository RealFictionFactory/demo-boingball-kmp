package com.rff.boingballdemo.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.rff.boingballdemo.audio.BoingBallAudioPlayer

actual val platformModule = module {
    singleOf(::BoingBallAudioPlayer)
}
