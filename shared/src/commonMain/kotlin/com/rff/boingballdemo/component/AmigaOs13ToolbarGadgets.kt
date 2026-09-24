package com.rff.boingballdemo.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.ui.theme.amigaOs13Blue

private fun Float.correct(value: Float) = this + value / 2

@Composable
internal fun AmigaOs13CloseGadget(
    modifier: Modifier = Modifier,
) {
    // Aspect ratio is 12 x 10 because the base of the gadget is 10 x 10 but it contains
    // two additional blue lines at the left and right edges
    Box(
        modifier = modifier
            .aspectRatio(12f/10f)
            .drawBehind {
                val pixelSize = size.height / 10
                val x0 = 0f.correct(pixelSize)
                val x1 = size.width.correct(-pixelSize)
                drawLine(
                    color = amigaOs13Blue,
                    start = Offset(x0, 0f),
                    end = Offset(x0, size.height),
                    strokeWidth = pixelSize,
                )
                drawLine(
                    color = amigaOs13Blue,
                    start = Offset(x1, 0f),
                    end = Offset(x1, size.height),
                    strokeWidth = pixelSize,
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(x = (x0 + pixelSize).correct(-pixelSize), y = 0f),
                    size = Size(10 * pixelSize, 10 * pixelSize),
                )
                drawRect(
                    color = amigaOs13Blue,
                    topLeft = Offset(
                        x = (x0 + 2*pixelSize).correct(-pixelSize),
                        y = (0f + 2*pixelSize).correct(-2*pixelSize)
                    ),
                    size = Size(8 * pixelSize, 8 * pixelSize),
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(
                        x = (x0 + 3*pixelSize).correct(-pixelSize),
                        y = (0f + 3*pixelSize).correct(-2*pixelSize)
                    ),
                    size = Size(6 * pixelSize, 6 * pixelSize),
                )
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(
                        x = (x0 + 5*pixelSize).correct(-pixelSize),
                        y = (0f + 5*pixelSize).correct(-2*pixelSize)
                    ),
                    size = Size(2 * pixelSize, 2 * pixelSize),
                )
            }
    )
}

@Composable
internal fun AmigaOs13BringToFrontGadget(
    modifier: Modifier = Modifier,
) {
    // Aspect ratio is 12 x 10 because the base of the gadget is 11 x 10 but it contains
    // one additional blue line at the left edge
    Box(
        modifier = modifier
            .aspectRatio(12f/10f)
            .drawBehind {
                val pixelSize = size.height / 10
                val x0 = 0f.correct(pixelSize)
                drawLine(
                    color = amigaOs13Blue,
                    start = Offset(x0, 0f),
                    end = Offset(x0, size.height),
                    strokeWidth = pixelSize,
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(x = (x0 + pixelSize).correct(-pixelSize), y = 0f),
                    size = Size(11 * pixelSize, 10 * pixelSize),
                )
                drawRect(
                    color = amigaOs13Blue,
                    topLeft = Offset(
                        x = (x0 + 2*pixelSize).correct(-pixelSize),
                        y = (0f + 2*pixelSize).correct(-2*pixelSize)
                    ),
                    size = Size(7 * pixelSize, 6 * pixelSize),
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(
                        x = (x0 + 3*pixelSize).correct(-pixelSize),
                        y = (0f + 3*pixelSize).correct(-2*pixelSize)
                    ),
                    size = Size(5 * pixelSize, 4 * pixelSize),
                )
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(
                        x = (x0 + 4*pixelSize).correct(-pixelSize),
                        y = (0f + 4*pixelSize).correct(-2*pixelSize)
                    ),
                    size = Size(7 * pixelSize, 6 * pixelSize),
                )
            }
    )
}

@Composable
internal fun AmigaOs13SendToBackGadget(
    modifier: Modifier = Modifier,
) {
    // Aspect ratio is 13 x 10 because the base of the gadget is 11 x 10 but it contains
    // two additional blue lines at the left and right edges
    Box(
        modifier = modifier
            .aspectRatio(13f / 10f)
            .drawBehind {
                val pixelSize = size.height / 10
                val x0 = 0f.correct(pixelSize)
                val x1 = size.width.correct(-pixelSize)
                drawLine(
                    color = amigaOs13Blue,
                    start = Offset(x0, 0f),
                    end = Offset(x0, size.height),
                    strokeWidth = pixelSize,
                )
                drawLine(
                    color = amigaOs13Blue,
                    start = Offset(x1, 0f),
                    end = Offset(x1, size.height),
                    strokeWidth = pixelSize,
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(x = (x0 + pixelSize).correct(-pixelSize), y = 0f),
                    size = Size(11 * pixelSize, 10 * pixelSize),
                )
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(
                        x = (x0 + 2 * pixelSize).correct(-pixelSize),
                        y = (0f + 2 * pixelSize).correct(-2 * pixelSize)
                    ),
                    size = Size(7 * pixelSize, 6 * pixelSize),
                )
                drawRect(
                    color = amigaOs13Blue,
                    topLeft = Offset(
                        x = (x0 + 4 * pixelSize).correct(-pixelSize),
                        y = (0f + 3 * pixelSize).correct(-2 * pixelSize)
                    ),
                    size = Size(7 * pixelSize, 7 * pixelSize),
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(
                        x = (x0 + 5 * pixelSize).correct(-pixelSize),
                        y = (0f + 4 * pixelSize).correct(-2 * pixelSize)
                    ),
                    size = Size(5 * pixelSize, 5 * pixelSize),
                )
            }
    )
}

@Composable
fun AmigaOs13ToolbarPlaceholderGadget(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .drawBehind {
                val pixelSize = 2 * (size.height / 10)
                val barLength = size.width - 2 * pixelSize

                drawRect(color = Color.White)
                drawRect(
                    color = amigaOs13Blue,
                    topLeft = Offset(pixelSize, size.height - 2 * pixelSize),
                    size = Size(barLength, pixelSize)
                )
                drawRect(
                    color = amigaOs13Blue,
                    topLeft = Offset(pixelSize, size.height - 4 * pixelSize),
                    size = Size(barLength, pixelSize)
                )
            }
    )
}

@Preview
@Composable
private fun CloseGadgetPreview() {
    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center,
    ) {
        AmigaOs13CloseGadget(
            modifier = Modifier
                .width((3 * 12).dp)
        )
    }
}

@Preview
@Composable
private fun BringToFrontGadgetPreview() {
    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center,
    ) {
        AmigaOs13BringToFrontGadget(
            modifier = Modifier
                .width((3 * 12).dp)
        )
    }
}

@Preview
@Composable
private fun SendToBackGadgetPreview() {
    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center,
    ) {
        AmigaOs13SendToBackGadget(
            modifier = Modifier
                .width((3 * 13).dp)
        )
    }
}

@Preview
@Composable
private fun ToolbarPlaceholderGadgetPreview() {
    Box(
        modifier = Modifier.size(250.dp, 50.dp),
        contentAlignment = Alignment.Center,
    ) {
        AmigaOs13ToolbarPlaceholderGadget(
            modifier = Modifier
                .height((3 * 10).dp)
                .fillMaxSize()
        )
    }
}
