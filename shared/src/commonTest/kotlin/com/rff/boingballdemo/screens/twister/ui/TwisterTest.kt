package com.rff.boingballdemo.screens.twister.ui

import kotlin.math.abs
import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TwisterTest {

    @Test
    fun paletteUsesAmigaTwelveBitColours() {
        val palette = twisterPalette()
        assertEquals(TWISTER_FACES * TWISTER_SHADES, palette.size)
        for (color in palette) {
            for (channel in listOf(color.red, color.green, color.blue)) {
                val level = channel * 15f
                assertEquals(round(level), level, 0.01f, "channel $channel is not a 4-bit level")
            }
        }
    }

    @Test
    fun paletteGetsBrighterWithEveryShade() {
        val palette = twisterPalette()
        repeat(TWISTER_FACES) { face ->
            val shades = palette.subList(face * TWISTER_SHADES, (face + 1) * TWISTER_SHADES)
            shades.zipWithNext { darker, brighter ->
                assertTrue(darker.red + darker.green + darker.blue <= brighter.red + brighter.green + brighter.blue)
            }
        }
    }

    @Test
    fun everyRowShowsOneOrTwoFacesCoveringTheColumn() {
        val twister = Twister()
        var time = 0f
        repeat(200) {
            twister.update(time)
            for (row in 0 until twister.rows) {
                val first = row * TWISTER_FACES
                val corners = List(TWISTER_FACES) { twister.edges[first + it] }
                var visible = 0
                var coveredWidth = 0f
                for (face in 0 until TWISTER_FACES) {
                    val shade = twister.shades[first + face]
                    if (shade < 0) continue
                    assertTrue(shade in 0 until twister.shadeCount)
                    visible++
                    coveredWidth += corners[(face + 1) % TWISTER_FACES] - corners[face]
                }
                assertTrue(visible in 1..2, "row $row shows $visible faces")
                // Visible faces sit side by side and span the whole silhouette, with no gaps.
                assertEquals(corners.max() - corners.min(), coveredWidth, 0.001f)
            }
            time += 0.173f
        }
    }

    @Test
    fun columnStaysOnScreen() {
        val twister = Twister()
        var time = 0f
        repeat(200) {
            twister.update(time)
            for (row in 0 until twister.rows) {
                for (corner in 0 until TWISTER_FACES) {
                    val x = twister.centers[row] + twister.edges[row * TWISTER_FACES + corner]
                    // Half-width is 0.17 of the view, so the screen edge is at about +/-2.9 units.
                    assertTrue(abs(x) < 2f, "row $row reaches $x")
                }
            }
            time += 0.173f
        }
    }

    @Test
    fun twisterTwistsAndMoves() {
        val twister = Twister()
        twister.update(1f)
        val before = twister.edges.copyOf()
        val top = before.copyOfRange(0, TWISTER_FACES)
        val bottom = before.copyOfRange(before.size - TWISTER_FACES, before.size)
        assertFalse(top.contentEquals(bottom), "column is not twisted")

        twister.update(1.5f)
        assertFalse(before.contentEquals(twister.edges), "twister did not move")
    }
}
