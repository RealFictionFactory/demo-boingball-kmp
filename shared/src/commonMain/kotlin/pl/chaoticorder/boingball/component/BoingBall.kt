package pl.chaoticorder.boingball.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import pl.chaoticorder.boingball.ui.theme.BoingBallDemoTheme
import pl.chaoticorder.boingball.ui.theme.amigaOs13Blue
import pl.chaoticorder.boingball.utils.Face
import pl.chaoticorder.boingball.utils.Point3D
import pl.chaoticorder.boingball.utils.TAU
import pl.chaoticorder.boingball.utils.sameAs
import pl.chaoticorder.boingball.utils.toRadians
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal const val ROTATION_SPEED = 0.02f
internal const val BOING_BALL_ROWS = 8
internal const val BOING_BALL_COLUMNS = 16

@Composable
fun BoingBall(
    modifier : Modifier = Modifier,
    tilt     : Float    = -23.5f, // Earth-like axial tilt
    themeColor: Color,
    altColor: Color,
    drawBorders: Boolean,
) {
    val vBounce = remember { Animatable(0f) }
    var angle by remember { mutableFloatStateOf(0f) }
    var direction by remember { mutableStateOf(false) }
    var hBouncePrev by remember { mutableFloatStateOf(0f) }
    val boing = rememberBoingBallAudio()
    // Horizontal movement fraction [0..1]
    val hBounce = rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    ).value

    // Get the current lifecycle owner
    val lifecycleOwner = LocalLifecycleOwner.current

    // State to track if the app is currently resumed
    var isResumed by remember {
        mutableStateOf(
            lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        )
    }

    // Use DisposableEffect for lifecycle observation
    // This ensures the observer is removed when the composable leaves the composition
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isResumed = true
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                isResumed = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Consolidated animation loop to avoid duplicate angle updates
    LaunchedEffect(isResumed) {
        if (!isResumed) return@LaunchedEffect

        while (isResumed) {
            // Handle bounce animation
            // fall quickly
            vBounce.animateTo(
                targetValue = 1f,
                animationSpec = tween(500, easing = FastOutLinearInEasing)
            )
            boing.play()
            // rise more slowly
            vBounce.animateTo(
                targetValue = 0f,
                animationSpec = tween(900, easing = LinearOutSlowInEasing)
            )
        }
    }

    // Separate rotation animation loop
    LaunchedEffect(isResumed) {
        if (!isResumed) return@LaunchedEffect

        while (isResumed) {
            if (direction) {
                angle += ROTATION_SPEED // radians per frame
            } else {
                angle -= ROTATION_SPEED // radians per frame
            }

            withFrameNanos { /* keep looping */ }
        }
    }

    LaunchedEffect(direction, isResumed) {
        if (!isResumed) return@LaunchedEffect

        if (direction)
            boing.playRight()
        else
            boing.playLeft()
    }

    LaunchedEffect(hBounce) {
        direction = hBouncePrev > hBounce
        hBouncePrev = hBounce
    }

    Canvas(modifier = modifier) {
        /* ----- geometry constants ----- */
        val radius = size.minDimension * 0.2f
        val bounceMax = size.height - (size.height - size.height * .9f) / 2 - radius
        val bounceMin = size.height - size.height * .9f + radius
        val offsetY = lerp(bounceMin, bounceMax, vBounce.value)

        val maxX = size.width - radius
        val cx = radius + (maxX - radius) * hBounce
        val tz = tilt.toRadians()

        // shadow
        drawCircle(
            color = Color.DarkGray,
            radius = radius,
            center = Offset(cx + 50f, offsetY - 10f),
            alpha = .3f
        )
        boingBall(
            cx = cx,
            cy = offsetY,
            radius = radius,
            rotationAngle = angle,
            earthTiltAngle = tz,
            ballThemeColor = themeColor,
            ballAltColor = altColor,
            drawBorders = drawBorders,
        )
    }
}

private fun lerp(start: Float, end: Float, fraction: Float) = start + (end - start) * fraction

private fun DrawScope.boingBall(
    cx: Float,
    cy: Float,
    radius: Float,
    rotationAngle: Float,
    earthTiltAngle: Float,
    ballThemeColor: Color,
    ballAltColor: Color,
    drawBorders: Boolean,
) {
    val columns = BOING_BALL_COLUMNS
    val rows = BOING_BALL_ROWS
    val view = Point3D(0f, 0f, -1f)

    // Pre-calculate all vertices once to avoid redundant calculations
    // This creates (rows+1) × columns vertices = 9 × 16 = 144 vertices
    // instead of calculating ~256 times in the loop (each vertex used by ~4 quads)
    val vertexCache = Array(rows + 1) { rowIndex ->
        Array(columns) { colIndex ->
            val lat = ((PI / rows) * (rowIndex - rows / 2f)).toFloat()   // -π/2 → +π/2
            val lon = ((TAU / columns) * colIndex)
            Point3D(
                x = cos(lat) * cos(lon),
                y = sin(lat),
                z = cos(lat) * sin(lon)
            )
                .rotateY(rotationAngle)
                .rotateZ(earthTiltAngle)
        }
    }

    // Helper to get vertex from cache with wraparound for column
    fun getVertex(rowIndex: Int, colIndex: Int): Point3D {
        return vertexCache[rowIndex][colIndex % columns]
    }

    val faces = mutableListOf<Face>()

    for (row in 0 until rows) {
        for (column in 0 until columns) {
            val v1 = getVertex(row, column)
            val v2 = getVertex(row, column + 1)
            val v3 = getVertex(row + 1, column + 1)
            val v4 = getVertex(row + 1, column)

            // Edges for the generic quad
            val e1 = v2 - v1
            val e2 = v3 - v1

            // Pick edges that match the actual path winding at the poles:
            // - south pole triangle uses (v1 -> v3 -> v4)
            // - north pole triangle uses (v1 -> v2 -> v3)
            val normal = when {
                e1.isZero() -> (v3 - v1).cross(v4 - v1)             // south pole
                (v3 - v4).isZero() -> (v2 - v1).cross(v3 - v1)      // north pole
                else -> e1.cross(e2)                                // regular quad
            }

            val facing = normal dot view
            // Single, unified cull (drop back-facing)
            if (facing <= 0f) continue

            // Build path in screen space
            val p1 = v1.project(cx, cy, radius)
            val p2 = v2.project(cx, cy, radius)
            val p3 = v3.project(cx, cy, radius)
            val p4 = v4.project(cx, cy, radius)

            val path = Path().apply {
                moveTo(p1.x, p1.y)
                if (!p1.sameAs(p2)) lineTo(p2.x, p2.y)   // keeps north-pole triangle as v1->v2->v3
                lineTo(p3.x, p3.y)
                if (!p3.sameAs(p4)) lineTo(p4.x, p4.y)   // keeps south-pole triangle as v1->v3->v4
                close()
            }

            val col = if (((row + column) and 1) == 0) ballThemeColor else ballAltColor
            // Use minimum Z (farthest point from camera) for more stable depth sorting
            // This prevents z-fighting when faces have similar average depth but different extents
            val depth = minOf(v1.z, v2.z, v3.z, v4.z)
            faces += Face(path, depth, col)
        }
    }

    // Sort in-place instead of creating a new sorted list
    faces.sortBy { it.depth }
    faces.forEach { f ->
        drawPath(f.path, color = f.color)
        if (drawBorders) {
            drawPath(f.path, color = Color.Black, style = Stroke(width = 0.8f))
        }
    }
}

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
