package pl.chaoticorder.boingball.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import pl.chaoticorder.boingball.audio.BoingBallAudioPlayer

actual val platformModule = module {
    singleOf(::BoingBallAudioPlayer)
}
