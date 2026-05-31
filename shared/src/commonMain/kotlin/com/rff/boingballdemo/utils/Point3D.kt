package com.rff.boingballdemo.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class Point3D(val x: Float, val y: Float, val z: Float) {
    operator fun minus(o: Point3D) = Point3D(x - o.x, y - o.y, z - o.z)

    fun cross(point: Point3D) = Point3D(
        y * point.z - z * point.y,
        z * point.x - x * point.z,
        x * point.y - y * point.x
    )

    /** iloczyn skalarny */
    infix fun dot(o: Point3D) = x * o.x + y * o.y + z * o.z

    fun rotateY(angle: Float): Point3D {
        val c = cos(angle)
        val s = sin(angle)

        return Point3D(
            x = x * c + z * s,
            y = y,
            z = -x * s + z * c
        )
    }

    fun rotateZ(angle: Float): Point3D {
        val c = cos(angle)
        val s = sin(angle)

        return Point3D(
            x = x * c - y * s,
            y = x * s + y * c,
            z = z
        )
    }

    /** simple perspective: focal = 2 × radius */
    fun project(screenCx: Float, screenCy: Float, radius: Float): Offset {
        val focal = 2f * radius
        val scale = focal / (focal - z)     // z ∈ [-1, +1]  ⇒ scale ≈ [½, ∞)
        return Offset(
            screenCx + x * radius * scale,
            screenCy - y * radius * scale
        )
    }

    fun isZero() = abs(x) < EPSILON && abs(y) < EPSILON && abs(z) < EPSILON
}
