package com.rff.boingballdemo.screens.starfield.ui

import com.rff.boingballdemo.utils.TAU
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random

/**
 * The ship only sees the stars in its view cone within the cloud radius (well under a tenth of
 * them), so the cloud needs many stars to look as dense as the flat starfield (about 200
 * on screen, measured on a 4:3 view).
 */
internal const val SUPER_STARFIELD_STAR_COUNT = 3000

/** Forward speed while cruising, in world units per second (the star cloud has radius 1). */
internal const val SUPER_STARFIELD_CRUISE_SPEED = 0.35f

/** Peak forward speed during a boost, as a multiple of the cruise speed. */
internal const val SUPER_STARFIELD_BOOST_FACTOR = 5f

/** Horizontal/vertical field of view across the shorter screen side, in radians (70°). */
private const val FIELD_OF_VIEW = (70.0 * PI / 180.0).toFloat()

/** Stars live in a cube of this half size around the ship and fade out towards its edge. */
private const val CLOUD_RADIUS = 1f

/** Stars closer than this in front of the ship are not drawn (avoids the division blowing up). */
private const val NEAR_PLANE = 0.01f

/** How quickly turn and bank rates follow their targets, per second (higher is snappier). */
private const val RATE_RESPONSE = 2.5f

/** Bank angle per unit of yaw rate: the ship leans into its turns like an aircraft. */
private const val BANK_PER_YAW_RATE = 0.9f

private const val CRUISE_MIN_SECONDS = 2f
private const val CRUISE_MAX_SECONDS = 5f
private const val TURN_MIN_SECONDS = 2.5f
private const val TURN_MAX_SECONDS = 4f
private const val TURN_MIN_YAW_RATE = 0.35f
private const val TURN_MAX_YAW_RATE = 0.7f
private const val TURN_MAX_PITCH_RATE = 0.4f
private const val BARREL_ROLL_SECONDS = 2.5f
private const val BOOST_SECONDS = 2.5f

/** What the autopilot is doing. */
internal enum class SuperManoeuvre { Cruise, Turn, BarrelRoll, Boost }

/**
 * A starfield seen from a flying spaceship: the ship moves through a 3D cloud of stars and
 * steers freely. An autopilot alternates straight flight with banked turns (yaw and pitch),
 * barrel rolls and speed boosts.
 *
 * The ship keeps a full 3D orientation (right / up / forward axes), turned a little every
 * frame by the current yaw, pitch and roll rates. Stars are kept relative to the ship in a
 * cube around it; a star leaving one face comes back on the opposite face at a new random
 * position, far away and therefore invisible, so the space never runs out and nothing pops.
 *
 * Everything lives in arrays allocated once; [advance] and [project] allocate nothing.
 */
