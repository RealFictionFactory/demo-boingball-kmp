package com.rff.boingballdemo.preferences

import com.rff.boingballdemo.component.OSStyle

data class PreferencesState(
    val themeColorIndex: Int = 1,
    val altColorIndex: Int = 3,
    val drawBorders: Boolean = true,
    val osStyle: OSStyle = OSStyle.AmigaOS13
)
