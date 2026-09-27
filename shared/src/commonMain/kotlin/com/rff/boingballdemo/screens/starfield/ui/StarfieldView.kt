package com.rff.boingballdemo.screens.starfield.ui

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

/**
 * Amiga-style palette: the original demos had only a few grey levels to fade stars in with
 * distance, so brightness is quantised to these instead of a smooth gradient.
 */
private val StarShades = listOf(
    Color(0xFF444444),
    Color(0xFF777777),
    Color(0xFFAAAAAA),
    Color(0xFFDDDDDD),
    Color(0xFFFFFFFF),
)

/** Stars grow from 1 to this many "Amiga pixels" as they approach. */
private const val MAX_STAR_PIXELS = 3

/** Size of one "Amiga pixel" relative to the view width (a 320 px wide screen). */
private const val AMIGA_PIXELS_ACROSS = 320f

/**
 * A starfield flying towards the viewer on a black screen. Animates only while the screen is
 * resumed and allocates nothing per frame.
 */
@Composable
fun StarfieldView(modifier: Modifier = Modifier) {
    val starfield = remember { Starfield() }
    // Bumped every frame; reading it while drawing redraws without recomposing.
    var frameNanos by remember { mutableLongStateOf(0L) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Restart timing after a pause so the stars do not jump.
            var lastFrame = withFrameNanos { it }
            while (true) {
                val now = withFrameNanos { it }
                starfield.advance((now - lastFrame) / 1_000_000_000f)
                lastFrame = now
                frameNanos = now
            }
        }
    }

    Spacer(
        modifier = modifier
            .background(Color.Black)
            .drawBehind {
                frameNanos // read to redraw on every frame
                starfield.project(size.width, size.height)
                val pixel = size.width / AMIGA_PIXELS_ACROSS
                for (i in 0 until starfield.starCount) {
                    if (!starfield.visible[i]) continue
                    val light = starfield.brightness[i]
                    val shade = StarShades[(light * StarShades.size).toInt().coerceIn(0, StarShades.lastIndex)]
                    val starSize = pixel * (1 + (light * MAX_STAR_PIXELS).toInt().coerceAtMost(MAX_STAR_PIXELS - 1))
                    drawRect(
                        color = shade,
                        topLeft = Offset(starfield.screenX[i] - starSize / 2f, starfield.screenY[i] - starSize / 2f),
                        size = Size(starSize, starSize),
                    )
                }
            },
    )
}

@Preview
@Composable
private fun StarfieldViewPreview() {
    BoingBallDemoTheme {
        StarfieldView(modifier = Modifier.size(320.dp).aspectRatio(4f / 3f))
    }
}