internal class SuperStarfield(
    val starCount: Int = SUPER_STARFIELD_STAR_COUNT,
    private val random: Random = Random.Default,
    private val cruiseSpeed: Float = SUPER_STARFIELD_CRUISE_SPEED,
) {
    // Star positions relative to the ship, in world axes.
    private val relX = FloatArray(starCount)
    private val relY = FloatArray(starCount)
    private val relZ = FloatArray(starCount)
    private val wrapped = BooleanArray(starCount)

    /** Projected positions and brightness (0..1), written by [project]. */
    val screenX = FloatArray(starCount)
    val screenY = FloatArray(starCount)
    val brightness = FloatArray(starCount)
    val visible = BooleanArray(starCount)

    /**
     * Screen position in the previous [project], for motion streaks. Only meaningful where
     * [hasPrevious] is true (the star was on screen last frame and did not wrap since).
     */
    val previousX = FloatArray(starCount)
    val previousY = FloatArray(starCount)
    val hasPrevious = BooleanArray(starCount)

    // Ship orientation: right, up and forward axes in world space (orthonormal).
    private var rightX = 1f; private var rightY = 0f; private var rightZ = 0f
    private var upX = 0f; private var upY = 1f; private var upZ = 0f
    private var forwardX = 0f; private var forwardY = 0f; private var forwardZ = 1f

    /** Current forward speed, in world units per second. */
    var speed = cruiseSpeed
        private set

    /** Current turn rates in radians per second: yaw > 0 turns right, pitch > 0 noses up. */
    var yawRate = 0f
        private set
    var pitchRate = 0f
        private set

    /** Current roll rate in radians per second; > 0 banks right (right wing down). */
    var rollRate = 0f
        private set

    /** Bank angle from turning, in radians; returns to 0 when the ship flies straight. */
    var bank = 0f
        private set

    var manoeuvre = SuperManoeuvre.Cruise
        private set

    private var elapsed = 0f
    private var duration = 0f
    private var targetYawRate = 0f
    private var targetPitchRate = 0f
    private var barrelRollDirection = 0f

    init {
        for (i in 0 until starCount) {
            relX[i] = randomCoordinate()
            relY[i] = randomCoordinate()
            relZ[i] = randomCoordinate()
        }
        startCruise()
    }

    /** Steers, turns the ship and flies it forward by [deltaSeconds]. */
    fun advance(deltaSeconds: Float) {
        updateAutopilot(deltaSeconds)
        turnShip(deltaSeconds)
        flyForward(deltaSeconds)
    }

    /** Projects the stars onto a [width] × [height] screen centred in the middle. */
    fun project(width: Float, height: Float) {
        val centerX = width / 2f
        val centerY = height / 2f
        val focal = minOf(width, height) / 2f / tan(FIELD_OF_VIEW / 2f)
        for (i in 0 until starCount) {
            val wasOnScreen = visible[i] && !wrapped[i]
            previousX[i] = screenX[i]
            previousY[i] = screenY[i]
            wrapped[i] = false

            val x = relX[i]; val y = relY[i]; val z = relZ[i]
            // Into ship space.
            val depth = x * forwardX + y * forwardY + z * forwardZ
            val distance = sqrt(x * x + y * y + z * z)
            val light = (1f - distance / CLOUD_RADIUS).coerceIn(0f, 1f)
            if (depth <= NEAR_PLANE || light <= 0f) {
                visible[i] = false
                hasPrevious[i] = false
                continue
            }
            val sideways = x * rightX + y * rightY + z * rightZ
            val upwards = x * upX + y * upY + z * upZ
            val sx = centerX + sideways / depth * focal
            val sy = centerY - upwards / depth * focal
            val onScreen = sx in 0f..width && sy in 0f..height
            screenX[i] = sx
            screenY[i] = sy
            brightness[i] = light
            visible[i] = onScreen
            hasPrevious[i] = onScreen && wasOnScreen
        }
    }

    /** Starts a banked turn with the given target rates. Used by the autopilot, and by tests. */
    fun startTurn(yawRate: Float, pitchRate: Float, seconds: Float = TURN_MAX_SECONDS) {
        manoeuvre = SuperManoeuvre.Turn
        elapsed = 0f
        duration = seconds
        targetYawRate = yawRate
        targetPitchRate = pitchRate
    }

    /** Starts a full 360° barrel roll. Used by the autopilot, and by tests. */
    fun startBarrelRoll(clockwise: Boolean) {
        manoeuvre = SuperManoeuvre.BarrelRoll
        elapsed = 0f
        duration = BARREL_ROLL_SECONDS
        targetYawRate = 0f
        targetPitchRate = 0f
        barrelRollDirection = if (clockwise) 1f else -1f
    }

    /** Starts a speed boost. Used by the autopilot, and by tests. */
    fun startBoost() {
        manoeuvre = SuperManoeuvre.Boost
        elapsed = 0f
        duration = BOOST_SECONDS
        targetYawRate = 0f
        targetPitchRate = 0f
    }

    /** Largest deviation of the ship axes from an orthonormal basis (for tests). */
    internal fun orientationError(): Float {
        val rightLength = sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ)
        val upLength = sqrt(upX * upX + upY * upY + upZ * upZ)
        val forwardLength = sqrt(forwardX * forwardX + forwardY * forwardY + forwardZ * forwardZ)
        val rightUp = rightX * upX + rightY * upY + rightZ * upZ
        val rightForward = rightX * forwardX + rightY * forwardY + rightZ * forwardZ
        val upForward = upX * forwardX + upY * forwardY + upZ * forwardZ
        return maxOf(
            abs(rightLength - 1f),
            abs(upLength - 1f),
            abs(forwardLength - 1f),
            abs(rightUp),
            abs(rightForward),
            abs(upForward),
        )
    }

    private fun updateAutopilot(deltaSeconds: Float) {
        elapsed += deltaSeconds
        val progress = if (duration > 0f) (elapsed / duration).coerceAtMost(1f) else 1f
        // Bell curve 0 → 1 → 0 over the manoeuvre, for things that swell and fade.
        val swell = sin(PI.toFloat() * progress)

        speed = if (manoeuvre == SuperManoeuvre.Boost) {
            cruiseSpeed * (1f + (SUPER_STARFIELD_BOOST_FACTOR - 1f) * swell)
        } else {
            cruiseSpeed
        }

        // Turn rates ease towards their targets, so turns start and end smoothly.
        val follow = (RATE_RESPONSE * deltaSeconds).coerceAtMost(1f)
        yawRate += (targetYawRate - yawRate) * follow
        pitchRate += (targetPitchRate - pitchRate) * follow

        // Lean into the turn, and level out again when it ends.
        val targetBank = yawRate * BANK_PER_YAW_RATE
        val bankRate = (targetBank - bank) * RATE_RESPONSE
        bank += bankRate * deltaSeconds

        // A barrel roll turns exactly 360°: the bell-shaped rate integrates to TAU.
        val barrelRate = if (manoeuvre == SuperManoeuvre.BarrelRoll) {
            barrelRollDirection * TAU / duration * (PI.toFloat() / 2f) * swell
        } else {
            0f
        }
        rollRate = bankRate + barrelRate

        if (progress >= 1f) {
            if (manoeuvre == SuperManoeuvre.Cruise) startRandomManoeuvre() else startCruise()
        }
    }

    private fun startCruise() {
        manoeuvre = SuperManoeuvre.Cruise
        elapsed = 0f
        duration = randomBetween(CRUISE_MIN_SECONDS, CRUISE_MAX_SECONDS)
        targetYawRate = 0f
        targetPitchRate = 0f
    }

    // Turns 50 %, boosts 30 %, barrel rolls 20 %.
    private fun startRandomManoeuvre() {
        val pick = random.nextFloat()
        when {
            pick < 0.5f -> startTurn(
                yawRate = randomBetween(TURN_MIN_YAW_RATE, TURN_MAX_YAW_RATE) * randomSign(),
                pitchRate = randomBetween(0f, TURN_MAX_PITCH_RATE) * randomSign(),
                seconds = randomBetween(TURN_MIN_SECONDS, TURN_MAX_SECONDS),
            )
            pick < 0.8f -> startBoost()
            else -> startBarrelRoll(clockwise = random.nextBoolean())
        }
    }

    // Rotates the ship axes by this frame's yaw, pitch and roll, then re-orthonormalises
    // them so rounding errors cannot build up over a long flight.
    private fun turnShip(deltaSeconds: Float) {
        rotateYaw(yawRate * deltaSeconds)
        rotatePitch(pitchRate * deltaSeconds)
        rotateRoll(rollRate * deltaSeconds)
        orthonormalise()
    }

    // Turn right: forward swings towards right.
    private fun rotateYaw(angle: Float) {
        if (angle == 0f) return
        val c = cos(angle); val s = sin(angle)
        val fx = forwardX * c + rightX * s; val fy = forwardY * c + rightY * s; val fz = forwardZ * c + rightZ * s
        val rx = rightX * c - forwardX * s; val ry = rightY * c - forwardY * s; val rz = rightZ * c - forwardZ * s
        forwardX = fx; forwardY = fy; forwardZ = fz
        rightX = rx; rightY = ry; rightZ = rz
    }

    // Nose up: forward swings towards up.
    private fun rotatePitch(angle: Float) {
        if (angle == 0f) return
        val c = cos(angle); val s = sin(angle)
        val fx = forwardX * c + upX * s; val fy = forwardY * c + upY * s; val fz = forwardZ * c + upZ * s
        val ux = upX * c - forwardX * s; val uy = upY * c - forwardY * s; val uz = upZ * c - forwardZ * s
        forwardX = fx; forwardY = fy; forwardZ = fz
        upX = ux; upY = uy; upZ = uz
    }

    // Bank right: the right wing dips (right swings towards -up).
    private fun rotateRoll(angle: Float) {
        if (angle == 0f) return
        val c = cos(angle); val s = sin(angle)
        val rx = rightX * c - upX * s; val ry = rightY * c - upY * s; val rz = rightZ * c - upZ * s
        val ux = upX * c + rightX * s; val uy = upY * c + rightY * s; val uz = upZ * c + rightZ * s
        rightX = rx; rightY = ry; rightZ = rz
        upX = ux; upY = uy; upZ = uz
    }

    // Gram-Schmidt: keep forward, make right perpendicular to it, derive up = forward × right.
    private fun orthonormalise() {
        var length = sqrt(forwardX * forwardX + forwardY * forwardY + forwardZ * forwardZ)
        forwardX /= length; forwardY /= length; forwardZ /= length
        val dot = rightX * forwardX + rightY * forwardY + rightZ * forwardZ
        rightX -= dot * forwardX; rightY -= dot * forwardY; rightZ -= dot * forwardZ
        length = sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ)
        rightX /= length; rightY /= length; rightZ /= length
        upX = forwardY * rightZ - forwardZ * rightY
        upY = forwardZ * rightX - forwardX * rightZ
        upZ = forwardX * rightY - forwardY * rightX
    }

    // The ship moves forward, so every star moves backwards relative to it. A star leaving
    // the cube re-enters on the opposite face with its other two coordinates re-randomised,
    // so the pattern never repeats.
    private fun flyForward(deltaSeconds: Float) {
        val step = speed * deltaSeconds
        val dx = forwardX * step; val dy = forwardY * step; val dz = forwardZ * step
        for (i in 0 until starCount) {
            relX[i] -= dx
            relY[i] -= dy
            relZ[i] -= dz
            when {
                relX[i] < -CLOUD_RADIUS || relX[i] > CLOUD_RADIUS -> {
                    relX[i] = wrap(relX[i]); relY[i] = randomCoordinate(); relZ[i] = randomCoordinate()
                    wrapped[i] = true
                }
                relY[i] < -CLOUD_RADIUS || relY[i] > CLOUD_RADIUS -> {
                    relY[i] = wrap(relY[i]); relX[i] = randomCoordinate(); relZ[i] = randomCoordinate()
                    wrapped[i] = true
                }
                relZ[i] < -CLOUD_RADIUS || relZ[i] > CLOUD_RADIUS -> {
                    relZ[i] = wrap(relZ[i]); relX[i] = randomCoordinate(); relY[i] = randomCoordinate()
                    wrapped[i] = true
                }
            }
        }
    }

    private fun wrap(value: Float): Float =
        if (value > CLOUD_RADIUS) value - 2f * CLOUD_RADIUS else value + 2f * CLOUD_RADIUS

    private fun randomCoordinate(): Float = (random.nextFloat() * 2f - 1f) * CLOUD_RADIUS

    private fun randomBetween(min: Float, max: Float): Float = min + random.nextFloat() * (max - min)

    private fun randomSign(): Float = if (random.nextBoolean()) 1f else -1f
}
