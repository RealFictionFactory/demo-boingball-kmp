package com.rff.boingballdemo.screens.workbench

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class WorkbenchViewModel(
    settings: AppSettings
) : ViewModel() {
    val uiState: StateFlow<WorkbenchState> = settings.osStyle
        .map { WorkbenchState(osStyle = it) }
        .stateInWhileSubscribed(viewModelScope, WorkbenchState())
}
