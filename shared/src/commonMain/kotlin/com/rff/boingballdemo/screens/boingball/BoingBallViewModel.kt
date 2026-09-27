package com.rff.boingballdemo.screens.boingball

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.data.local.BoingBallPrefs
import com.rff.boingballdemo.ui.theme.AltAmigaOs13PickerColors
import com.rff.boingballdemo.ui.theme.DefaultAmigaOs13PickerColors
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

private const val DEFAULT_THEME_COLOR_INDEX = 1
private const val DEFAULT_ALT_COLOR_INDEX = 3

class BoingBallViewModel(
    settings: AppSettings,
) : ViewModel() {
    val uiState: StateFlow<BoingBallState> = settings.boingBallPrefs
        .map { it.toBoingBallState() }
        .stateInWhileSubscribed(viewModelScope, BoingBallState())
}

private fun BoingBallPrefs.toBoingBallState(): BoingBallState {
    val themeColorIndex = themeColorIndex.takeIf {
        it in DefaultAmigaOs13PickerColors.indices
    } ?: DEFAULT_THEME_COLOR_INDEX.coerceIn(DefaultAmigaOs13PickerColors.indices)
    val altColorIndex = altColorIndex.takeIf {
        it in AltAmigaOs13PickerColors.indices
    } ?: DEFAULT_ALT_COLOR_INDEX.coerceIn(AltAmigaOs13PickerColors.indices)

    return BoingBallState(
        themeColor = DefaultAmigaOs13PickerColors[themeColorIndex],
        altColor = AltAmigaOs13PickerColors[altColorIndex],
        drawBorders = drawBorders,
        videoSystem = videoSystem,
    )
}
