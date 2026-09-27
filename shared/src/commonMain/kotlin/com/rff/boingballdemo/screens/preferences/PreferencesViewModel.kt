package com.rff.boingballdemo.screens.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.VideoSystem
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.data.local.BoingBallPrefs
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Reads and writes DataStore directly; the stored prefs are the only source of truth. */
class PreferencesViewModel(
    private val settings: AppSettings,
) : ViewModel() {
    val uiState: StateFlow<PreferencesState> = settings.boingBallPrefs
        .map { prefs ->
            PreferencesState(
                themeColorIndex = prefs.themeColorIndex,
                altColorIndex = prefs.altColorIndex,
                drawBorders = prefs.drawBorders,
                osStyle = prefs.osStyle,
                videoSystem = prefs.videoSystem,
            )
        }
        .stateInWhileSubscribed(viewModelScope, PreferencesState())

    fun onAction(action: PreferencesAction) {
        when (action) {
            is PreferencesAction.ChangeThemeColor -> update { it.copy(themeColorIndex = action.index) }
            is PreferencesAction.ChangeAltColor -> update { it.copy(altColorIndex = action.index) }
            is PreferencesAction.ChangeFrameDraw -> update { it.copy(drawBorders = action.draw) }
            is PreferencesAction.SetVideoSystem -> update { it.copy(videoSystem = action.videoSystem) }
            PreferencesAction.BringDefaults -> update {
                it.copy(
                    themeColorIndex = 0,
                    altColorIndex = 3,
                    drawBorders = false,
                    videoSystem = VideoSystem.PAL,
                )
            }
            PreferencesAction.BringAppDefaults -> update {
                it.copy(
                    themeColorIndex = 1,
                    altColorIndex = 3,
                    drawBorders = true,
                    osStyle = OSStyle.AmigaOS13,
                    videoSystem = VideoSystem.PAL,
                )
            }
            PreferencesAction.SetAmigaOS13 -> update { it.copy(osStyle = OSStyle.AmigaOS13) }
            PreferencesAction.SetAmigaOS20 -> update { it.copy(osStyle = OSStyle.AmigaOS20) }
            PreferencesAction.Back -> {
                // Back is handled by the enclosing navigation layer; keeping this
                // action as a safe no-op prevents crashes if the action is triggered
                // from a non-window context.
            }
        }
    }

    private fun update(transform: (BoingBallPrefs) -> BoingBallPrefs) {
        viewModelScope.launch {
            // Use NonCancellable to ensure preferences are saved even if ViewModel is cleared
            withContext(NonCancellable) {
                settings.updateBoingBallPrefs(transform)
            }
        }
    }
}
