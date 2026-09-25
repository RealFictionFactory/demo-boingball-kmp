package com.rff.boingballdemo.screens.boingball

import androidx.compose.ui.graphics.Color
import com.rff.boingballdemo.component.VideoSystem
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.whiteColor

data class BoingBallState(
    val themeColor: Color = amigaOs13Blue,
    val altColor: Color = whiteColor,
    val drawBorders: Boolean = true,
    val videoSystem: VideoSystem = VideoSystem.PAL,
)
