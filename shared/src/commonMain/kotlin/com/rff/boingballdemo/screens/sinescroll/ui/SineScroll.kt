package com.rff.boingballdemo.screens.sinescroll.ui

import com.rff.boingballdemo.utils.TAU
import kotlin.math.sin

/** Gap between two passes of the message, so the loop is easy to spot. */
private const val MESSAGE_GAP = "          "

internal val SineScrollMessage = listOf(
    "HELLO AND WELCOME TO THE BOING BALL DEMO!",
    "GREETINGS TO EVERY AMIGA FAN OUT THERE - FROM THE A500 KIDS TO THE A1200 VETERANS, " +
        "FROM THE CD32 GAMERS TO THE WORKBENCH WIZARDS AND EVERYONE WHO EVER TYPED \"LOADWB\" IN A CLI!",
    "RESPECT TO JAY MINER AND THE ORIGINAL AMIGA TEAM, AND TO DALE LUCK AND R.J. MICAL " +
        "FOR THE BOUNCING RED AND WHITE BALL THAT STOLE THE SHOW AT CES 1984!",
    "HUGE HELLOS TO THE DEMOSCENE (RANDOM ORDER): PHENOMENA, SANITY, KEFRENS, TRSI, THE BLACK LOTUS, " +
        "ANARCHY, THE SILENTS, SCOOPEX, ANDROMEDA, FAIRLIGHT, MELON DEZIGN, ALCATRAZ, " +
        "SPACEBALLS AND EVERY CRACKTRO CREW WHO SHOWED US WHAT THE COPPER AND THE BLITTER COULD REALLY DO!",
    "GURU MEDITATION IS NOT AN ERROR - IT IS A LIFESTYLE.",
    "THIS DEMO IS WRITTEN IN KOTLIN MULTIPLATFORM AND COMPOSE, BUT ITS HEART STILL BEATS AT 7.09 MHZ.",
    "KEEP YOUR DISKS AWAY FROM MAGNETS, YOUR JOYSTICK AWAY FROM YOUR LITTLE BROTHER, " +
        "AND NEVER FORGET: ONLY AMIGA MAKES IT POSSIBLE!",
    "AND HERE WE GO AGAIN...",
).joinToString(separator = "   ***   ", postfix = MESSAGE_GAP)

/**
 * The scroll text broken down into the distinct characters it uses ([glyphs], each drawn
 * once into a glyph atlas) and the atlas cell of every character of the text ([cells]).
 */
internal class SineScrollText(text: String) {
    val glyphs: String = text.toSet().joinToString("")
    val cells: IntArray = IntArray(text.length) { glyphs.indexOf(text[it]) }
    val length: Int get() = cells.size
}

/**
 * Left edge of the text after scrolling it by [scrolled]. The text enters from the right
 * edge of a [viewWidth] wide view and starts over once it has fully left on the left side.
 */
internal fun sineScrollTextLeft(scrolled: Float, textWidth: Float, viewWidth: Float): Float =
    viewWidth - scrolled % (textWidth + viewWidth)

/**
 * Vertical offset in [-1, 1] of the wave at horizontal position [x] (in Amiga pixels) after
 * [timeSeconds]. Two sines of different length and speed keep the wave from looking static.
 */
internal fun sineScrollWave(x: Float, timeSeconds: Float): Float =
    0.85f * sin(TAU * (x / 320f - 0.3f * timeSeconds)) +
        0.15f * sin(TAU * (x / 160f + 0.5f * timeSeconds))
