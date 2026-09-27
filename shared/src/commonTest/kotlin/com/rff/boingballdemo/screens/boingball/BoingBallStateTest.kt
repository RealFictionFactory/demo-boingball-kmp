package com.rff.boingballdemo.screens.boingball

import com.rff.boingballdemo.data.local.BoingBallPrefs
import com.rff.boingballdemo.ui.theme.AltAmigaOs13PickerColors
import com.rff.boingballdemo.ui.theme.DefaultAmigaOs13PickerColors
import kotlin.test.Test
import kotlin.test.assertEquals

class BoingBallStateTest {
    @Test
    fun mapsColorIndicesToPickerColors() {
        val state = BoingBallPrefs.App.toBoingBallState()
        assertEquals(DefaultAmigaOs13PickerColors[BoingBallPrefs.App.themeColorIndex], state.themeColor)
        assertEquals(AltAmigaOs13PickerColors[BoingBallPrefs.App.altColorIndex], state.altColor)
        assertEquals(BoingBallPrefs.App.drawBorders, state.drawBorders)
    }

    @Test
    fun outOfRangeColorIndicesFallBackToDefault() {
        val state = BoingBallPrefs.App.copy(themeColorIndex = 42, altColorIndex = -1).toBoingBallState()
        val default = BoingBallPrefs.Default.toBoingBallState()
        assertEquals(default.themeColor, state.themeColor)
        assertEquals(default.altColor, state.altColor)
    }
}
