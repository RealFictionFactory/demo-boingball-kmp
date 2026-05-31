package com.rff.boingballdemo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Orange
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.amigaOs30Grey
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.toolbarTextStyle
import com.rff.boingballdemo.ui.theme.whiteColor

@Composable
fun AmigaButton(
    text: String = "",
    osStyle: OSStyle,
    onClick: () -> Unit = {}
) {
    when (osStyle) {
        OSStyle.AmigaOS13 -> AmigaOs13Button(text = text, onClick = onClick)
        OSStyle.AmigaOS20 -> AmigaOs30Button(text = text, onClick = onClick)
    }
}

@Composable
private fun AmigaOs13Button(
    text: String = "",
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor = if (isPressed) amigaOs13Orange else Color.Transparent
    val fontColor = if (isPressed) Color.Black else Color.White

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .background(color = backgroundColor)
            .border(width = 1.dp, color = Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = fontColor,
        )
    }
}

@Composable
private fun AmigaOs30Button(
    text: String = "",
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor = if (isPressed) amigaOs30Blue else amigaOs30Grey
    val topLeftFrameColor = if (isPressed) blackColor else whiteColor
    val bottomRightFrameColor = if (isPressed) whiteColor else blackColor

    Box(
        modifier = Modifier
            .drawBehind {
                val pixelSize = size.height / 24
                drawAmigaOs30Frame(
                    strokePx = pixelSize,
                    fillColor = backgroundColor,
                    topLeftColor = topLeftFrameColor,
                    bottomRightColor = bottomRightFrameColor
                )
            }
            .defaultMinSize(minWidth = 24.dp, minHeight = 24.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = toolbarTextStyle(),
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
    }
}

@Preview
@Composable
private fun ButtonPreview() {
    BoingBallDemoTheme {
        AmigaOs13Button(text = "Button text")
    }
}

@Preview
@Composable
private fun Button30Preview() {
    BoingBallDemoTheme {
        AmigaOs30Button(text = "Button text")
    }
}
