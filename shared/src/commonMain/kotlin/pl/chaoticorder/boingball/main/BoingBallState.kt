package pl.chaoticorder.boingball.main

import androidx.compose.ui.graphics.Color
import pl.chaoticorder.boingball.component.OSStyle
import pl.chaoticorder.boingball.ui.theme.amigaOs13Blue
import pl.chaoticorder.boingball.ui.theme.whiteColor

data class BoingBallState(
    val themeColor: Color = amigaOs13Blue,
    val osStyle: OSStyle = OSStyle.AmigaOS13,
    val altColor: Color = whiteColor,
    val drawBorders: Boolean = true,
)
