package com.rff.boingballdemo.screens.starfield.ui

import com.rff.boingballdemo.utils.TAU
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

internal const val STARFIELD_STAR_COUNT = 220

/** Depth units travelled per second; a star crosses the full depth in about 2.5 s. */
internal const val STARFIELD_SPEED = 0.4f

/** Stars closer than this are recycled, which also avoids the division blowing up. */
private const val NEAR_PLANE = 0.02f

/** Each roll turns the whole field by a quarter turn around the view axis. */
internal const val STARFIELD_ROLL_ANGLE = (PI / 2).toFloat()

/** How long one roll takes, in seconds. */
internal const val STARFIELD_ROLL_SECONDS = 2f

/** How long one sideways drift takes, in seconds. */
internal const val STARFIELD_DRIFT_SECONDS = 3f

/** Peak sideways speed of a drift, in world units per second. */
internal const val STARFIELD_DRIFT_SPEED = 0.4f

/** Straight flight between two manoeuvres lasts a random time in this range, in seconds. */
internal const val STARFIELD_CRUISE_MIN_SECONDS = 2f
internal const val STARFIELD_CRUISE_MAX_SECONDS = 5f

/** Direction the ship drifts in, on screen (y points down). Stars move the opposite way. */
internal enum class DriftDirection(val dx: Float, val dy: Float) {
    Left(-1f, 0f),
    Right(1f, 0f),
    Up(0f, -1f),
    Down(0f, 1f),
}

private enum class Phase { Cruise, Roll, Drift }

/**
 * The classic 3D starfield: stars fly from the far plane (z = 1) towards the viewer and are
 * projected with `x / z`, so they spread out and speed up as they approach.
 *
 * Between stretches of straight flight the ship performs a random manoeuvre:
 * - a roll: the whole field turns a quarter turn around the view axis, either way;
 * - a drift: the ship slides up, down, left or right for a moment. Every star moves by the
 *   same world distance, so near stars sweep past faster than far ones (parallax).
 *
 * All state lives in arrays allocated once; [advance] and [project] allocate nothing, so
 * they can run every frame. Coordinates are in a unit space: x and y in [-1, 1], z in (0, 1].
 */
