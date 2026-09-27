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
import kotlin.math.abs

/** A few grey levels, like the limited palette of the original demos. */
private val SuperStarShades = listOf(
    Color(0xFF444444),
    Color(0xFF777777),
    Color(0xFFAAAAAA),
    Color(0xFFDDDDDD),
    Color(0xFFFFFFFF),
)

/** Stars grow from 1 to this many "Amiga pixels" as they approach. */
private const val MAX_SUPER_STAR_PIXELS = 3

/** Size of one "Amiga pixel" relative to the view width (a 320 px wide screen). */
private const val SUPER_AMIGA_PIXELS_ACROSS = 320f

/** Stars that moved more than this many pixels since the last frame are drawn as streaks. */
private const val STREAK_MIN_PIXELS = 1.5f

/**
 * A starfield seen from a spaceship steering through space (see [SuperStarfield]). Fast
 * stars, e.g. during a boost or a sharp turn, are drawn as motion streaks. Animates only
 * while the screen is resumed and allocates nothing per frame.
 */
@Composable
fun SuperStarfieldView(modifier: Modifier = Modifier) {
    val starfield = remember { SuperStarfield() }
    // Bumped every frame; reading it while drawing redraws without recomposing.
    var frameNanos by remember { mutableLongStateOf(0L) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Restart timing after a pause so the ship does not jump.
            var lastFrame = withFrameNanos { it }
            while (true) {
                val now = withFrameNanos { it }
                // Cap long frames (e.g. after a hitch) so the flight stays smooth.
                starfield.advance(((now - lastFrame) / 1_000_000_000f).coerceAtMost(0.1f))
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
                val pixel = size.width / SUPER_AMIGA_PIXELS_ACROSS
                for (i in 0 until starfield.starCount) {
                    if (!starfield.visible[i]) continue
                    val light = starfield.brightness[i]
                    val shade = SuperStarShades[
                        (light * SuperStarShades.size).toInt().coerceIn(0, SuperStarShades.lastIndex)
                    ]
                    val starSize = pixel *
                        (1 + (light * MAX_SUPER_STAR_PIXELS).toInt().coerceAtMost(MAX_SUPER_STAR_PIXELS - 1))
                    val x = starfield.screenX[i]
                    val y = starfield.screenY[i]
                    val isStreak = starfield.hasPrevious[i] &&
                        abs(x - starfield.previousX[i]) + abs(y - starfield.previousY[i]) > pixel * STREAK_MIN_PIXELS
                    if (isStreak) {
                        drawLine(
                            color = shade,
                            start = Offset(starfield.previousX[i], starfield.previousY[i]),
                            end = Offset(x, y),
                            strokeWidth = starSize,
                        )
                    } else {
                        drawRect(
                            color = shade,
                            topLeft = Offset(x - starSize / 2f, y - starSize / 2f),
                            size = Size(starSize, starSize),
                        )
                    }
                }
            },
    )
}

@Preview
@Composable
private fun SuperStarfieldViewPreview() {
    BoingBallDemoTheme {
        SuperStarfieldView(modifier = Modifier.size(320.dp).aspectRatio(4f / 3f))
    }
}
