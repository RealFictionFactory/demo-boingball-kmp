package com.rff.boingballdemo.screens.sinescroll.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SineScrollTest {
    private val viewWidth = 320f
    private val textWidth = 1_000f

    @Test
    fun textEntersFromTheRightEdge() {
        assertEquals(viewWidth, sineScrollTextLeft(scrolled = 0f, textWidth, viewWidth))
        assertEquals(viewWidth - 50f, sineScrollTextLeft(scrolled = 50f, textWidth, viewWidth))
    }

    @Test
    fun textStartsOverOnceItHasFullyLeft() {
        val loop = textWidth + viewWidth
        // Just before the loop ends, the text's right edge is at the left edge of the view.
        assertEquals(-textWidth, sineScrollTextLeft(scrolled = loop - 0.001f, textWidth, viewWidth), 0.01f)
        assertEquals(viewWidth, sineScrollTextLeft(scrolled = loop, textWidth, viewWidth))
        assertEquals(viewWidth - 10f, sineScrollTextLeft(scrolled = 3 * loop + 10f, textWidth, viewWidth), 0.01f)
    }

    @Test
    fun waveStaysWithinUnitRange() {
        var t = 0f
        while (t < 30f) {
            var x = 0f
            while (x <= 320f) {
                val wave = sineScrollWave(x, t)
                assertTrue(wave in -1f..1f, "wave $wave out of range at x=$x t=$t")
                x += 7f
            }
            t += 0.37f
        }
    }

    @Test
    fun waveMovesOverTime() {
        assertTrue(sineScrollWave(100f, 0f) != sineScrollWave(100f, 0.5f))
    }

    @Test
    fun textMapsEveryCharacterToItsGlyph() {
        val text = SineScrollText("AMIGA AMIGA")
        assertEquals("AMIG ", text.glyphs)
        assertEquals(11, text.length)
        for (i in 0 until text.length) {
            assertEquals("AMIGA AMIGA"[i], text.glyphs[text.cells[i]])
        }
    }

    @Test
    fun messageLoopsWithAGap() {
        assertTrue(SineScrollMessage.endsWith("          "))
        assertTrue(SineScrollMessage.startsWith("HELLO"))
    }
}
