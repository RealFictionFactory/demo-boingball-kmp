package com.rff.boingballdemo.screens.boingball

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.data.local.BoingBallPrefs
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class BoingBallViewModel(
    settings: AppSettings,
) : ViewModel() {
    val uiState: StateFlow<BoingBallState> = settings.boingBallPrefs
        .map { it.toBoingBallState() }
        .stateInWhileSubscribed(viewModelScope, BoingBallPrefs.Default.toBoingBallState())
}
