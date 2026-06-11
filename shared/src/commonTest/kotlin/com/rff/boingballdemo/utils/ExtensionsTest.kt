package com.rff.boingballdemo.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExtensionsTest {

    @Test
    fun toRadiansZero() {
        assertEquals(0f, 0f.toRadians())
    }

    @Test
    fun toRadians90() {
        assertEquals((PI / 2f).toFloat(), 90f.toRadians())
    }

    @Test
    fun toRadians180() {
        assertEquals(PI.toFloat(), 180f.toRadians())
    }

    @Test
    fun toRadians360() {
        assertEquals((2f * PI).toFloat(), 360f.toRadians())
    }

    @Test
    fun toRadiansNegative() {
        assertEquals((-PI / 2f).toFloat(), (-90f).toRadians())
    }

    @Test
    fun sameAsIdentical() {
        assertTrue(Offset(3f, 4f).sameAs(Offset(3f, 4f)))
    }

    @Test
    fun sameAsWithinEpsilon() {
        assertTrue(Offset(1f, 1f).sameAs(Offset(1f + EPSILON / 2f, 1f - EPSILON / 2f)))
    }

    @Test
    fun sameAsDifferent() {
        assertFalse(Offset(1f, 1f).sameAs(Offset(2f, 1f)))
    }

    @Test
    fun sameAsBorderline() {
        assertFalse(Offset(0f, 0f).sameAs(Offset(EPSILON * 2f, 0f)))
    }
}
