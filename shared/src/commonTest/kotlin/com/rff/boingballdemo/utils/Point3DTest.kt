package com.rff.boingballdemo.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Point3DTest {

    @Test
    fun minus() {
        val result = Point3D(5f, 3f, 1f) - Point3D(2f, 1f, 4f)
        assertEquals(Point3D(3f, 2f, -3f), result)
    }

    @Test
    fun crossOrthogonal() {
        val result = Point3D(1f, 0f, 0f).cross(Point3D(0f, 1f, 0f))
        assertEquals(Point3D(0f, 0f, 1f), result)
    }

    @Test
    fun crossSelfIsZero() {
        val v = Point3D(3f, -2f, 5f)
        val result = v.cross(v)
        assertTrue(result.isZero())
    }

    @Test
    fun dot() {
        val result = Point3D(1f, 2f, 3f).dot(Point3D(4f, 5f, 6f))
        assertEquals(32f, result)
    }

    @Test
    fun dotOrthogonal() {
        val result = Point3D(1f, 0f, 0f).dot(Point3D(0f, 1f, 0f))
        assertEquals(0f, result)
    }

    @Test
    fun rotateYZero() {
        val p = Point3D(2f, 3f, 4f)
        val result = p.rotateY(0f)
        assertEquals(p, result)
    }

    @Test
    fun rotateY90Deg() {
        val result = Point3D(1f, 0f, 0f).rotateY((PI / 2f).toFloat())
        assertEquals(0f, result.x, 1e-6f)
        assertEquals(0f, result.y, 1e-6f)
        assertEquals(-1f, result.z, 1e-6f)
    }

    @Test
    fun rotateY180Deg() {
        val result = Point3D(1f, 0f, 0f).rotateY(PI.toFloat())
        assertEquals(-1f, result.x, 1e-6f)
        assertEquals(0f, result.y, 1e-6f)
        assertEquals(0f, result.z, 1e-6f)
    }

    @Test
    fun rotateY360Deg() {
        val result = Point3D(1f, 0f, 0f).rotateY((2f * PI).toFloat())
        assertEquals(1f, result.x, 1e-6f)
        assertEquals(0f, result.y, 1e-6f)
        assertEquals(0f, result.z, 1e-6f)
    }

    @Test
    fun rotateZZero() {
        val p = Point3D(2f, 3f, 4f)
        val result = p.rotateZ(0f)
        assertEquals(p, result)
    }

    @Test
    fun rotateZ90Deg() {
        val result = Point3D(1f, 0f, 0f).rotateZ((PI / 2f).toFloat())
        assertEquals(0f, result.x, 1e-6f)
        assertEquals(1f, result.y, 1e-6f)
        assertEquals(0f, result.z, 1e-6f)
    }

    @Test
    fun rotateZ180Deg() {
        val result = Point3D(1f, 0f, 0f).rotateZ(PI.toFloat())
        assertEquals(-1f, result.x, 1e-6f)
        assertEquals(0f, result.y, 1e-6f)
        assertEquals(0f, result.z, 1e-6f)
    }

    @Test
    fun rotateZ360Deg() {
        val result = Point3D(1f, 0f, 0f).rotateZ((2f * PI).toFloat())
        assertEquals(1f, result.x, 1e-6f)
        assertEquals(0f, result.y, 1e-6f)
        assertEquals(0f, result.z, 1e-6f)
    }

    @Test
    fun projectOrigin() {
        val result = Point3D(0f, 0f, 0f).project(0f, 0f, 100f)
        assertEquals(Offset(0f, 0f), result)
    }

    @Test
    fun projectOffset() {
        val result = Point3D(0.5f, 0.5f, 0f).project(100f, 100f, 50f)
        assertEquals(Offset(125f, 75f), result)
    }

    @Test
    fun projectDepth() {
        val result = Point3D(0f, 0f, 1f).project(0f, 0f, 100f)
        val expectedScale = (200f / (200f - 1f))
        assertEquals(Offset(0f, 0f), result)
    }

    @Test
    fun isZeroTrue() {
        assertTrue(Point3D(0f, 0f, 0f).isZero())
    }

    @Test
    fun isZeroTrueWithinEpsilon() {
        assertTrue(Point3D(EPSILON / 2f, 0f, 0f).isZero())
    }

    @Test
    fun isZeroFalse() {
        assertFalse(Point3D(1f, 0f, 0f).isZero())
    }
}
