package com.rff.boingballdemo.screens.boingball.ui

import com.rff.boingballdemo.component.VideoSystem
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
    fun palKeepsReferenceSpeeds() {
        val pal = VideoSystem.PAL.vblankHz
        assertEquals(ROTATION_SPEED_RADIANS_PER_SECOND, rotationSpeedRadiansPerSecond(pal))
        assertEquals(HORIZONTAL_TRAVEL_MS, scaledDurationMs(HORIZONTAL_TRAVEL_MS, pal))
        assertEquals(VERTICAL_FALL_MS, scaledDurationMs(VERTICAL_FALL_MS, pal))
        assertEquals(VERTICAL_RISE_MS, scaledDurationMs(VERTICAL_RISE_MS, pal))
    }

    @Test
    fun ntscIsSixFifthsOfPal() {
        val ntsc = VideoSystem.NTSC.vblankHz
        assertEquals(
            ROTATION_SPEED_RADIANS_PER_SECOND * 60f / 50f,
            rotationSpeedRadiansPerSecond(ntsc),
            1e-5f,
        )
        assertEquals(2500, scaledDurationMs(HORIZONTAL_TRAVEL_MS, ntsc))
        assertEquals(500, scaledDurationMs(VERTICAL_FALL_MS, ntsc))
        assertEquals(916, scaledDurationMs(VERTICAL_RISE_MS, ntsc))
    }

    @Test
    fun firstTravelFromCenterTakesHalfDuration() {
        assertEquals(
            HORIZONTAL_TRAVEL_MS / 2,
            horizontalTravelDurationMs(HORIZONTAL_START_FRACTION, 0f, HORIZONTAL_TRAVEL_MS)
        )
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
