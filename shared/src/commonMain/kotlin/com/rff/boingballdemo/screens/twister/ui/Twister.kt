package com.rff.boingballdemo.screens.twister.ui

import androidx.compose.ui.graphics.Color
import com.rff.boingballdemo.utils.TAU
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/** Default number of scanlines the twister is built from: chunky, like a 240-line screen in 2-pixel rows. */
internal const val TWISTER_ROWS = 120

/** A twister is a square column, so every scanline cuts through four faces. */
internal const val TWISTER_FACES = 4

/** Number of shades each face colour is quantised to, from dark (side-on) to full (facing the viewer). */
internal const val TWISTER_SHADES = 16

/** Width of a face seen exactly front-on, with the column's half-width as the unit. */
private val FACE_FULL_WIDTH = sqrt(2f)

/** Base colours of the four faces, going round the column. */
private val TwisterFaceColors = listOf(
    Color(0xFFFF2200),
    Color(0xFFFFCC00),
    Color(0xFF00CC33),
    Color(0xFF00AAFF),
)

/**
 * Shaded colours of every face, face by face: entry `face * shades + shade`. Shade 0 is
 * nearly black, the last one is the face colour with a white highlight, all rounded to
 * the Amiga OCS 12-bit colour space (4 bits per channel).
 */
internal fun twisterPalette(shades: Int = TWISTER_SHADES): List<Color> =
    TwisterFaceColors.flatMap { base ->
        List(shades) { shade ->
            val light = (shade + 1f) / shades
            fun channel(value: Float): Float {
                // Mostly the lit base colour, washing towards white only near full light.
                val lit = value * light + (1f - value) * maxOf(0f, light - 0.8f) * 2.5f
                return (lit.coerceIn(0f, 1f) * 15f).roundToInt() / 15f
            }
            Color(red = channel(base.red), green = channel(base.green), blue = channel(base.blue))
        }
    }

/**
 * The classic demo twister: a square column spinning around its vertical axis, with the
 * rotation angle changing from scanline to scanline so the column twists, untwists and
 * sways. Every scanline is the column's cross-section, a rotated square, seen edge-on.
 *
 * Call [update] once per frame, then read [edges], [centers] and [shades]; nothing is
 * allocated per frame. Positions are in units of the column's half-width, with 0 at the
 * middle of the screen.
 */
internal class Twister(
    val rows: Int = TWISTER_ROWS,
    val shadeCount: Int = TWISTER_SHADES,
) {
    /** Horizontal position of each of the four corners, row by row, relative to [centers]. */
    val edges = FloatArray(rows * TWISTER_FACES)

    /** Horizontal sway of every row. */
    val centers = FloatArray(rows)

    /**
     * Shade of every face, row by row: the face between corner `i` and corner `i + 1` is
     * visible only when it has a shade in `0 until shadeCount`, otherwise it is -1
     * (it faces away from the viewer).
     */
    val shades = IntArray(rows * TWISTER_FACES)

    fun update(timeSeconds: Float) {
        // How far the column twists along its height, swinging between left and right twists.
        val twist = 2.4f * sin(timeSeconds * 0.45f)
        val spin = timeSeconds * 1.2f
        for (row in 0 until rows) {
            val height = row.toFloat() / rows
            val angle = spin +
                twist * sin(height * TAU * 0.5f + timeSeconds * 0.8f) +
                0.6f * sin(height * TAU * 1.5f - timeSeconds * 1.1f)
            centers[row] = 0.9f * sin(height * TAU + timeSeconds * 1.3f)

            val first = row * TWISTER_FACES
            for (corner in 0 until TWISTER_FACES) {
                edges[first + corner] = sin(angle + corner * TAU / TWISTER_FACES)
            }
            for (face in 0 until TWISTER_FACES) {
                val left = edges[first + face]
                val right = edges[first + (face + 1) % TWISTER_FACES]
                // A face points at the viewer when its corners come left to right; the
                // wider it looks, the more squarely it faces us and the brighter it is.
                val width = right - left
                shades[first + face] = if (width > 0f) {
                    (width / FACE_FULL_WIDTH * shadeCount).toInt().coerceIn(0, shadeCount - 1)
                } else {
                    -1
                }
            }
        }
    }
}
