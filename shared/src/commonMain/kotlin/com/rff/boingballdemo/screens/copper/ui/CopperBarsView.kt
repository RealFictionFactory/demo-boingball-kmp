package com.rff.boingballdemo.screens.copper.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.utils.TAU
import kotlin.math.sin

internal const val COPPER_BAR_COUNT = 5

/** Number of horizontal slices used to fake the copper colour ramp of a single bar. */
private const val COPPER_BAR_STEPS = 15

/** Relative position of the bright highlight inside a bar (0 = top, 1 = bottom). */
private const val COPPER_BAR_HIGHLIGHT = 0.42f

/** Base colours of the bars, in the order they are drawn (first one is the backmost). */
private val CopperBarColors = listOf(
    Color(0xFFFF2200),
    Color(0xFFFF8800),
    Color(0xFF00CC33),
    Color(0xFF00AAFF),
    Color(0xFFDD22CC),
)

/**
 * Classic Amiga copper bars: a handful of shaded colour bars weaving up and down
 * over a black screen, each one with its own speed and phase.
 */
@Composable
fun CopperBarsView(
    modifier: Modifier = Modifier,
    barCount: Int = COPPER_BAR_COUNT,
) {
    var timeSeconds by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastFrameNanos = withFrameNanos { it }
        while (true) {
            val frameNanos = withFrameNanos { it }
            timeSeconds += (frameNanos - lastFrameNanos) / 1_000_000_000f
            lastFrameNanos = frameNanos
        }
    }

    Canvas(modifier = modifier.background(color = Color.Black)) {
        val bars = minOf(barCount, CopperBarColors.size)
        val barHeight = size.height / (bars + 3f)
        val travel = size.height - barHeight

        repeat(bars) { index ->
            val phase = index * TAU / bars
            val speed = 0.16f + index * 0.023f
            // sine in [-1, 1] mapped to a top position in [0, travel]
            val wave = sin(TAU * speed * timeSeconds + phase)
            val top = travel * (1f + wave) / 2f

            drawCopperBar(
                top = top,
                height = barHeight,
                color = CopperBarColors[index],
            )
        }
    }
}

private fun DrawScope.drawCopperBar(
    top: Float,
    height: Float,
    color: Color,
) {
    val stepHeight = height / COPPER_BAR_STEPS

    repeat(COPPER_BAR_STEPS) { step ->
        val position = (step + 0.5f) / COPPER_BAR_STEPS
        // 0 at both edges of the bar, 1 at the highlight line
        val brightness = if (position < COPPER_BAR_HIGHLIGHT) {
            position / COPPER_BAR_HIGHLIGHT
        } else {
            (1f - position) / (1f - COPPER_BAR_HIGHLIGHT)
        }

        val stepColor = if (brightness < 0.75f) {
            lerp(Color.Black, color, brightness / 0.75f)
        } else {
            lerp(color, Color.White, (brightness - 0.75f) / 0.25f)
        }

        drawRect(
            color = stepColor,
            topLeft = Offset(0f, top + step * stepHeight),
            // slight overdraw avoids hairline gaps between slices
            size = Size(size.width, stepHeight + 1f),
        )
    }
}

@Preview
@Composable
private fun CopperBarsViewPreview() {
    BoingBallDemoTheme {
        CopperBarsView(
            modifier = Modifier
                .size(320.dp)
                .aspectRatio(4f / 3f)
                .fillMaxSize(),
        )
    }
}
