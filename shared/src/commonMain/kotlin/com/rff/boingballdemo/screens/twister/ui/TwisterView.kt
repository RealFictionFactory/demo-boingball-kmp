package com.rff.boingballdemo.screens.twister.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import kotlin.math.floor

/** Half-width of the twister column, as a fraction of the view width. */
private const val TWISTER_HALF_WIDTH = 0.17f

/**
 * Full-window twister (see [Twister]) on a black screen, drawn as chunky scanlines with
 * one rectangle per visible face. Animates only while the screen is resumed.
 */
@Composable
fun TwisterView(modifier: Modifier = Modifier) {
    val twister = remember { Twister() }
    val palette = remember { twisterPalette(twister.shadeCount) }
    // Seconds of animation so far; only the draw phase reads it.
    val clock = remember { FloatArray(1) }
    // Bumped every frame; reading it while drawing redraws without recomposing.
    var frameNanos by remember { mutableLongStateOf(0L) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Restart timing after a pause so the twister does not jump.
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
        modifier = modifier
            .background(color = Color.Black)
            .drawBehind {
                frameNanos // read to redraw on every frame
                twister.update(clock[0])
                val halfWidth = size.width * TWISTER_HALF_WIDTH
                val rowHeight = size.height / twister.rows
                for (row in 0 until twister.rows) {
                    // Whole-pixel edges shared by neighbouring rows and faces leave no hairline seams.
                    val top = floor(row * rowHeight)
                    val bottom = floor((row + 1) * rowHeight)
                    val center = size.width / 2f + twister.centers[row] * halfWidth
                    val first = row * TWISTER_FACES
                    for (face in 0 until TWISTER_FACES) {
                        val shade = twister.shades[first + face]
                        if (shade < 0) continue
                        val left = floor(center + twister.edges[first + face] * halfWidth)
                        val right = floor(center + twister.edges[first + (face + 1) % TWISTER_FACES] * halfWidth)
                        if (right <= left) continue
                        drawRect(
                            color = palette[face * twister.shadeCount + shade],
                            topLeft = Offset(left, top),
                            size = Size(right - left, bottom - top),
                        )
                    }
                }
            },
    )
}

@Preview
@Composable
private fun TwisterViewPreview() {
    BoingBallDemoTheme {
        TwisterView(modifier = Modifier.size(320.dp).aspectRatio(4f / 3f))
    }
}
