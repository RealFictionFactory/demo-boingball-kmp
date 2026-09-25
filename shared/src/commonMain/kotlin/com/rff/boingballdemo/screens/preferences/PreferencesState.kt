package com.rff.boingballdemo.screens.preferences

import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.VideoSystem

data class PreferencesState(
    val themeColorIndex: Int = 0,
    val altColorIndex: Int = 3,
    val drawBorders: Boolean = false,
    val osStyle: OSStyle = OSStyle.AmigaOS13,
    val videoSystem: VideoSystem = VideoSystem.PAL,
)
