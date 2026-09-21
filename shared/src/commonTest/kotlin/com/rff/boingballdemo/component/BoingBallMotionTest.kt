package com.rff.boingballdemo.component

import com.rff.boingballdemo.utils.TAU
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoingBallMotionTest {

    @Test
    fun startsCenteredHorizontally() {
        assertEquals(0.5f, HORIZONTAL_START_FRACTION)
    }

    @Test
    fun firstMoveIsLeft() {
        assertTrue(INITIAL_MOVING_LEFT)
        assertEquals(0f, nextHorizontalFraction(INITIAL_MOVING_LEFT))
    }

    @Test
    fun nextTargetIsRightWhenMovingRight() {
        assertEquals(1f, nextHorizontalFraction(movingLeft = false))
    }

    @Test
    fun leftwardRotationTurnsFrontFacesRight() {
        assertEquals(1f, rotationSign(movingLeft = true))
        assertEquals(-1f, rotationSign(movingLeft = false))
    }

    @Test
    fun firstTravelFromCenterTakesHalfDuration() {
        assertEquals(
            HORIZONTAL_TRAVEL_MS / 2,
            horizontalTravelDurationMs(HORIZONTAL_START_FRACTION, 0f)
        )
    }

    @Test
    fun fullWidthTravelUsesFullDuration() {
        assertEquals(HORIZONTAL_TRAVEL_MS, horizontalTravelDurationMs(0f, 1f))
        assertEquals(HORIZONTAL_TRAVEL_MS, horizontalTravelDurationMs(1f, 0f))
    }

    @Test
    fun palRotationMatchesFourteenRegisterCycleAt50Hz() {
        val degreesPerCycle = 360f * 2f / BOING_BALL_COLUMNS
        val degreesPerSecond = degreesPerCycle * VideoSystem.PAL.vblankHz / ORIGINAL_COLOR_CYCLE_LENGTH
        val radiansPerSecond = degreesPerSecond * (PI.toFloat() / 180f)
        assertEquals(45f, degreesPerCycle)
        assertEquals(radiansPerSecond, rotationSpeedRadiansPerSecond(VideoSystem.PAL.vblankHz), 1e-5f)
    }

    @Test
    fun ntscRotationIsSixFifthsOfPal() {
        val pal = rotationSpeedRadiansPerSecond(VideoSystem.PAL.vblankHz)
        val ntsc = rotationSpeedRadiansPerSecond(VideoSystem.NTSC.vblankHz)
        assertEquals(pal * 60f / 50f, ntsc, 1e-5f)
        assertEquals(
            (TAU * 2f / BOING_BALL_COLUMNS) * (VideoSystem.NTSC.vblankHz.toFloat() / ORIGINAL_COLOR_CYCLE_LENGTH),
            ntsc,
            1e-6f,
        )
    }

    @Test
    fun palKeepsReferenceDurations() {
        assertEquals(HORIZONTAL_TRAVEL_MS, scaledDurationMs(HORIZONTAL_TRAVEL_MS, VideoSystem.PAL.vblankHz))
        assertEquals(VERTICAL_FALL_MS, scaledDurationMs(VERTICAL_FALL_MS, VideoSystem.PAL.vblankHz))
        assertEquals(VERTICAL_RISE_MS, scaledDurationMs(VERTICAL_RISE_MS, VideoSystem.PAL.vblankHz))
    }

    @Test
    fun ntscDurationsScaleFromPal() {
        assertEquals(2500, scaledDurationMs(HORIZONTAL_TRAVEL_MS, VideoSystem.NTSC.vblankHz))
        assertEquals(416, scaledDurationMs(VERTICAL_FALL_MS, VideoSystem.NTSC.vblankHz))
        assertEquals(750, scaledDurationMs(VERTICAL_RISE_MS, VideoSystem.NTSC.vblankHz))
    }

    @Test
    fun fromLabelUsesResolvedResourceStrings() {
        val labels = mapOf(
            VideoSystem.PAL to "PAL",
            VideoSystem.NTSC to "NTSC",
        )
        assertEquals(VideoSystem.PAL, VideoSystem.fromLabel("PAL", labels))
        assertEquals(VideoSystem.NTSC, VideoSystem.fromLabel("NTSC", labels))
        assertEquals(null, VideoSystem.fromLabel("SECAM", labels))
    }
}
