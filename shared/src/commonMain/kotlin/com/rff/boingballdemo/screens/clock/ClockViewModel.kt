package com.rff.boingballdemo.screens.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.utils.stateInWhileSubscribed
import com.rff.boingballdemo.utils.toDateText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class ClockViewModel : ViewModel() {
    /** Emits the local time on every whole second. Runs only while the clock is shown. */
    private val localTime: Flow<LocalDateTime> = flow {
        while (true) {
            val now = Clock.System.now()
            emit(now.toLocalDateTime(TimeZone.currentSystemDefault()))
            delay((1000L - now.toEpochMilliseconds() % 1000L).milliseconds)
        }
    }

    val uiState: StateFlow<ClockState> = localTime.map { it.toClockState() }.stateInWhileSubscribed(
        viewModelScope,
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toClockState(),
    )
}

private fun LocalDateTime.toClockState() = ClockState(
    hour = hour,
    minute = minute,
    second = second,
    dateText = toDateText(),
)
