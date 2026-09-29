package com.rff.boingballdemo.screens.plasma.ui

import androidx.compose.ui.graphics.Color
import com.rff.boingballdemo.utils.TAU
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/** Default number of plasma cells across and down: chunky, like a 320x240 screen in 4x4 blocks. */
internal const val PLASMA_COLUMNS = 80
internal const val PLASMA_ROWS = 60

/** Number of colours in the plasma palette; the palette is cycled to animate the colours. */
internal const val PLASMA_PALETTE_SIZE = 64

/** Palette entries the colours cycle through per second. */
private const val PALETTE_CYCLE_SPEED = 12f

/**
 * A smooth, looping rainbow palette in the Amiga OCS 12-bit colour space (4 bits per
 * channel), so neighbouring colours band just like on the real hardware.
 */
internal fun plasmaPalette(size: Int = PLASMA_PALETTE_SIZE): List<Color> = List(size) { index ->
    val angle = TAU * index / size
    fun channel(phase: Float): Float {
        val level = ((0.5f + 0.5f * sin(angle + phase)) * 15f).roundToInt()
        return level / 15f
    }
    Color(red = channel(0f), green = channel(TAU / 3f), blue = channel(2f * TAU / 3f))
}

/**
 * The classic demo plasma: every cell of a [columns] x [rows] grid takes a palette index
 * from a sum of moving sine waves (horizontal, vertical, diagonal and circular). Call
 * [update] once per frame, then read [cells]; nothing is allocated per frame.
 */
internal class Plasma(
    val columns: Int = PLASMA_COLUMNS,
    val rows: Int = PLASMA_ROWS,
    val paletteSize: Int = PLASMA_PALETTE_SIZE,
) {
    /** Palette index of every cell, row by row. */
    val cells = IntArray(columns * rows)

    /** Distance of every cell from the grid centre, which never changes. */
    private val distances = FloatArray(columns * rows) { index ->
        val dx = index % columns - columns / 2f
        val dy = index / columns - rows / 2f
        sqrt(dx * dx + dy * dy)
    }

    fun update(timeSeconds: Float) {
        // Colour cycling: shift every cell along the palette as time goes by.
        val cycle = (timeSeconds * PALETTE_CYCLE_SPEED).toInt()
        for (row in 0 until rows) {
            val vertical = sin(row / 7f + timeSeconds * 0.9f)
            for (column in 0 until columns) {
                val index = row * columns + column
                val sum = sin(column / 11f + timeSeconds) +
                    vertical +
                    sin((column + row) / 16f + timeSeconds * 1.3f) +
                    sin(distances[index] / 6f - timeSeconds * 1.7f)
                // sum is in [-4, 4]: map it to [0, 1) and then onto the palette.
                val shade = ((sum + 4f) / 8f * paletteSize).toInt().coerceIn(0, paletteSize - 1)
                cells[index] = (shade + cycle) % paletteSize
            }
        }
    }
}
