package com.rff.boingballdemo.screens.boingball

import androidx.compose.ui.graphics.Color
import com.rff.boingballdemo.component.VideoSystem
import com.rff.boingballdemo.data.local.BoingBallPrefs
import com.rff.boingballdemo.ui.theme.AltAmigaOs13PickerColors
import com.rff.boingballdemo.ui.theme.DefaultAmigaOs13PickerColors

data class BoingBallState(
    val themeColor: Color,
    val altColor: Color,
    val drawBorders: Boolean,
    val videoSystem: VideoSystem,
)

/** Maps stored prefs to colors; an out-of-range color index falls back to [BoingBallPrefs.Default]. */
fun BoingBallPrefs.toBoingBallState(): BoingBallState {
    val default = BoingBallPrefs.Default
    val themeColorIndex = themeColorIndex.takeIf { it in DefaultAmigaOs13PickerColors.indices }
        ?: default.themeColorIndex
    val altColorIndex = altColorIndex.takeIf { it in AltAmigaOs13PickerColors.indices }
        ?: default.altColorIndex

    return BoingBallState(
        themeColor = DefaultAmigaOs13PickerColors[themeColorIndex],
        altColor = AltAmigaOs13PickerColors[altColorIndex],
        drawBorders = drawBorders,
        videoSystem = videoSystem,
    )
}
