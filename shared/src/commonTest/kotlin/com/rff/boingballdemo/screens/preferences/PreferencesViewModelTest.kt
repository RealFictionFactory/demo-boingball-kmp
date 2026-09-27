package com.rff.boingballdemo.screens.preferences

import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.VideoSystem
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.data.local.BoingBallPrefs
import com.rff.boingballdemo.testing.InMemoryDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class PreferencesViewModelTest {
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
    fun quickSuccessiveChangesAreAllKept() = runTest(dispatcher) {
        val viewModel = PreferencesViewModel(settings)

        // Issued before any write completes, as with fast taps.
        viewModel.onAction(PreferencesAction.ChangeThemeColor(2))
        viewModel.onAction(PreferencesAction.ChangeFrameDraw(true))
        viewModel.onAction(PreferencesAction.SetAmigaOS20)
        viewModel.onAction(PreferencesAction.SetVideoSystem(VideoSystem.NTSC))
        advanceUntilIdle()

        val prefs = settings.boingBallPrefs.first()
        assertEquals(2, prefs.themeColorIndex)
        assertEquals(true, prefs.drawBorders)
        assertEquals(OSStyle.AmigaOS20, prefs.osStyle)
        assertEquals(VideoSystem.NTSC, prefs.videoSystem)
    }

    @Test
    fun uiStateReflectsStoredPrefs() = runTest(dispatcher) {
        val viewModel = PreferencesViewModel(settings)
        val collector = launch { viewModel.uiState.collect {} }

        viewModel.onAction(PreferencesAction.ChangeAltColor(1))
        viewModel.onAction(PreferencesAction.SetAmigaOS20)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.altColorIndex)
        assertEquals(OSStyle.AmigaOS20, viewModel.uiState.value.osStyle)
        collector.cancel()
    }

    @Test
    fun startsWithDefaultPrefsWhenNothingIsStored() = runTest(dispatcher) {
        assertEquals(BoingBallPrefs.Default, settings.boingBallPrefs.first())
        assertEquals(BoingBallPrefs.Default.toPreferencesState(), PreferencesViewModel(settings).uiState.value)
    }

    @Test
    fun appDefaultsReplaceEverySetting() = runTest(dispatcher) {
        val viewModel = PreferencesViewModel(settings)
        viewModel.onAction(PreferencesAction.SetAmigaOS20)
        viewModel.onAction(PreferencesAction.SetVideoSystem(VideoSystem.NTSC))
        viewModel.onAction(PreferencesAction.BringAppDefaults)
        advanceUntilIdle()

        assertEquals(BoingBallPrefs.App, settings.boingBallPrefs.first())
    }

    @Test
    fun demoDefaultsKeepOsStyle() = runTest(dispatcher) {
        val viewModel = PreferencesViewModel(settings)
        viewModel.onAction(PreferencesAction.SetAmigaOS20)
        viewModel.onAction(PreferencesAction.ChangeThemeColor(2))
        viewModel.onAction(PreferencesAction.BringDefaults)
        advanceUntilIdle()

        assertEquals(
            BoingBallPrefs.Default.copy(osStyle = OSStyle.AmigaOS20),
            settings.boingBallPrefs.first(),
        )
    }
}
