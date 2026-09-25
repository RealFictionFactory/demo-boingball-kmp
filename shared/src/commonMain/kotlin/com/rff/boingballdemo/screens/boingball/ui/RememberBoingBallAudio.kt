package com.rff.boingballdemo.screens.boingball.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.rff.boingballdemo.screens.boingball.audio.BoingBallAudioPlayer
import org.koin.compose.getKoin

@Composable
fun rememberBoingBallAudio(): BoingBallAudioPlayer? {
    if (LocalInspectionMode.current) return null
    val koin = getKoin()
    return remember(koin) { koin.get<BoingBallAudioPlayer>() }
}
