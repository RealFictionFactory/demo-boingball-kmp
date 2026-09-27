package com.rff.boingballdemo.screens.copper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class CopperBarsViewModel(
    settings: AppSettings
) : ViewModel() {
    val uiState: StateFlow<CopperBarsState> = settings.osStyle
        .map { CopperBarsState(osStyle = it) }
        .stateInWhileSubscribed(viewModelScope, CopperBarsState())
}
