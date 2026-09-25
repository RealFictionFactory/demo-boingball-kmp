package com.rff.boingballdemo.screens.boingball

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.ui.theme.AltAmigaOs13PickerColors
import com.rff.boingballdemo.ui.theme.DefaultAmigaOs13PickerColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

private const val DEFAULT_THEME_COLOR_INDEX = 1
private const val DEFAULT_ALT_COLOR_INDEX = 3

class BoingBallViewModel(
    private val settings: AppSettings,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoingBallState())
    val uiState = _uiState.asStateFlow()

    init {
        settings.boingBallPrefs
            .onEach { prefs ->
                val themeColorIndex = prefs.themeColorIndex.takeIf {
                    it in DefaultAmigaOs13PickerColors.indices
                } ?: DEFAULT_THEME_COLOR_INDEX.coerceIn(DefaultAmigaOs13PickerColors.indices)
                val altColorIndex = prefs.altColorIndex.takeIf {
                    it in AltAmigaOs13PickerColors.indices
                } ?: DEFAULT_ALT_COLOR_INDEX.coerceIn(AltAmigaOs13PickerColors.indices)

                _uiState.update {
                    it.copy(
                        themeColor = DefaultAmigaOs13PickerColors[themeColorIndex],
                        altColor = AltAmigaOs13PickerColors[altColorIndex],
                        drawBorders = prefs.drawBorders,
                        videoSystem = prefs.videoSystem,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
