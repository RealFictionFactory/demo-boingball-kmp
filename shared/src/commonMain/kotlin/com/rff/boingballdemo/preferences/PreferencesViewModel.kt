package com.rff.boingballdemo.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.data.local.BoingBallPrefs
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreferencesViewModel(
    private val settings: AppSettings,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        PreferencesState(appVersion = settings.getVersion())
    )
    val uiState: StateFlow<PreferencesState> = _uiState.asStateFlow()

    init {
        settings.boingBallPrefs
            .onEach { prefs ->
                _uiState.update {
                    it.copy(
                        themeColorIndex = prefs.themeColorIndex,
                        altColorIndex = prefs.altColorIndex,
                        drawBorders = prefs.drawBorders,
                        osStyle = prefs.osStyle,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: PreferencesAction) {
        when (action) {
            is PreferencesAction.ChangeThemeColor -> {
                _uiState.update { it.copy(themeColorIndex = action.index) }
            }
            is PreferencesAction.ChangeAltColor -> {
                _uiState.update { it.copy(altColorIndex = action.index) }
            }
            is PreferencesAction.ChangeFrameDraw -> {
                _uiState.update { it.copy(drawBorders = action.draw) }
            }
            PreferencesAction.BringDefaults -> {
                _uiState.update {
                    it.copy(
                        themeColorIndex = 0,
                        altColorIndex = 3,
                        drawBorders = false,
                    )
                }
                saveCurrentSettings()
            }
            PreferencesAction.BringAppDefaults -> {
                _uiState.update {
                    it.copy(
                        themeColorIndex = 1,
                        altColorIndex = 3,
                        drawBorders = true,
                        osStyle = OSStyle.AmigaOS13,
                    )
                }
                saveCurrentSettings()
            }
            PreferencesAction.SaveSettings -> {
                saveCurrentSettings()
            }
            PreferencesAction.SetAmigaOS13 -> {
                setStyle(OSStyle.AmigaOS13)
            }
            PreferencesAction.SetAmigaOS20 -> {
                setStyle(OSStyle.AmigaOS20)
            }
            PreferencesAction.Back -> TODO()
        }
    }

    private fun saveCurrentSettings() {
        viewModelScope.launch {
            // Use NonCancellable to ensure preferences are saved even if ViewModel is cleared
            withContext(NonCancellable) {
                settings.saveBoingBallPrefs(
                    BoingBallPrefs(
                        themeColorIndex = _uiState.value.themeColorIndex,
                        altColorIndex = _uiState.value.altColorIndex,
                        drawBorders = _uiState.value.drawBorders,
                        osStyle = _uiState.value.osStyle,
                    )
                )
            }
        }
    }

    private fun setStyle(style: OSStyle) {
        _uiState.update { it.copy(osStyle = style) }
        saveCurrentSettings()
    }
}
