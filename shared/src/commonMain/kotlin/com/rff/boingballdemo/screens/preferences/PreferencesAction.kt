package com.rff.boingballdemo.screens.preferences

import com.rff.boingballdemo.component.VideoSystem

sealed interface PreferencesAction {
    data class ChangeThemeColor(val index: Int) : PreferencesAction
    data class ChangeAltColor(val index: Int) : PreferencesAction
    data class ChangeFrameDraw(val draw: Boolean) : PreferencesAction
    data class SetVideoSystem(val videoSystem: VideoSystem) : PreferencesAction
    data object BringDefaults : PreferencesAction
    data object BringAppDefaults : PreferencesAction
    data object SetAmigaOS13 : PreferencesAction
    data object SetAmigaOS20 : PreferencesAction
    data object Back : PreferencesAction
}
