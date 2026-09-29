package com.rff.boingballdemo.screens.kefrens.ui

import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KefrensBarsTest {

    @Test
    fun paletteUsesAmigaTwelveBitColours() {
        val palette = kefrensPalette()
        assertEquals(1 + KEFRENS_COLOR_COUNT * KEFRENS_BAR_WIDTH, palette.size)
        for (color in palette) {
            for (channel in listOf(color.red, color.green, color.blue)) {
                val level = channel * 15f
                assertEquals(round(level), level, 0.01f, "channel $channel is not a 4-bit level")
            }
        }
    }

    @Test
    fun barsAreDarkAtTheEdgesAndBrightInTheMiddle() {
        val palette = kefrensPalette()
        fun lightness(index: Int) = palette[index].let { it.red + it.green + it.blue }
        repeat(KEFRENS_COLOR_COUNT) { color ->
            val first = 1 + color * KEFRENS_BAR_WIDTH
            val middle = first + KEFRENS_BAR_WIDTH / 2 - 1
            assertTrue(lightness(first) < lightness(middle))
            assertTrue(lightness(first + KEFRENS_BAR_WIDTH - 1) < lightness(middle))
        }
    }

    @Test
    fun firstRowShowsOnlyItsOwnBar() {
        val bars = KefrensBars()
        bars.update(0.7f)
        val left = bars.barPositions[0]
        for (column in 0 until bars.columns) {
            val cell = bars.cells[column]
            if (column in left until left + bars.barWidth) {
                assertTrue(cell != KEFRENS_BACKGROUND, "bar cell $column is empty")
            } else {
                assertEquals(KEFRENS_BACKGROUND, cell, "cell $column outside the bar is not empty")
            }
        }
    }

    @Test
    fun everyRowKeepsTheRowAboveExceptWhereItsBarLands() {
        val bars = KefrensBars()
        var time = 0f
        repeat(50) {
            bars.update(time)
            for (row in 1 until bars.rows) {
                val left = bars.barPositions[row]
                for (column in 0 until bars.columns) {
                    if (column in left until left + bars.barWidth) continue
                    assertEquals(
                        bars.cells[(row - 1) * bars.columns + column],
                        bars.cells[row * bars.columns + column],
                        "row $row changed outside its bar at column $column",
                    )
                }
            }
            time += 0.173f
        }
    }

    @Test
    fun barsStayOnScreenAndWithinThePalette() {
        val bars = KefrensBars()
        val paletteSize = kefrensPalette(bars.barWidth).size
        var time = 0f
        repeat(200) {
            bars.update(time)
            assertTrue(bars.barPositions.all { it in 0..bars.columns - bars.barWidth })
            assertTrue(bars.cells.all { it in 0 until paletteSize })
            time += 0.173f
        }
    }

    @Test
    fun barsSwingAcrossTheScreenAndMove() {
        val bars = KefrensBars()
        bars.update(0f)
        val spread = bars.barPositions.max() - bars.barPositions.min()
        assertTrue(spread > bars.columns / 2, "bars only spread over $spread columns")

        val before = bars.cells.copyOf()
        bars.update(0.5f)
        assertFalse(before.contentEquals(bars.cells), "bars did not move")
    }
}
