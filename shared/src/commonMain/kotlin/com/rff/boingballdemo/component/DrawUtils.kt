package com.rff.boingballdemo.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.whiteColor

fun DrawScope.drawAmigaOs30Frame(
    strokePx: Float,
    fillColor: Color,
    topLeftColor: Color = whiteColor,
    bottomRightColor: Color = blackColor,
) {
    drawRect(color = topLeftColor)
    drawRect(
        color = bottomRightColor,
        topLeft = Offset(strokePx, size.height - strokePx),
        size = Size(size.width - 2 * strokePx, strokePx)
    )
    drawRect(
        color = bottomRightColor,
        topLeft = Offset(size.width - strokePx, 0f),
        size = Size(strokePx, size.height)
    )
    drawRect(
        color = fillColor,
        topLeft = Offset(strokePx, strokePx),
        size = Size(size.width - 2 * strokePx, size.height - 2 * strokePx)
    )
}

fun Modifier.amigaOs30Frame(
    fillColor: Color,
    stroke: Dp = 1.dp,
    topLeftColor: Color = whiteColor,
    bottomRightColor: Color = blackColor,
): Modifier = drawBehind {
    drawAmigaOs30Frame(stroke.toPx(), fillColor, topLeftColor, bottomRightColor)
}
