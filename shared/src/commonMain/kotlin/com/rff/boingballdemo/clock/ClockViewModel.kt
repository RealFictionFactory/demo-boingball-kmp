package com.rff.boingballdemo.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.utils.toDateText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class ClockViewModel(
    private val settings: AppSettings
) : ViewModel() {
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
                delay((1000L - now.toEpochMilliseconds() % 1000L).milliseconds)
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
