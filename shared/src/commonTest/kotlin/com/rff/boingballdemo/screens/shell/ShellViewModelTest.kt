package com.rff.boingballdemo.screens.shell

import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.testing.InMemoryDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class ShellViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val settings = AppSettings(InMemoryDataStore())

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsWithBannerAndPromptForStoredStyle() = runTest(dispatcher) {
        val viewModel = ShellViewModel(settings)
        val collector = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(bannerFor(OSStyle.AmigaOS13), state.lines)
        assertEquals(promptFor(OSStyle.AmigaOS13), state.currentLine)
        collector.cancel()
    }

    @Test
    fun styleChangeResetsSessionAndCancelsRunningCommand() = runTest(dispatcher) {
        val viewModel = ShellViewModel(settings)
        val collector = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onTap()
        settings.updateBoingBallPrefs { it.copy(osStyle = OSStyle.AmigaOS20) }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(bannerFor(OSStyle.AmigaOS20), state.lines)
        assertEquals(promptFor(OSStyle.AmigaOS20), state.currentLine)
        assertFalse(state.isBusy)
        collector.cancel()
    }
}
