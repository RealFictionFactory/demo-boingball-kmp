package com.rff.boingballdemo.screens.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.VideoSystem
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
    private val _uiState = MutableStateFlow(PreferencesState())
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
                        videoSystem = prefs.videoSystem,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: PreferencesAction) {
        when (action) {
            is PreferencesAction.ChangeThemeColor -> {
                _uiState.update { it.copy(themeColorIndex = action.index) }
                saveCurrentSettings()
            }
            is PreferencesAction.ChangeAltColor -> {
                _uiState.update { it.copy(altColorIndex = action.index) }
                saveCurrentSettings()
            }
            is PreferencesAction.ChangeFrameDraw -> {
                _uiState.update { it.copy(drawBorders = action.draw) }
                saveCurrentSettings()
            }
            is PreferencesAction.SetVideoSystem -> {
                _uiState.update { it.copy(videoSystem = action.videoSystem) }
                saveCurrentSettings()
            }
            PreferencesAction.BringDefaults -> {
                _uiState.update {
                    it.copy(
                        themeColorIndex = 0,
                        altColorIndex = 3,
                        drawBorders = false,
                        videoSystem = VideoSystem.NTSC,
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
                        videoSystem = VideoSystem.PAL,
                    )
                }
                saveCurrentSettings()
            }
            PreferencesAction.SetAmigaOS13 -> {
                setStyle(OSStyle.AmigaOS13)
            }
            PreferencesAction.SetAmigaOS20 -> {
                setStyle(OSStyle.AmigaOS20)
            }
            PreferencesAction.Back -> {
                // Back is handled by the enclosing navigation layer; keeping this
                // action as a safe no-op prevents crashes if the action is triggered
                // from a non-window context.
            }
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
                        videoSystem = _uiState.value.videoSystem,
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
