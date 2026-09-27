package com.rff.boingballdemo.screens.starfield.ui

import com.rff.boingballdemo.utils.TAU
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StarfieldTest {
    private val width = 320f
    private val height = 240f

    private fun distanceFromCenter(field: Starfield, i: Int) =
        abs(field.screenX[i] - width / 2f) + abs(field.screenY[i] - height / 2f)

    @Test
    fun starsMoveOutwardAndGetBrighter() {
        val field = Starfield(starCount = 50, random = Random(1))
        field.project(width, height)
        val before = List(field.starCount) { distanceFromCenter(field, it) to field.brightness[it] }
        // Stars already off screen are recycled to the far plane by the next advance().
        val wasVisible = field.visible.copyOf()

        // A short step, so no star reaches the near plane.
        field.advance(deltaSeconds = 0.01f)
        field.project(width, height)

        var checked = 0
        for (i in 0 until field.starCount) {
            if (!wasVisible[i] || !field.visible[i]) continue
            val (distance, brightness) = before[i]
            assertTrue(distanceFromCenter(field, i) >= distance, "star $i moved inward")
            assertTrue(field.brightness[i] > brightness, "star $i got dimmer")
            checked++
        }
        assertTrue(checked > 0)
    }

    @Test
    fun fieldStaysPopulatedOverTime() {
        val field = Starfield(starCount = 200, random = Random(2))
        // About 25 s at 60 fps: every star is recycled many times.
        repeat(1_500) {
            field.advance(deltaSeconds = 1f / 60f)
            field.project(width, height)
        }
        val visible = field.visible.count { it }
        assertTrue(visible > field.starCount / 2, "only $visible of ${field.starCount} stars visible")
        for (i in 0 until field.starCount) {
            assertTrue(field.brightness[i] in 0f..1f, "brightness out of range: ${field.brightness[i]}")
            if (field.visible[i]) {
                assertTrue(field.screenX[i] in 0f..width && field.screenY[i] in 0f..height)
            }
        }
    }

    @Test
    fun sameSeedGivesSameField() {
        val a = Starfield(starCount = 30, random = Random(3))
        val b = Starfield(starCount = 30, random = Random(3))
        repeat(100) {
            a.advance(0.02f); a.project(width, height)
            b.advance(0.02f); b.project(width, height)
        }
        assertContentEquals(a.screenX, b.screenX)
        assertContentEquals(a.screenY, b.screenY)
    }

    @Test
    fun cruisesStraightBeforeTheFirstRoll() {
        val field = Starfield(starCount = 10, random = Random(4))
        // Shorter than the minimum cruise.
        repeat(((STARFIELD_CRUISE_MIN_SECONDS - 0.5f) * 60).toInt()) { field.advance(1f / 60f) }
        assertFalse(field.isRolling)
        assertEquals(0f, field.rollAngle)
    }

    @Test
    fun rollsAQuarterTurnEitherWayAndSettles() {
        val field = Starfield(starCount = 10, random = Random(5))
        val quarter = STARFIELD_ROLL_ANGLE
        var rolls = 0
        var clockwise = 0
        var wasRolling = false
        var angleBeforeRoll = 0f
        // About ten minutes at 60 fps.
        repeat(36_000) {
            field.advance(1f / 60f)
            if (field.isRolling && !wasRolling) angleBeforeRoll = field.rollAngle
            if (!field.isRolling && wasRolling) {
                rolls++
                // Every roll ends exactly on a quarter turn, within [0, 2π).
                val quarters = field.rollAngle / quarter
                assertTrue(abs(quarters - round(quarters)) < 1e-4f, "angle ${field.rollAngle} is not a quarter turn")
                assertTrue(field.rollAngle >= 0f && field.rollAngle < TAU)
                val turn = (field.rollAngle - angleBeforeRoll).mod(TAU)
                assertTrue(abs(turn - quarter) < 1e-3f || abs(turn - 3 * quarter) < 1e-3f, "turned by $turn")
                if (abs(turn - quarter) < 1e-3f) clockwise++
            }
            wasRolling = field.isRolling
        }
        // 10 minutes / (3..7 s cruise + 2..3 s manoeuvre), about half of them rolls.
        assertTrue(rolls in 20..80, "rolls=$rolls")
        assertTrue(clockwise in 1 until rolls, "only one direction: $clockwise of $rolls clockwise")
    }

    @Test
    fun rollRotatesTheProjectionAroundTheCenter() {
        val straight = Starfield(starCount = 40, random = Random(6))
        val rolled = Starfield(starCount = 40, random = Random(6))
        straight.project(width, height)
        // Run the copy through its first roll with the stars held still (speed 0): a pure
        // rotation keeps every star at the same distance from the centre.
        while (!rolled.isRolling) rolled.advance(1f / 60f, speed = 0f)
        while (rolled.isRolling) rolled.advance(1f / 60f, speed = 0f)
        rolled.project(width, height)

        var checked = 0
        for (i in 0 until straight.starCount) {
            if (!straight.visible[i] || !rolled.visible[i]) continue
            assertEquals(distanceFromCenterSquared(straight, i), distanceFromCenterSquared(rolled, i), 1f)
            checked++
        }
        assertTrue(checked > 0)
    }

    private fun distanceFromCenterSquared(field: Starfield, i: Int): Float {
        val dx = field.screenX[i] - width / 2f
        val dy = field.screenY[i] - height / 2f
        return dx * dx + dy * dy
    }

    @Test
    fun scheduleMixesRollsAndAllDriftDirections() {
        val field = Starfield(starCount = 10, random = Random(7))
        val drifts = mutableSetOf<DriftDirection>()
        var rolled = false
        repeat(36_000) {
            field.advance(1f / 60f)
            field.drift?.let(drifts::add)
            if (field.isRolling) rolled = true
            assertFalse(field.isRolling && field.drift != null, "roll and drift at once")
        }
        assertTrue(rolled)
        assertEquals(DriftDirection.entries.toSet(), drifts)
    }

    @Test
    fun driftMovesStarsOppositeToTheShipWithParallax() {
        for (direction in DriftDirection.entries) {
            val field = Starfield(starCount = 80, random = Random(8))
            field.project(width, height)
            val beforeX = field.screenX.copyOf()
            val beforeY = field.screenY.copyOf()
            val wasVisible = field.visible.copyOf()

            // Hold the forward flight so only the drift moves the stars; stop mid-drift.
            field.startDrift(direction)
            repeat(60) { field.advance(1f / 60f, speed = 0f) }
            field.project(width, height)

            var near = 0f to 0
            var far = 0f to 0
            for (i in 0 until field.starCount) {
                if (!wasVisible[i] || !field.visible[i]) continue
                val moved = (field.screenX[i] - beforeX[i]) * -direction.dx +
                    (field.screenY[i] - beforeY[i]) * -direction.dy
                assertTrue(moved > 0f, "$direction: star $i did not move opposite to the ship")
                if (field.brightness[i] > 0.5f) near = near.first + moved to near.second + 1
                else far = far.first + moved to far.second + 1
            }
            assertTrue(near.second > 0 && far.second > 0, "$direction: need near and far stars")
            assertTrue(near.first / near.second > far.first / far.second, "$direction: no parallax")
        }
    }

    @Test
    fun driftKeepsTheLeadingSideAtNormalDensity() {
        // A wide screen drifting right: the right side extends past the central square, so
        // without refilling it would thin out to about a third of its normal density.
        val wide = 400f
        val short = 200f
        fun averageInRightQuarter(drift: Boolean): Float {
            var stars = 0
            var frames = 0
            for (seed in 0 until 10) {
                val field = Starfield(starCount = 200, random = Random(seed))
                field.project(wide, short)
                repeat(120) { field.advance(1f / 60f); field.project(wide, short) }
                if (drift) field.startDrift(DriftDirection.Right)
                // Measure through the middle of the drift, where it is fastest.
                repeat((STARFIELD_DRIFT_SECONDS * 60).toInt()) { frame ->
                    field.advance(1f / 60f)
                    field.project(wide, short)
                    if (frame in 60..150) {
                        frames++
                        stars += (0 until field.starCount).count {
                            field.visible[it] && field.screenX[it] > wide * 0.75f
                        }
                    }
                }
            }
            return stars.toFloat() / frames
        }

        val normal = averageInRightQuarter(drift = false)
        val drifting = averageInRightQuarter(drift = true)
        assertTrue(drifting in normal * 0.75f..normal * 1.25f, "leading quarter $drifting vs normal $normal")
    }

    @Test
    fun driftEndsAndReturnsToCruise() {
        val field = Starfield(starCount = 10, random = Random(10))
        field.startDrift(DriftDirection.Up)
        repeat((STARFIELD_DRIFT_SECONDS * 60).toInt() + 2) { field.advance(1f / 60f) }
        assertEquals(null, field.drift)
        assertFalse(field.isRolling)
    }
}
