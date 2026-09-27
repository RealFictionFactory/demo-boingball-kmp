package com.rff.boingballdemo.screens.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.data.local.BoingBallPrefs
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** Reads and writes DataStore directly; the stored prefs are the only source of truth. */
class PreferencesViewModel(
    private val settings: AppSettings,
) : ViewModel() {
    val uiState: StateFlow<PreferencesState> = settings.boingBallPrefs
        .map { it.toPreferencesState() }
        .stateInWhileSubscribed(viewModelScope, PreferencesState())

    fun onAction(action: PreferencesAction) {
        when (action) {
            is PreferencesAction.ChangeThemeColor -> update { it.copy(themeColorIndex = action.index) }
            is PreferencesAction.ChangeAltColor -> update { it.copy(altColorIndex = action.index) }
            is PreferencesAction.ChangeFrameDraw -> update { it.copy(drawBorders = action.draw) }
            is PreferencesAction.SetVideoSystem -> update { it.copy(videoSystem = action.videoSystem) }
            PreferencesAction.BringDefaults -> update { it.withDemoDefaults() }
            PreferencesAction.BringAppDefaults -> update { BoingBallPrefs.App }
            PreferencesAction.SetAmigaOS13 -> update { it.copy(osStyle = OSStyle.AmigaOS13) }
            PreferencesAction.SetAmigaOS20 -> update { it.copy(osStyle = OSStyle.AmigaOS20) }
        }
    }

    private fun update(transform: (BoingBallPrefs) -> BoingBallPrefs) {
        // UNDISPATCHED hands the write to the app scope before this call returns, so
        // closing the window right after a tap can no longer drop the change.
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            settings.updateBoingBallPrefs(transform)
        }
    }
}
