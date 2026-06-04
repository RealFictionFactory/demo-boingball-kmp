package com.rff.boingballdemo.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.whiteColor

@Composable
fun AmigaScreenTitleBar(
    text: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier,
    bgColor: Color = whiteColor,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(bgColor)

                if (osStyle == OSStyle.AmigaOS20) {
                    drawRect(
                        color = blackColor,
                        topLeft = Offset(0f, size.height - 1.dp.toPx()),
                        size = Size(size.width, 1.dp.toPx()),
                    )
                }
            }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AmigaMenuText(
            text = text,
            osStyle = osStyle,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0057AF)
@Composable
private fun AmigaScreenTitleBarOs13Preview() {
    AmigaScreenTitleBar(text = "Workbench", osStyle = OSStyle.AmigaOS13)
}

@Preview(showBackground = true, backgroundColor = 0xFFAAAAAA)
@Composable
private fun AmigaScreenTitleBarOs30Preview() {
    AmigaScreenTitleBar(text = "Workbench", osStyle = OSStyle.AmigaOS20)
}
