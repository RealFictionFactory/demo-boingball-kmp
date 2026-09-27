package com.rff.boingballdemo.screens.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class AboutViewModel(
    settings: AppSettings,
) : ViewModel() {
    private val initialState = AboutState(appVersion = settings.getVersion())

    val uiState: StateFlow<AboutState> = settings.osStyle
        .map { initialState.copy(osStyle = it) }
        .stateInWhileSubscribed(viewModelScope, initialState)
}
