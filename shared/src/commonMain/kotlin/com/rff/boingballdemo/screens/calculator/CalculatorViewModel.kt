package com.rff.boingballdemo.screens.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class CalculatorViewModel(
    settings: AppSettings
) : ViewModel() {
    private val calculator = MutableStateFlow(CalculatorState())

    val uiState: StateFlow<CalculatorState> = combine(calculator, settings.osStyle) { state, osStyle ->
        state.copy(osStyle = osStyle)
    }.stateInWhileSubscribed(viewModelScope, CalculatorState())

    fun onAction(action: CalculatorAction) {
        calculator.update { it.reduce(action) }
    }
}
