package com.rff.boingballdemo.screens.boingball.ui

import androidx.compose.ui.graphics.Path
import com.rff.boingballdemo.utils.TAU
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The Boing Ball as a latitude/longitude mesh of [rows] × [columns] faces.
 *
 * All buffers are allocated once and rewritten by [update], so drawing a frame creates
 * no objects: no per-vertex points, no per-face paths.
 *
 * Vertex (row, column) lives at index `row * columns + column`. Longitude wraps, so the
 * last column shares its east edge with column 0.
 */
internal class BoingBallMesh(
    private val rows: Int = BOING_BALL_ROWS,
    private val columns: Int = BOING_BALL_COLUMNS,
) {
    private val vertexCount = (rows + 1) * columns

    // Unit-sphere vertices before rotation; constant.
    private val baseX = FloatArray(vertexCount)
    private val baseY = FloatArray(vertexCount)
    private val baseZ = FloatArray(vertexCount)

    // Rotated camera-space X/Y (Z only feeds the projection) and projected screen
    // coordinates, rewritten by update().
    private val rotatedX = FloatArray(vertexCount)
    private val rotatedY = FloatArray(vertexCount)
    internal val screenX = FloatArray(vertexCount)
    internal val screenY = FloatArray(vertexCount)

    /** Per face (`row * columns + column`): true when it faces the camera. */
    internal val faceVisible = BooleanArray(rows * columns)

    init {
        for (row in 0..rows) {
            val lat = ((PI / rows) * (row - rows / 2f)).toFloat() // -π/2 → +π/2
            for (column in 0 until columns) {
                val lon = (TAU / columns) * column
                val index = row * columns + column
                baseX[index] = cos(lat) * cos(lon)
                baseY[index] = sin(lat)
                baseZ[index] = cos(lat) * sin(lon)
            }
        }
    }

    /**
     * Spins the ball by [rotationAngle] around its axis, tilts it by [tiltAngle] on screen,
     * projects it around ([cx], [cy]) and culls the faces turned away from the camera.
     */
    fun update(rotationAngle: Float, tiltAngle: Float, cx: Float, cy: Float, radius: Float) {
        val cosSpin = cos(rotationAngle)
        val sinSpin = sin(rotationAngle)
        val cosTilt = cos(tiltAngle)
        val sinTilt = sin(tiltAngle)
        // Simple perspective: focal length = 2 × radius.
        val focal = 2f * radius

        for (i in 0 until vertexCount) {
            // Spin around Y, then tilt around Z (poles stay on the silhouette).
            val spunX = baseX[i] * cosSpin + baseZ[i] * sinSpin
            val spunZ = -baseX[i] * sinSpin + baseZ[i] * cosSpin
            val x = spunX * cosTilt - baseY[i] * sinTilt
            val y = spunX * sinTilt + baseY[i] * cosTilt
            rotatedX[i] = x
            rotatedY[i] = y

            val scale = focal / (focal - spunZ)
            screenX[i] = cx + x * radius * scale
            screenY[i] = cy - y * radius * scale
        }

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                faceVisible[row * columns + column] = isFrontFacing(row, column)
            }
        }
    }

    /**
     * Rebuilds the checkerboard into two reused paths, one per color. The mesh is convex
     * and back faces are culled, so faces never overlap and need no depth sorting.
     */
    fun buildPaths(themePath: Path, altPath: Path) {
        themePath.rewind()
        altPath.rewind()
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                if (!faceVisible[row * columns + column]) continue
                val path = if (((row + column) and 1) == 0) themePath else altPath
                addFace(path, row, column)
            }
        }
    }

    // Faces are quads v1→v2→v3→v4 (SW, SE, NE, NW). At the poles every longitude collapses
    // to one point, so those faces are triangles:
    //   south (row 0):    v1 == v2  →  v1→v3→v4
    //   north (last row): v3 == v4  →  v1→v2→v3
    private fun addFace(path: Path, row: Int, column: Int) {
        val v1 = vertex(row, column)
        val v2 = vertex(row, column + 1)
        val v3 = vertex(row + 1, column + 1)
        val v4 = vertex(row + 1, column)

        path.moveTo(screenX[v1], screenY[v1])
        if (row != 0) path.lineTo(screenX[v2], screenY[v2])
        path.lineTo(screenX[v3], screenY[v3])
        if (row != rows - 1) path.lineTo(screenX[v4], screenY[v4])
        path.close()
    }

    // Camera is on +Z. Quad winding is clockwise from outside, so normals point inward;
    // inward · (0, 0, -1) > 0 keeps the front (+Z) hemisphere. That dot product is -normal.z.
    private fun isFrontFacing(row: Int, column: Int): Boolean {
        val v1 = vertex(row, column)
        // South cannot use (v2−v1)×(v3−v1): that edge is zero. North matches the regular quad.
        val a = if (row == 0) vertex(row + 1, column + 1) else vertex(row, column + 1)
        val b = if (row == 0) vertex(row + 1, column) else vertex(row + 1, column + 1)

        val ax = rotatedX[a] - rotatedX[v1]
        val ay = rotatedY[a] - rotatedY[v1]
        val bx = rotatedX[b] - rotatedX[v1]
        val by = rotatedY[b] - rotatedY[v1]
        val normalZ = ax * by - ay * bx
        return -normalZ > 0f
    }

    private fun vertex(row: Int, column: Int): Int = row * columns + column % columns
}
