package com.rff.boingballdemo.screens.kefrens.ui

import androidx.compose.ui.graphics.Color
import com.rff.boingballdemo.utils.TAU
import kotlin.math.roundToInt
import kotlin.math.sin

/** Default size of the chunky screen: 160 cells across (2-pixel wide, like a lowres line), 120 scanlines. */
internal const val KEFRENS_COLUMNS = 160
internal const val KEFRENS_ROWS = 120

/** Width of one bar in cells; every cell across it gets its own shade. */
internal const val KEFRENS_BAR_WIDTH = 12

/** Palette index of the empty background. */
internal const val KEFRENS_BACKGROUND = 0

/** Relative position of the bright highlight across a bar (0 = left edge, 1 = right edge). */
private const val BAR_HIGHLIGHT = 0.4f

/** Scanlines drawn with the same bar colour before moving on to the next one. */
private const val ROWS_PER_COLOR = 6

/** Base colours the bars cycle through down the screen. */
private val KefrensColors = listOf(
    Color(0xFFFF2200),
    Color(0xFFFF8800),
    Color(0xFFFFEE00),
    Color(0xFF00CC33),
    Color(0xFF00AAFF),
    Color(0xFFDD22CC),
)

internal val KEFRENS_COLOR_COUNT = KefrensColors.size

/**
 * Background first, then [barWidth] shades of every bar colour from left to right: entry
 * `1 + color * barWidth + x`. Each bar is dark at its edges with a white-ish highlight,
 * rounded to the Amiga OCS 12-bit colour space (4 bits per channel).
 */
internal fun kefrensPalette(barWidth: Int = KEFRENS_BAR_WIDTH): List<Color> =
    listOf(Color.Black) + KefrensColors.flatMap { base ->
        List(barWidth) { x ->
            val position = (x + 0.5f) / barWidth
            // 0 at both edges of the bar, 1 at the highlight
            val brightness = if (position < BAR_HIGHLIGHT) {
                position / BAR_HIGHLIGHT
            } else {
                (1f - position) / (1f - BAR_HIGHLIGHT)
            }
            fun channel(value: Float): Float {
                val lit = if (brightness < 0.75f) {
                    value * brightness / 0.75f
                } else {
                    value + (1f - value) * (brightness - 0.75f) / 0.25f
                }
                return (lit.coerceIn(0f, 1f) * 15f).roundToInt() / 15f
            }
            Color(red = channel(base.red), green = channel(base.green), blue = channel(base.blue))
        }
    }

/**
 * The Kefrens bars (named after the Danish group whose demos made them famous): one
 * shaded vertical bar per scanline, swinging left and right along a sum of sine waves.
 * The line buffer is never cleared between scanlines, so every line keeps all the bars
 * drawn above it and the bars pile up into a wavy, seemingly 3D curtain.
 *
 * Call [update] once per frame, then read [cells]; nothing is allocated per frame.
 */
internal class KefrensBars(
    val columns: Int = KEFRENS_COLUMNS,
    val rows: Int = KEFRENS_ROWS,
    val barWidth: Int = KEFRENS_BAR_WIDTH,
) {
    /** Palette index of every cell, row by row. */
    val cells = IntArray(columns * rows)

    /** Left edge of the bar added on every scanline. */
    val barPositions = IntArray(rows)

    /** The single line the hardware would reuse for every scanline. */
    private val line = IntArray(columns)

    fun update(timeSeconds: Float) {
        line.fill(KEFRENS_BACKGROUND)
        val travel = (columns - barWidth) / 2f
        // Colours roll down the screen as time goes by.
        val colorShift = (timeSeconds * 8f).toInt()
        for (row in 0 until rows) {
            val height = row.toFloat() / rows
            // Two waves of different speed and length; their sum stays within [-1, 1].
            val wave = 0.65f * sin(height * TAU * 1.1f + timeSeconds * 1.9f) +
                0.35f * sin(height * TAU * 2.3f - timeSeconds * 1.3f)
            val left = (travel + wave * travel).roundToInt().coerceIn(0, columns - barWidth)
            barPositions[row] = left

            val color = ((row / ROWS_PER_COLOR + colorShift) % KEFRENS_COLOR_COUNT)
            val firstShade = 1 + color * barWidth
            for (x in 0 until barWidth) {
                line[left + x] = firstShade + x
            }
            line.copyInto(cells, destinationOffset = row * columns)
        }
    }
}
