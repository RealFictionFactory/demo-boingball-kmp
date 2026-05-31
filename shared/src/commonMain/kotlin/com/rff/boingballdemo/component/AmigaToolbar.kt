package com.rff.boingballdemo.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.topazFont

@Composable
fun AmigaToolbar(
    title: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier,
    toolbarHeight: Dp = 28.dp,
) {
    when (osStyle) {
        OSStyle.AmigaOS13 -> AmigaOs13Toolbar(
            title = title,
            modifier = modifier,
            toolbarHeight = toolbarHeight,
        )
        OSStyle.AmigaOS20 -> AmigaOs30Toolbar(
            title = title,
            modifier = modifier,
            toolbarHeight = toolbarHeight,
        )
    }
}

@Composable
private fun AmigaOs13Toolbar(
    title: String,
    modifier: Modifier = Modifier,
    toolbarHeight: Dp = 28.dp,
) {
    Box(
        modifier
            .height(toolbarHeight)
            .fillMaxWidth()
            .drawBehind {
                // background
                drawRect(Color.White)
            }
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(4.dp))
            // Close gadget
            AmigaOs13CloseGadget()
            Spacer(Modifier.width(4.dp))
            // Title
            Text(
                text = title,
                fontSize = 16.sp,
                color = amigaOs13Blue,
                fontFamily = topazFont(),
            )
            // Depth gadget
            AmigaOs13ToolbarPlaceholderGadget(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            // Bring to front gadget
            AmigaOs13BringToFrontGadget()
            // Send to back gadget
            AmigaOs13SendToBackGadget()
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
private fun AmigaOs30Toolbar(
    title: String,
    modifier: Modifier = Modifier,
    toolbarHeight: Dp = 28.dp,
) {
    Box(
        modifier
            .height(toolbarHeight)
            .fillMaxWidth()
            .drawBehind {
                // background
                drawRect(amigaOs30Blue)
            }
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close gadget
            AmigaOs30CloseGadget()
            AmigaOs30ToolbarPlaceholderGadget(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            // Bring to front gadget
            AmigaOs30BringToFrontGadget()
            // Send to back gadget
            AmigaOs30SendToBackGadget()
        }
    }
}

@Preview
@Composable
private fun AmigaOs13ToolbarPreview() {
    AmigaOs13Toolbar(title = "Amiga Toolbar")
}

@Preview
@Composable
private fun AmigaOs30ToolbarPreview() {
    AmigaOs30Toolbar(title = "Amiga Toolbar")
}
