package com.rff.boingballdemo.preferences

sealed interface PreferencesAction {
    data class ChangeThemeColor(val index: Int) : PreferencesAction
    data class ChangeAltColor(val index: Int) : PreferencesAction
    data class ChangeFrameDraw(val draw: Boolean) : PreferencesAction
    data object BringDefaults : PreferencesAction
    data object BringAppDefaults : PreferencesAction
    data object SaveSettings : PreferencesAction
    data object SetAmigaOS13 : PreferencesAction
    data object SetAmigaOS20 : PreferencesAction
}
