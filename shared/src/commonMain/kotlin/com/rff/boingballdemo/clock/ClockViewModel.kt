package com.rff.boingballdemo.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ClockViewModel(private val settings: AppSettings) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<ClockState> = _uiState.asStateFlow()

    init {
        settings.boingBallPrefs
            .onEach { prefs -> _uiState.update { it.copy(osStyle = prefs.osStyle) } }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            while (true) {
                val now = Clock.System.now()
                val local = now.toLocalDateTime(TimeZone.currentSystemDefault())
                _uiState.update {
                    it.copy(
                        hour = local.hour,
                        minute = local.minute,
                        second = local.second,
                        dateText = local.toDateText(),
                    )
                }
                delay(1000L - now.toEpochMilliseconds() % 1000L)
            }
        }
    }

    private fun initialState(): ClockState {
        val local = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return ClockState(
            hour = local.hour,
            minute = local.minute,
            second = local.second,
            dateText = local.toDateText(),
        )
    }
}

private fun LocalDateTime.toDateText(): String {
    val dd = dayOfMonth.toString().padStart(2, '0')
    val mm = monthNumber.toString().padStart(2, '0')
    return "$dd.$mm.$year"
}
