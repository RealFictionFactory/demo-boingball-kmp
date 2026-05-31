package pl.chaoticorder.boingball.preferences

import pl.chaoticorder.boingball.component.OSStyle

data class PreferencesState(
    val themeColorIndex: Int = 1,
    val altColorIndex: Int = 3,
    val drawBorders: Boolean = true,
    val appVersion: String = "",
    val osStyle: OSStyle = OSStyle.AmigaOS13
)
