package com.rff.boingballdemo.screens.plasma.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlasmaTest {

    @Test
    fun paletteUsesAmigaTwelveBitColours() {
        val palette = plasmaPalette()
        assertEquals(PLASMA_PALETTE_SIZE, palette.size)
        for (color in palette) {
            for (channel in listOf(color.red, color.green, color.blue)) {
                val level = channel * 15f
                assertEquals(kotlin.math.round(level), level, 0.01f, "channel $channel is not a 4-bit level")
            }
        }
    }

    @Test
    fun paletteLoopsSmoothly() {
        val palette = plasmaPalette()
        // Colour cycling wraps from the last entry to the first: that step must be as small as any other.
        val first = palette.first()
        val last = palette.last()
        assertTrue(kotlin.math.abs(first.red - last.red) <= 2f / 15f)
        assertTrue(kotlin.math.abs(first.green - last.green) <= 2f / 15f)
        assertTrue(kotlin.math.abs(first.blue - last.blue) <= 2f / 15f)
    }

    @Test
    fun cellsStayWithinThePalette() {
        val plasma = Plasma()
        var time = 0f
        repeat(200) {
            plasma.update(time)
            assertTrue(plasma.cells.all { it in 0 until plasma.paletteSize })
            time += 0.173f
        }
    }

    @Test
    fun plasmaUsesManyColoursAndMoves() {
        val plasma = Plasma()
        plasma.update(0f)
        val before = plasma.cells.copyOf()
        assertTrue(before.toSet().size > plasma.paletteSize / 2, "plasma is too flat")

        plasma.update(0.5f)
        assertFalse(before.contentEquals(plasma.cells), "plasma did not move")
    }
}
