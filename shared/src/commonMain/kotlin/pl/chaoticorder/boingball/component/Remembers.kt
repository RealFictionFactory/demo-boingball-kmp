package pl.chaoticorder.boingball.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import pl.chaoticorder.boingball.audio.BoingBallAudioPlayer
import org.koin.compose.getKoin

@Composable
fun rememberBoingBallAudio(): BoingBallAudioPlayer {
    val koin = getKoin()
    return remember(koin) { koin.get<BoingBallAudioPlayer>() }
}
