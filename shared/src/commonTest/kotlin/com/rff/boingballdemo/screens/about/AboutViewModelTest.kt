package com.rff.boingballdemo.screens.about

import com.rff.boingballdemo.VersionConfig
import com.rff.boingballdemo.testing.testAppSettings
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

class AboutViewModelTest {
    @Test
    fun showsGeneratedAppVersion() {
        val viewModel = AboutViewModel(testAppSettings(StandardTestDispatcher()))
        assertEquals(VersionConfig.VERSION_NAME, viewModel.uiState.value.appVersion)
    }
}
