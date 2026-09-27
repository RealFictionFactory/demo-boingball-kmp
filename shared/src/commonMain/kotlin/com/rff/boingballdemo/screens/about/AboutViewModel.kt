package com.rff.boingballdemo.screens.about

import androidx.lifecycle.ViewModel
import com.rff.boingballdemo.data.local.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AboutViewModel(
    settings: AppSettings,
) : ViewModel() {
    val uiState: StateFlow<AboutState> =
        MutableStateFlow(AboutState(appVersion = settings.getVersion())).asStateFlow()
}
