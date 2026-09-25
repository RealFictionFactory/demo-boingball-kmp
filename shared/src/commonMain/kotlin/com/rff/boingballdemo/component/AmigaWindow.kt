package com.rff.boingballdemo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.whiteColor

@Composable
fun AmigaWindow(
    title: String,
    osStyle: OSStyle,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (contentModifier: Modifier) -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AmigaToolbar(title = title, osStyle = osStyle)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .amigaWindowFrame(osStyle),
            ) {
                content(Modifier.fillMaxWidth())
            }
        }
        AmigaCloseTouchOverlay(onCloseClick = onCloseClick)
    }
}

private fun Modifier.amigaWindowFrame(osStyle: OSStyle): Modifier = when (osStyle) {
    OSStyle.AmigaOS13 -> background(Color.White)
        .padding(horizontal = 2.dp)
        .padding(bottom = 2.dp)
        .background(amigaOs13Blue)
    OSStyle.AmigaOS20 -> background(Color.White)
        .padding(horizontal = 1.dp)
        .background(amigaOs30Blue)
        .padding(horizontal = 2.dp)
        .background(blackColor)
        .padding(horizontal = 1.dp)
        .background(blackColor)
        .padding(bottom = 1.dp)
        .background(whiteColor)
        .padding(bottom = 1.dp)
        .background(backgroundColor)
}
