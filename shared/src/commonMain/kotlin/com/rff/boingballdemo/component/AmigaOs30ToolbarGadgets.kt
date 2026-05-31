package com.rff.boingballdemo.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.amigaOs30Grey
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.toolbarTextStyle
import com.rff.boingballdemo.ui.theme.whiteColor

@Composable
internal fun AmigaOs30CloseGadget(
    modifier: Modifier = Modifier,
) {
    val fixedWidth = 14f
    val fixedHeight = 15f
    Box(
        modifier = modifier
            .aspectRatio(fixedWidth/fixedHeight)
            .drawBehind {
                val pixelWSize = size.width / fixedWidth
                val pixelHSize = size.height / fixedHeight
                drawRect(color = whiteColor)
                drawRect(
                    color = blackColor,
                    topLeft = Offset(pixelWSize, size.height - pixelHSize),
                    size = Size(size.width - 2*pixelWSize, pixelHSize)
                )
                drawRect(
                    color = blackColor,
                    topLeft = Offset(size.width - pixelWSize, 0f),
                    size = Size(pixelWSize, size.height)
                )
                drawRect(
                    color = amigaOs30Blue,
                    topLeft = Offset(pixelWSize, pixelHSize),
                    size = Size(size.width - 2*pixelWSize, size.height - 2*pixelHSize),
                )
                drawRect(
                    color = blackColor,
                    topLeft = Offset(5*pixelWSize, 5*pixelHSize),
                    size = Size(4*pixelWSize, size.height - 10*pixelHSize),
                )
                drawRect(
                    color = whiteColor,
                    topLeft = Offset(6*pixelWSize, 6*pixelHSize),
                    size = Size(2*pixelWSize, size.height - 12*pixelHSize),
                )
            }
    )
}

@Composable
internal fun AmigaOs30BringToFrontGadget(
    modifier: Modifier = Modifier,
) {
    val fixedWidth = 18f
    val fixedHeight = 15f
    Box(
        modifier = modifier
            .aspectRatio(fixedWidth/fixedHeight)
            .drawBehind {
                val pixelWSize = size.width / fixedWidth
                val pixelHSize = size.height / fixedHeight
                drawRect(color = whiteColor)
                drawRect(
                    color = blackColor,
                    topLeft = Offset(pixelWSize, size.height - pixelHSize),
                    size = Size(size.width - 2*pixelWSize, pixelHSize)
                )
                drawRect(
                    color = blackColor,
                    topLeft = Offset(size.width - pixelWSize, 0f),
                    size = Size(pixelWSize, size.height)
                )
                drawRect(
                    color = amigaOs30Blue,
                    topLeft = Offset(pixelWSize, pixelHSize),
                    size = Size(size.width - 2*pixelWSize, size.height - 2*pixelHSize),
                )
                drawRect(
                    color = blackColor,
                    topLeft = Offset(3*pixelWSize, 3*pixelHSize),
                    size = Size(size.width - 6*pixelWSize, size.height - 6*pixelHSize),
                )
                drawRect(
                    color = amigaOs30Blue,
                    topLeft = Offset(4*pixelWSize, 4*pixelHSize),
                    size = Size(size.width - 8*pixelWSize, size.height - 8*pixelHSize),
                )
                drawRect(
                    color = blackColor,
                    topLeft = Offset(3*pixelWSize, 3*pixelHSize),
                    size = Size(size.width - 11*pixelWSize, size.height - 9*pixelHSize),
                )
                drawRect(
                    color = whiteColor,
                    topLeft = Offset(4*pixelWSize, 4*pixelHSize),
                    size = Size(size.width - 13*pixelWSize, size.height - 11*pixelHSize),
                )
            }
    )
}

@Composable
internal fun AmigaOs30SendToBackGadget(
    modifier: Modifier = Modifier,
) {
    val fixedWidth = 18f
    val fixedHeight = 15f
    Box(
        modifier = modifier
            .aspectRatio(fixedWidth/fixedHeight)
            .drawBehind {
                val pixelWSize = size.width / fixedWidth
                val pixelHSize = size.height / fixedHeight
                drawRect(color = whiteColor)
                drawRect(
                    color = blackColor,
                    topLeft = Offset(pixelWSize, size.height - pixelHSize),
                    size = Size(size.width - 2*pixelWSize, pixelHSize)
                )
                drawRect(
                    color = blackColor,
                    topLeft = Offset(size.width - pixelWSize, 0f),
                    size = Size(pixelWSize, size.height)
                )
                drawRect(
                    color = amigaOs30Blue,
                    topLeft = Offset(pixelWSize, pixelHSize),
                    size = Size(size.width - 2*pixelWSize, size.height - 2*pixelHSize),
                )
                drawRect(
                    color = blackColor,
                    topLeft = Offset(3*pixelWSize, 3*pixelHSize),
                    size = Size(size.width - 9*pixelWSize, size.height - 8*pixelHSize)
                )
                drawRect(
                    color = amigaOs30Grey,
                    topLeft = Offset(4*pixelWSize, 4*pixelHSize),
                    size = Size(size.width - 11*pixelWSize, size.height - 10*pixelHSize)
                )
                drawRect(
                    color = blackColor,
                    topLeft = Offset(6*pixelWSize, 5*pixelHSize),
                    size = Size(size.width - 9*pixelWSize, size.height - 8*pixelHSize)
                )
                drawRect(
                    color = whiteColor,
                    topLeft = Offset(7*pixelWSize, 6*pixelHSize),
                    size = Size(size.width - 11*pixelWSize, size.height - 10*pixelHSize)
                )
            }
    )
}

@Composable
fun AmigaOs30ToolbarPlaceholderGadget(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .drawBehind {
                val pixelSize = size.height / 15
                drawAmigaOs30Frame(
                    strokePx = pixelSize,
                    fillColor = amigaOs30Blue
                )
            }
    ) {
        Text(
            text = text,
            style = toolbarTextStyle(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Preview
@Composable
private fun CloseGadgetPreview() {
    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center,
    ) {
        AmigaOs30CloseGadget(
            modifier = Modifier
                .height((3 * 12).dp)
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
        AmigaOs30BringToFrontGadget(
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
        AmigaOs30SendToBackGadget(
            modifier = Modifier
                .width((3 * 13).dp)
        )
    }
}

@Preview
@Composable
private fun ToolbarPlaceholderGadgetPreview() {
    Box(
        modifier = Modifier.size(200.dp, 50.dp),
        contentAlignment = Alignment.Center,
    ) {
        AmigaOs30ToolbarPlaceholderGadget(
            text = "Title",
            modifier = Modifier
                .height((3 * 12).dp)
                .fillMaxSize()
        )
    }
}
