package com.rff.boingballdemo.screens.kefrens.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import kotlin.math.floor

/**
 * Full-window Kefrens bars (see [KefrensBars]), drawn as chunky cells. Runs of neighbouring
 * cells with the same colour are merged into one rectangle to keep draw calls down.
 * Animates only while the screen is resumed.
 */
@Composable
fun KefrensBarsView(modifier: Modifier = Modifier) {
    val bars = remember { KefrensBars() }
    val palette = remember { kefrensPalette(bars.barWidth) }
    // Seconds of animation so far; only the draw phase reads it.
    val clock = remember { FloatArray(1) }
    // Bumped every frame; reading it while drawing redraws without recomposing.
    var frameNanos by remember { mutableLongStateOf(0L) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Restart timing after a pause so the bars do not jump.
            var lastFrame = withFrameNanos { it }
            while (true) {
                val now = withFrameNanos { it }
                // Cap long frames (e.g. after a hitch) so the motion stays smooth.
                clock[0] += ((now - lastFrame) / 1_000_000_000f).coerceAtMost(0.1f)
                lastFrame = now
                frameNanos = now
            }
        }
    }

    Spacer(
        modifier = modifier.drawBehind {
            frameNanos // read to redraw on every frame
            bars.update(clock[0])
            val cellWidth = size.width / bars.columns
            val cellHeight = size.height / bars.rows
            for (row in 0 until bars.rows) {
                // Whole-pixel edges shared by neighbouring cells leave no hairline seams.
                val top = floor(row * cellHeight)
                val bottom = floor((row + 1) * cellHeight)
                val rowStart = row * bars.columns
                var start = 0
                while (start < bars.columns) {
                    val shade = bars.cells[rowStart + start]
                    var end = start + 1
                    while (end < bars.columns && bars.cells[rowStart + end] == shade) end++
                    val left = floor(start * cellWidth)
                    val right = if (end == bars.columns) size.width else floor(end * cellWidth)
                    drawRect(
                        color = palette[shade],
                        topLeft = Offset(left, top),
                        size = Size(right - left, bottom - top),
                    )
                    start = end
                }
            }
        },
    )
}

@Preview
@Composable
private fun KefrensBarsViewPreview() {
    BoingBallDemoTheme {
        KefrensBarsView(modifier = Modifier.size(320.dp).aspectRatio(4f / 3f))
    }
}
