package com.rff.boingballdemo.screens.preferences

import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.VideoSystem
import com.rff.boingballdemo.data.local.BoingBallPrefs

data class PreferencesState(
    val themeColorIndex: Int = BoingBallPrefs.Default.themeColorIndex,
    val altColorIndex: Int = BoingBallPrefs.Default.altColorIndex,
    val drawBorders: Boolean = BoingBallPrefs.Default.drawBorders,
    val osStyle: OSStyle = BoingBallPrefs.Default.osStyle,
    val videoSystem: VideoSystem = BoingBallPrefs.Default.videoSystem,
)

fun BoingBallPrefs.toPreferencesState() = PreferencesState(
    themeColorIndex = themeColorIndex,
    altColorIndex = altColorIndex,
    drawBorders = drawBorders,
    osStyle = osStyle,
    videoSystem = videoSystem,
)
