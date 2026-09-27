package com.rff.boingballdemo.screens.clock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ClockViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // The clock reads the real system time, so only ranges and formatting are checked.
    @Test
    fun publishesAValidTimeWhileCollected() = runTest(dispatcher) {
        val viewModel = ClockViewModel()
        val collector = launch { viewModel.uiState.collect {} }
        runCurrent()
        advanceTimeBy(2_000L)

        val state = viewModel.uiState.value
        assertTrue(state.hour in 0..23)
        assertTrue(state.minute in 0..59)
        assertTrue(state.second in 0..59)
        assertTrue(state.dateText.isNotBlank())
        collector.cancel()
    }
}
