package com.rff.boingballdemo.screens.boingball.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.rff.boingballdemo.component.VideoSystem
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.utils.toRadians
import kotlin.math.abs
import kotlinx.coroutines.launch

internal const val BOING_BALL_ROWS = 8
internal const val BOING_BALL_COLUMNS = 16
/** PAL reference speeds; NTSC is 60/50 of these. */
internal const val ROTATION_SPEED_RADIANS_PER_SECOND = 2.8f
internal const val HORIZONTAL_TRAVEL_MS = 3000
internal const val VERTICAL_FALL_MS = 600
internal const val VERTICAL_RISE_MS = 1100
internal const val HORIZONTAL_START_FRACTION = 0.5f
internal const val INITIAL_MOVING_LEFT = true
/** Shadow offset as a fraction of the ball radius (was 50 px / 10 px at a ~160 px radius). */
internal const val SHADOW_OFFSET_X = 0.3f
internal const val SHADOW_OFFSET_Y = 0.06f

internal fun rotationSpeedRadiansPerSecond(vblankHz: Int): Float =
    ROTATION_SPEED_RADIANS_PER_SECOND * vblankHz / VideoSystem.PAL.vblankHz

internal fun scaledDurationMs(palDurationMs: Int, vblankHz: Int): Int =
    (palDurationMs * VideoSystem.PAL.vblankHz / vblankHz).coerceAtLeast(1)

internal fun nextHorizontalFraction(movingLeft: Boolean): Float = if (movingLeft) 0f else 1f

/** Positive Y rotation moves the front-facing tiles to the right. */
internal fun rotationSign(movingLeft: Boolean): Float = if (movingLeft) 1f else -1f

internal fun horizontalTravelDurationMs(
    from: Float,
    to: Float,
    fullMs: Int,
): Int = (fullMs * abs(to - from)).toInt().coerceAtLeast(1)

@Composable
fun BoingBall(
    modifier : Modifier = Modifier,
    tilt     : Float    = -23.5f, // Earth-like axial tilt
    themeColor: Color,
    altColor: Color,
    drawBorders: Boolean,
    drawShadow: Boolean = true,
    videoSystem: VideoSystem = VideoSystem.PAL,
) {
    val vblankHz = videoSystem.vblankHz
    val rotationSpeed = rotationSpeedRadiansPerSecond(vblankHz)
    val fullTravelMs = scaledDurationMs(HORIZONTAL_TRAVEL_MS, vblankHz)
    val fallMs = scaledDurationMs(VERTICAL_FALL_MS, vblankHz)
    val riseMs = scaledDurationMs(VERTICAL_RISE_MS, vblankHz)
    val vBounce = remember { Animatable(0f) }
    val hBounce = remember { Animatable(HORIZONTAL_START_FRACTION) }
    var angle by remember { mutableFloatStateOf(0f) }
    // true = travelling left; front-facing tiles rotate to the right.
    var direction by remember { mutableStateOf(INITIAL_MOVING_LEFT) }
    val boing = rememberBoingBallAudio()

    val lifecycleOwner = LocalLifecycleOwner.current

    // Animate only while the screen is resumed. repeatOnLifecycle cancels the loops on
    // pause and restarts them on resume, continuing from the current ball position.
    LaunchedEffect(lifecycleOwner, videoSystem) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Vertical bounce, with the boing sound on each floor hit.
            launch {
                while (true) {
                    vBounce.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(fallMs, easing = FastOutLinearInEasing)
                    )
                    boing?.play()
                    vBounce.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(riseMs, easing = LinearOutSlowInEasing)
                    )
                }
            }

            // Spin, advanced by real frame time so speed does not depend on refresh rate.
            launch {
                var lastFrameNanos = withFrameNanos { it }
                while (true) {
                    val frameNanos = withFrameNanos { it }
                    val deltaSeconds = (frameNanos - lastFrameNanos) / 1_000_000_000f
                    lastFrameNanos = frameNanos

                    angle += rotationSign(direction) * rotationSpeed * deltaSeconds
                }
            }

            // Horizontal travel between the walls, with a panned boing on each wall hit.
            launch {
                while (true) {
                    val target = nextHorizontalFraction(direction)
                    val duration = horizontalTravelDurationMs(hBounce.value, target, fullTravelMs)
                    hBounce.animateTo(
                        targetValue = target,
                        animationSpec = tween(duration, easing = LinearEasing)
                    )
                    direction = !direction
                    if (direction) {
                        boing?.playRight()
                    } else {
                        boing?.playLeft()
                    }
                }
            }
        }
    }

    Spacer(
        modifier = modifier.drawWithCache {
            // Allocated once per size; every frame only rewrites them.
            val mesh = BoingBallMesh()
            val themePath = Path()
            val altPath = Path()
            val borderStroke = Stroke(width = 0.8f)
            val tiltRadians = tilt.toRadians()

            onDrawBehind {
                val radius = size.minDimension * 0.2f
                val bounceMax = size.height - (size.height - size.height * .9f) / 2 - radius
                val bounceMin = size.height - size.height * .9f + radius
                val cy = lerp(bounceMin, bounceMax, vBounce.value)
                val maxX = size.width - radius
                val cx = radius + (maxX - radius) * hBounce.value

                if (drawShadow) {
                    drawCircle(
                        color = Color.DarkGray,
                        radius = radius,
                        // Relative to the ball, so the shadow looks the same at any size.
                        center = Offset(cx + radius * SHADOW_OFFSET_X, cy - radius * SHADOW_OFFSET_Y),
                        alpha = .3f
                    )
                }

                mesh.update(angle, tiltRadians, cx, cy, radius)
                mesh.buildPaths(themePath, altPath)
                drawPath(themePath, color = themeColor)
                drawPath(altPath, color = altColor)
                if (drawBorders) {
                    drawPath(themePath, color = Color.Black, style = borderStroke)
                    drawPath(altPath, color = Color.Black, style = borderStroke)
                }
            }
        }
    )
}

private fun lerp(start: Float, end: Float, fraction: Float) = start + (end - start) * fraction

@Preview
@Composable
private fun BoingBallPreview() {
    BoingBallDemoTheme {
        Box(modifier = Modifier
            .size(300.dp)
            .padding(32.dp)
        ) {
            BoingBall(
                modifier = Modifier.fillMaxSize(),
                tilt = +23.5f,
                themeColor = amigaOs13Blue,
                altColor = Color.White,
                drawBorders = true,
            )
        }
    }
}


@Preview
@Composable
private fun BoingBallIconTemplatePreview() {
    BoingBallDemoTheme {
        Box(modifier = Modifier
            .size(300.dp)
            .padding(32.dp)
        ) {
            BoingBall(
                modifier = Modifier.fillMaxSize(),
                tilt = +23.5f,
                themeColor = Color.Red,
                altColor = Color.White,
                drawBorders = false,
                drawShadow = false,
            )
        }
    }
}
