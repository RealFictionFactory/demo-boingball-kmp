package com.rff.boingballdemo.screens.boingball.ui

import com.rff.boingballdemo.utils.Point3D
import com.rff.boingballdemo.utils.TAU
import com.rff.boingballdemo.utils.toRadians
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoingBallMeshTest {
    private val rows = BOING_BALL_ROWS
    private val columns = BOING_BALL_COLUMNS

    // The previous per-frame implementation, built from Point3D objects.
    private fun referenceVertex(row: Int, column: Int, spin: Float, tilt: Float): Point3D {
        val lat = ((PI / rows) * (row - rows / 2f)).toFloat()
        val lon = (TAU / columns) * (column % columns)
        return Point3D(cos(lat) * cos(lon), sin(lat), cos(lat) * sin(lon))
            .rotateY(spin)
            .rotateZ(tilt)
    }

    private fun referenceVisible(row: Int, column: Int, spin: Float, tilt: Float): Boolean {
        val v1 = referenceVertex(row, column, spin, tilt)
        val v2 = referenceVertex(row, column + 1, spin, tilt)
        val v3 = referenceVertex(row + 1, column + 1, spin, tilt)
        val v4 = referenceVertex(row + 1, column, spin, tilt)
        val normal = if (row == 0) (v3 - v1).cross(v4 - v1) else (v2 - v1).cross(v3 - v1)
        return (normal dot Point3D(0f, 0f, -1f)) > 0f
    }

    @Test
    fun matchesPreviousImplementation() {
        val mesh = BoingBallMesh()
        val tilt = (-23.5f).toRadians()
        val (cx, cy, radius) = Triple(400f, 300f, 120f)

        for (spin in listOf(0f, 0.37f, 1.9f, 3.3f, -2.1f, 12.7f)) {
            mesh.update(spin, tilt, cx, cy, radius)
            for (row in 0..rows) {
                for (column in 0 until columns) {
                    val expected = referenceVertex(row, column, spin, tilt).project(cx, cy, radius)
                    val index = row * columns + column
                    assertEquals(expected.x, mesh.screenX[index], 1e-3f, "x row=$row column=$column spin=$spin")
                    assertEquals(expected.y, mesh.screenY[index], 1e-3f, "y row=$row column=$column spin=$spin")
                }
            }
            for (row in 0 until rows) {
                for (column in 0 until columns) {
                    assertEquals(
                        referenceVisible(row, column, spin, tilt),
                        mesh.faceVisible[row * columns + column],
                        "face row=$row column=$column spin=$spin",
                    )
                }
            }
        }
    }

    @Test
    fun aboutHalfTheFacesAreVisible() {
        val mesh = BoingBallMesh()
        mesh.update(0.5f, (-23.5f).toRadians(), 0f, 0f, 100f)
        val visible = mesh.faceVisible.count { it }
        val total = rows * columns
        assertTrue(abs(visible - total / 2) <= columns, "visible=$visible of $total")
    }
}
