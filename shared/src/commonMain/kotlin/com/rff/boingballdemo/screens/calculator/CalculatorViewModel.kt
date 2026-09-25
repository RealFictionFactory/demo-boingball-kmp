package com.rff.boingballdemo.screens.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class CalculatorViewModel(
    private val settings: AppSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    init {
        settings.boingBallPrefs
            .onEach { prefs -> _uiState.update { it.copy(osStyle = prefs.osStyle) } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: CalculatorAction) {
        _uiState.update { it.reduce(action) }
    }
}
