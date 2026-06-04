package com.rff.boingballdemo.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.amigaOs13Orange
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.whiteColor

private val secondHandColorOs30 = Color(0xFF4477CC)

@Composable
fun AmigaClockFace(
    hour: Int,
    minute: Int,
    second: Int,
    osStyle: OSStyle,
    modifier: Modifier = Modifier,
) {
    val secondColor = if (osStyle == OSStyle.AmigaOS13) amigaOs13Orange else secondHandColorOs30

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val center = Offset(cx, cy)
        val r = minOf(size.width, size.height) / 2f * 0.96f
        val faceBorder = 2.5.dp.toPx()
        val tickStroke = 1.5.dp.toPx()

        drawCircle(color = whiteColor, radius = r, center = center)
        drawCircle(color = blackColor, radius = r, center = center, style = Stroke(width = faceBorder))

        drawMinuteTicks(center, cx, cy, r, tickStroke)
        drawHourDiamonds(center, cx, cy, r)

        val hourAngle = (hour % 12 + minute / 60f + second / 3600f) * 30f
        val minuteAngle = (minute + second / 60f) * 6f
        val secondAngle = second * 6f

        rotate(hourAngle, center) { drawClockHand(cx, cy, r * 0.60f, r * 0.12f, r * 0.055f, blackColor) }
        rotate(minuteAngle, center) { drawClockHand(cx, cy, r * 0.8f, r * 0.10f, r * 0.040f, blackColor) }
        rotate(secondAngle, center) {
            drawLine(
                color = secondColor,
                start = Offset(cx, cy + r * 0.18f),
                end = Offset(cx, cy - r * 0.83f),
                strokeWidth = tickStroke,
            )
        }

        drawCircle(color = blackColor, radius = r * 0.032f, center = center)
    }
}

private fun DrawScope.drawMinuteTicks(center: Offset, cx: Float, cy: Float, r: Float, strokeWidth: Float) {
    for (i in 0..59) {
        if (i % 5 != 0) {
            rotate(i * 6f, center) {
                drawLine(
                    color = blackColor,
                    start = Offset(cx, cy - r * 0.91f),
                    end = Offset(cx, cy - r * 0.97f),
                    strokeWidth = strokeWidth,
                )
            }
        }
    }
}

private fun DrawScope.drawHourDiamonds(center: Offset, cx: Float, cy: Float, r: Float) {
    for (i in 0..11) {
        val size = r * 0.05f
        val dist = r * 0.92f
        rotate(i * 30f, center) {
            val my = cy - dist
            val path = Path().apply {
                moveTo(cx, my - size)
                lineTo(cx + size * 0.62f, my)
                lineTo(cx, my + size)
                lineTo(cx - size * 0.62f, my)
                close()
            }
            drawPath(path, blackColor)
        }
    }
}

private fun DrawScope.drawClockHand(
    cx: Float, cy: Float,
    length: Float, backLength: Float, halfWidth: Float,
    color: Color,
) {
    val backHalfWidth = halfWidth * 0.42f
    val path = Path().apply {
        moveTo(cx, cy - length)
        lineTo(cx - halfWidth, cy - length * 0.28f)
        lineTo(cx - backHalfWidth, cy + backLength)
        lineTo(cx, cy + backLength * 1.25f)
        lineTo(cx + backHalfWidth, cy + backLength)
        lineTo(cx + halfWidth, cy - length * 0.28f)
        close()
    }
    drawPath(path, color)
}

@Preview(showBackground = true, backgroundColor = 0xFFAAAAAA)
@Composable
private fun AmigaClockFaceOs13Preview() {
    AmigaClockFace(
        hour = 10,
        minute = 10,
        second = 30,
        osStyle = OSStyle.AmigaOS13
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFAAAAAA)
@Composable
private fun AmigaClockFaceOs30Preview() {
    AmigaClockFace(
        hour = 10,
        minute = 10,
        second = 30,
        osStyle = OSStyle.AmigaOS20
    )
}
