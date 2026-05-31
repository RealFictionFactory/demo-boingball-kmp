package com.rff.boingballdemo.main

import androidx.compose.ui.graphics.Color
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.whiteColor

data class BoingBallState(
    val themeColor: Color = amigaOs13Blue,
    val osStyle: OSStyle = OSStyle.AmigaOS13,
    val altColor: Color = whiteColor,
    val drawBorders: Boolean = true,
)
