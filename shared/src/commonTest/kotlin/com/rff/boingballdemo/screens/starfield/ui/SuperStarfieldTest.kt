package com.rff.boingballdemo.screens.starfield.ui

import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SuperStarfieldTest {
    private val width = 320f
    private val height = 240f
    private val frame = 1f / 60f

    /** Average screen movement of stars visible before and after [step], as (dx, dy). */
    private fun averageMovement(field: SuperStarfield, step: () -> Unit): Pair<Float, Float> {
        field.project(width, height)
        val beforeX = field.screenX.copyOf()
        val beforeY = field.screenY.copyOf()
        val wasVisible = field.visible.copyOf()
        step()
        field.project(width, height)
        var dx = 0f
        var dy = 0f
        var count = 0
        for (i in 0 until field.starCount) {
            if (!wasVisible[i] || !field.visible[i]) continue
            dx += field.screenX[i] - beforeX[i]
            dy += field.screenY[i] - beforeY[i]
            count++
        }
        assertTrue(count > 0, "no star stayed visible")
        return dx / count to dy / count
    }

    @Test
    fun flyingStraightSpreadsStarsFromTheCenter() {
        val field = SuperStarfield(starCount = 200, random = Random(1))
        field.project(width, height)
        val before = List(field.starCount) {
            abs(field.screenX[it] - width / 2f) + abs(field.screenY[it] - height / 2f)
        }
        val wasVisible = field.visible.copyOf()
        // Short enough that the autopilot is still cruising.
        repeat(10) { field.advance(frame) }
        field.project(width, height)
        assertEquals(SuperManoeuvre.Cruise, field.manoeuvre)
        for (i in 0 until field.starCount) {
            if (!wasVisible[i] || !field.visible[i]) continue
            val after = abs(field.screenX[i] - width / 2f) + abs(field.screenY[i] - height / 2f)
            assertTrue(after >= before[i] - 1e-3f, "star $i moved towards the centre")
        }
    }

    @Test
    fun turningRightMovesTheSceneLeft() {
        // No forward speed, so only the turn moves the stars.
        val field = SuperStarfield(starCount = 300, random = Random(2), cruiseSpeed = 0f)
        field.startTurn(yawRate = 0.6f, pitchRate = 0f)
        val (dx, _) = averageMovement(field) { repeat(30) { field.advance(frame) } }
        assertTrue(dx < -1f, "dx=$dx")
    }

    @Test
    fun noseUpMovesTheSceneDown() {
        val field = SuperStarfield(starCount = 300, random = Random(3), cruiseSpeed = 0f)
        field.startTurn(yawRate = 0f, pitchRate = 0.4f)
        val (_, dy) = averageMovement(field) { repeat(30) { field.advance(frame) } }
        assertTrue(dy > 1f, "dy=$dy")
    }

    @Test
    fun turnsBankIntoTheTurnAndLevelOutAfterwards() {
        val field = SuperStarfield(starCount = 10, random = Random(4), cruiseSpeed = 0f)
        field.startTurn(yawRate = 0.6f, pitchRate = 0f, seconds = 2f)
        repeat(60) { field.advance(frame) }
        assertTrue(field.bank > 0.2f, "turning right should bank right, bank=${field.bank}")
        // Turn over, cruising again: yaw and bank decay back to level flight. Checked before
        // the shortest cruise (2 s) ends, so the autopilot has not started a new manoeuvre.
        repeat(60 + 108) { field.advance(frame) }
        assertEquals(SuperManoeuvre.Cruise, field.manoeuvre)
        assertTrue(abs(field.bank) < 0.05f, "bank=${field.bank}")
        assertTrue(abs(field.yawRate) < 0.05f, "yawRate=${field.yawRate}")
    }

    @Test
    fun barrelRollTurnsExactlyOnce() {
        val field = SuperStarfield(starCount = 10, random = Random(5), cruiseSpeed = 0f)
        field.startBarrelRoll(clockwise = true)
        var rolled = 0f
        repeat(10_000) {
            if (field.manoeuvre != SuperManoeuvre.BarrelRoll) return@repeat
            field.advance(frame / 4)
            rolled += field.rollRate * frame / 4
        }
        assertEquals(2f * kotlin.math.PI.toFloat(), rolled, 0.05f)
    }

    @Test
    fun boostSpeedsUpAndSlowsDownAgain() {
        val field = SuperStarfield(starCount = 10, random = Random(6))
        field.startBoost()
        var peak = 0f
        while (field.manoeuvre == SuperManoeuvre.Boost) {
            field.advance(frame)
            peak = maxOf(peak, field.speed)
        }
        assertTrue(peak > SUPER_STARFIELD_CRUISE_SPEED * 4f, "peak=$peak")
        assertEquals(SUPER_STARFIELD_CRUISE_SPEED, field.speed, 1e-4f)
    }

    @Test
    fun longFlightStaysStableAndPopulated() {
        val field = SuperStarfield(random = Random(7))
        val seen = mutableSetOf<SuperManoeuvre>()
        // Ten minutes of autopilot.
        repeat(36_000) {
            field.advance(frame)
            seen += field.manoeuvre
        }
        field.project(width, height)
        assertEquals(SuperManoeuvre.entries.toSet(), seen)
        assertTrue(field.orientationError() < 1e-4f, "axes drifted: ${field.orientationError()}")
        // Roughly the density of the flat starfield (about 200 stars on screen).
        val visible = field.visible.count { it }
        assertTrue(visible > 150, "only $visible stars visible")
        for (i in 0 until field.starCount) {
            assertTrue(field.brightness[i] in 0f..1f)
            if (field.visible[i]) {
                assertTrue(field.screenX[i] in 0f..width && field.screenY[i] in 0f..height)
                assertTrue(!field.screenX[i].isNaN() && !field.screenY[i].isNaN())
            }
        }
    }

    @Test
    fun streaksOnlyForStarsOnScreenInBothFrames() {
        val field = SuperStarfield(starCount = 200, random = Random(8))
        field.startBoost()
        repeat(60) { field.advance(frame); field.project(width, height) }
        for (i in 0 until field.starCount) {
            if (field.hasPrevious[i]) assertTrue(field.visible[i])
        }
        assertTrue(field.hasPrevious.any { it })
    }

    @Test
    fun sameSeedGivesSameFlight() {
        val a = SuperStarfield(starCount = 50, random = Random(9))
        val b = SuperStarfield(starCount = 50, random = Random(9))
        repeat(600) {
            a.advance(frame); a.project(width, height)
            b.advance(frame); b.project(width, height)
        }
        assertContentEquals(a.screenX, b.screenX)
        assertContentEquals(a.screenY, b.screenY)
    }
}
