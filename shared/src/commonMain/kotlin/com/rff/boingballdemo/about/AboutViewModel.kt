package com.rff.boingballdemo.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class AboutViewModel(
    private val settings: AppSettings,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AboutState(appVersion = settings.getVersion())
    )
    val uiState: StateFlow<AboutState> = _uiState.asStateFlow()

    init {
        settings.boingBallPrefs
            .onEach { prefs ->
                _uiState.update { it.copy(osStyle = prefs.osStyle) }
            }
            .launchIn(viewModelScope)
    }
}