internal class Starfield(
    val starCount: Int = STARFIELD_STAR_COUNT,
    private val random: Random = Random.Default,
) {
    private val x = FloatArray(starCount)
    private val y = FloatArray(starCount)
    private val z = FloatArray(starCount)

    /** Projected screen positions and per-star brightness (0..1), written by [project]. */
    val screenX = FloatArray(starCount)
    val screenY = FloatArray(starCount)
    val brightness = FloatArray(starCount)

    /** Whether the star is on screen after the last [project]. */
    val visible = BooleanArray(starCount)

    // Set by project() for stars that flew off the edge; advance() recycles them.
    private val offScreen = BooleanArray(starCount)

    /**
     * Current roll of the whole field around the view axis, in radians within [0, 2π).
     * Positive is clockwise on screen (y points down).
     */
    var rollAngle = 0f
        private set

    /** True while a roll is in progress. */
    val isRolling: Boolean get() = phase == Phase.Roll

    /** The drift in progress, or null when not drifting. */
    var drift: DriftDirection? = null
        private set

    private var phase = Phase.Cruise
    // 0 at the start and end of a drift, 1 at its middle.
    private var driftIntensity = 0f
    // Half the screen size in normalised units (1 = the edge of the central square).
    private var extentX = 1f
    private var extentY = 1f
    private var rollFrom = 0f
    private var rollTo = 0f
    private var phaseElapsed = 0f
    private var cruiseSeconds = 0f

    init {
        // Spread the initial stars over the whole depth so the field starts full.
        for (i in 0 until starCount) respawn(i, z = random.nextFloat().coerceAtLeast(NEAR_PLANE))
        cruiseSeconds = nextCruiseSeconds()
    }

    /**
     * Moves every star towards the viewer (and sideways while drifting). Stars that passed
     * the near plane, or left the screen in the last [project], restart far away; during a
     * drift some come in at the leading edge instead, so that side does not empty out.
     */
    fun advance(deltaSeconds: Float, speed: Float = STARFIELD_SPEED) {
        advanceManoeuvre(deltaSeconds)
        val step = speed * deltaSeconds

        // Sideways movement of the stars in world space: opposite to the ship's screen
        // direction, turned back by the current roll so "left" stays screen-left.
        var shiftX = 0f
        var shiftY = 0f
        val direction = drift
        if (direction != null) {
            val distance = STARFIELD_DRIFT_SPEED * driftIntensity * deltaSeconds
            val cosRoll = cos(rollAngle)
            val sinRoll = sin(rollAngle)
            val starsDx = -direction.dx * distance
            val starsDy = -direction.dy * distance
            shiftX = starsDx * cosRoll + starsDy * sinRoll
            shiftY = -starsDx * sinRoll + starsDy * cosRoll
        }

        for (i in 0 until starCount) {
            if (offScreen[i]) {
                if (direction != null && random.nextFloat() < refillChance(direction)) {
                    respawnAtLeadingEdge(i, direction)
                } else {
                    respawn(i, z = 1f)
                }
                continue
            }
            z[i] -= step
            x[i] += shiftX
            y[i] += shiftY
            if (z[i] < NEAR_PLANE) respawn(i, z = 1f)
        }
    }

    /**
     * Chance that a recycled star re-enters at the leading edge instead of far away. Only
     * needed where the screen extends past the central square in the drift direction (e.g.
     * drifting sideways on a wide screen): elsewhere approaching stars already fill it.
     */
    private fun refillChance(direction: DriftDirection): Float {
        val extent = if (direction.dx != 0f) extentX else extentY
        return driftIntensity * (extent - 1f).coerceIn(0f, 1f) * LEADING_EDGE_REFILL
    }

    /** Starts a quarter-turn roll now. Used by the random schedule, and by tests. */
    fun startRoll(clockwise: Boolean) {
        phase = Phase.Roll
        phaseElapsed = 0f
        rollFrom = rollAngle
        rollTo = rollAngle + if (clockwise) STARFIELD_ROLL_ANGLE else -STARFIELD_ROLL_ANGLE
    }

    /** Starts a sideways drift now. Used by the random schedule, and by tests. */
    fun startDrift(direction: DriftDirection) {
        phase = Phase.Drift
        phaseElapsed = 0f
        drift = direction
        driftIntensity = 0f
    }

    /**
     * Projects the stars onto a [width] × [height] screen centred in the middle. Only writes
     * the output arrays; stars that left the screen are recycled on the next [advance].
     */
    fun project(width: Float, height: Float) {
        val centerX = width / 2f
        val centerY = height / 2f
        // Scale by the shorter side so the field looks the same in any aspect ratio.
        val scale = minOf(width, height) / 2f
        extentX = centerX / scale
        extentY = centerY / scale
        val cosRoll = cos(rollAngle)
        val sinRoll = sin(rollAngle)
        for (i in 0 until starCount) {
            val px = x[i] / z[i] * scale
            val py = y[i] / z[i] * scale
            val sx = centerX + px * cosRoll - py * sinRoll
            val sy = centerY + px * sinRoll + py * cosRoll
            val onScreen = sx in 0f..width && sy in 0f..height
            screenX[i] = sx
            screenY[i] = sy
            brightness[i] = 1f - z[i]
            visible[i] = onScreen
            offScreen[i] = !onScreen
        }
    }

    // Cruise for a random time, then roll or drift in a random direction (50/50).
    private fun advanceManoeuvre(deltaSeconds: Float) {
        phaseElapsed += deltaSeconds
        when (phase) {
            Phase.Cruise -> if (phaseElapsed >= cruiseSeconds) {
                if (random.nextBoolean()) {
                    startRoll(clockwise = random.nextBoolean())
                } else {
                    startDrift(DriftDirection.entries[random.nextInt(DriftDirection.entries.size)])
                }
            }
            Phase.Roll -> {
                val progress = (phaseElapsed / STARFIELD_ROLL_SECONDS).coerceAtMost(1f)
                rollAngle = rollFrom + (rollTo - rollFrom) * smoothStep(progress)
                if (progress >= 1f) {
                    // Land exactly on the quarter turn and keep the angle in [0, 2π).
                    rollAngle = rollTo.mod(TAU)
                    startCruise()
                }
            }
            Phase.Drift -> {
                val progress = (phaseElapsed / STARFIELD_DRIFT_SECONDS).coerceAtMost(1f)
                // Speed swells and fades like a bell, so the drift starts and stops gently.
                driftIntensity = sin(PI.toFloat() * progress)
                if (progress >= 1f) {
                    drift = null
                    driftIntensity = 0f
                    startCruise()
                }
            }
        }
    }

    private fun startCruise() {
        phase = Phase.Cruise
        phaseElapsed = 0f
        cruiseSeconds = nextCruiseSeconds()
    }

    private fun nextCruiseSeconds(): Float =
        STARFIELD_CRUISE_MIN_SECONDS +
            random.nextFloat() * (STARFIELD_CRUISE_MAX_SECONDS - STARFIELD_CRUISE_MIN_SECONDS)

    // Places a star just inside the screen edge the ship drifts towards, at a far depth so
    // it appears dim, like a star coming into view.
    private fun respawnAtLeadingEdge(index: Int, direction: DriftDirection) {
        offScreen[index] = false
        val depth = LEADING_EDGE_MIN_Z + random.nextFloat() * (1f - LEADING_EDGE_MIN_Z)
        val across = random.nextFloat() * 2f - 1f
        // Normalised screen position on the leading edge.
        val u = if (direction.dx != 0f) direction.dx * extentX * EDGE_INSET else across * extentX
        val v = if (direction.dy != 0f) direction.dy * extentY * EDGE_INSET else across * extentY
        // Undo the roll to get back to world space, then undo the x / z projection.
        val cosRoll = cos(rollAngle)
        val sinRoll = sin(rollAngle)
        x[index] = (u * cosRoll + v * sinRoll) * depth
        y[index] = (-u * sinRoll + v * cosRoll) * depth
        z[index] = depth
    }

    private fun respawn(index: Int, z: Float) {
        offScreen[index] = false
        x[index] = random.nextFloat() * 2f - 1f
        y[index] = random.nextFloat() * 2f - 1f
        this.z[index] = z
    }
}

/**
 * Tuned so the leading side of a 2:1 screen keeps its normal star density during a drift
 * (measured: 26.9 stars in the leading quarter vs 27.2 without drifting; 10.4 with no refill).
 */
private const val LEADING_EDGE_REFILL = 0.8f

/** Stars entering at the leading edge start at least this far away (so they appear dim). */
private const val LEADING_EDGE_MIN_Z = 0.5f

/** Just inside the edge, so a new star is on screen right away and is not recycled again. */
private const val EDGE_INSET = 0.99f

/** Ease-in, ease-out: starts and ends the roll gently, fastest in the middle. */
private fun smoothStep(t: Float): Float = t * t * (3f - 2f * t)
