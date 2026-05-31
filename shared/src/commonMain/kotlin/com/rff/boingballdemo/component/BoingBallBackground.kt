package com.rff.boingballdemo.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.wireframeColor

private const val STROKE_WIDTH = 2f
private const val TOTAL_CELLS_WITH_MARGIN_HORIZONTALLY = 17
private const val TOTAL_FLOOR_CELLS = 6

@Composable
fun BoingBallBackground(
    modifier: Modifier = Modifier,
    horizonRatio: Float = 0.9f, // 0..1 where floor meets wall
    floorRows: Int = TOTAL_FLOOR_CELLS, // how many “bands” on the floor
) {
    Canvas(modifier = modifier) {
        //println("Background canvas size = $size")
        val canvasWidth = size.width
        val canvasHeight = size.height
        // 17 include 2 cells of margin, visible horizontally will be 15 cells
        val cellSize = (canvasWidth / TOTAL_CELLS_WITH_MARGIN_HORIZONTALLY).toInt().toFloat()
        // Y coordinate of where the floor meets the wall
        val horizon = canvasHeight * horizonRatio

        // *** BACK WALL ***
        // verticals
        val cellsCountH: Int = (canvasWidth / cellSize).toInt() - 2
        val cellsCountV: Int = (horizon / cellSize).toInt() - 1
        val cellsTotalWidth = cellsCountH * cellSize
        val cellsTotalHeight = cellsCountV * cellSize
        var x = (canvasWidth - cellsTotalWidth) / 2
        var y = cellSize
        while (x <= (canvasWidth - cellsTotalWidth) / 2 + cellsTotalWidth) {
            drawLine(wireframeColor, Offset(x, y), Offset(x, y + cellsTotalHeight), STROKE_WIDTH)
            x += cellSize
        }
        // horizontals
        x = (canvasWidth - cellsTotalWidth) / 2
        while (y <= horizon) {
            drawLine(wireframeColor, Offset(x, y), Offset(x + cellsTotalWidth, y), STROKE_WIDTH)
            y += cellSize
        }

        // *** FLOOR ***
        val horizonY = cellSize + cellsTotalHeight
        val centerX = size.width / 2f
        val maxSlope = 1f

        val xList = mutableListOf<Pair<Float, Float>>()
        while (x <= (canvasWidth - cellsTotalWidth) / 2 + cellsTotalWidth) {
            val dxNorm = (x - centerX) / centerX
            val slope = dxNorm * maxSlope
            val dy = canvasHeight - horizonY
            xList.add(x to x + slope * dy)
            x += cellSize
        }

        xList.forEach { (x, endX) ->
            drawLine(wireframeColor, Offset(x, horizonY), Offset(endX, canvasHeight), STROKE_WIDTH)
        }

        for (i in 1 until floorRows) {
            val t = i / (floorRows - 1f)
            val ty = t * t
            val dy = horizonY + ty * (canvasHeight - horizonY)
            val firstHX = xList.first().first
            val lastHX = xList.last().first
            val firstBX = xList.first().second
            val lastBX = xList.last().second

            val frac = (dy - horizonY) / (canvasHeight - horizonY)
            val leftX = firstHX + (firstBX - firstHX) * frac
            val rightX = lastHX + (lastBX - lastHX) * frac

            drawLine(wireframeColor, Offset(leftX, dy), Offset(rightX, dy), STROKE_WIDTH)
        }
    }
}

@Preview
@Composable
private fun BoingBallPreview() {
    BoingBallDemoTheme {
        Box(modifier = Modifier.size(300.dp)) {
            BoingBallBackground(modifier = Modifier.fillMaxSize())
        }
    }
}
