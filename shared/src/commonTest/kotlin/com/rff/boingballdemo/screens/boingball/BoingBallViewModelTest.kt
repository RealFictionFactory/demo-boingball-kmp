package com.rff.boingballdemo.screens.boingball

import com.rff.boingballdemo.component.VideoSystem
import com.rff.boingballdemo.data.local.BoingBallPrefs
import com.rff.boingballdemo.testing.testAppSettings
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

@OptIn(ExperimentalCoroutinesApi::class)
class BoingBallViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val settings = testAppSettings(dispatcher)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsWithDefaultLook() {
        assertEquals(BoingBallPrefs.Default.toBoingBallState(), BoingBallViewModel(settings).uiState.value)
    }

    @Test
    fun followsStoredPrefs() = runTest(dispatcher) {
        val viewModel = BoingBallViewModel(settings)
        val collector = launch { viewModel.uiState.collect {} }

        settings.updateBoingBallPrefs { BoingBallPrefs.App.copy(videoSystem = VideoSystem.NTSC) }
        advanceUntilIdle()

        assertEquals(
            BoingBallPrefs.App.copy(videoSystem = VideoSystem.NTSC).toBoingBallState(),
            viewModel.uiState.value,
        )
        collector.cancel()
    }
}
