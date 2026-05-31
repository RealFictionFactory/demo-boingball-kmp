package pl.chaoticorder.boingball.di

import pl.chaoticorder.boingball.audio.BoingBallAudioPlayer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::BoingBallAudioPlayer)
}
