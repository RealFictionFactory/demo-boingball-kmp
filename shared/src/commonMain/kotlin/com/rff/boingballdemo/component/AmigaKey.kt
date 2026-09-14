package com.rff.boingballdemo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs13Orange
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.amigaOs30Grey
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.ui.theme.topazFont20
import com.rff.boingballdemo.ui.theme.whiteColor

/**
 * Square Workbench-style key used by the Calculator keypad.
 */
@Composable
fun AmigaKey(
    text: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val clickModifier = modifier.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick,
    )

    when (osStyle) {
        OSStyle.AmigaOS13 -> AmigaOs13Key(
            text = text,
            isPressed = isPressed,
            modifier = clickModifier,
        )
        OSStyle.AmigaOS20 -> AmigaOs30Key(
            text = text,
            isPressed = isPressed,
            modifier = clickModifier,
        )
    }
}

@Composable
private fun AmigaOs13Key(
    text: String,
    isPressed: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = if (isPressed) amigaOs13Orange else amigaOs13Blue)
            .border(width = 1.dp, color = whiteColor)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = topazFont(),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = if (isPressed) blackColor else whiteColor,
        )
    }
}

@Composable
private fun AmigaOs30Key(
    text: String,
    isPressed: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawAmigaOs30Frame(
                    strokePx = size.minDimension / 24f,
                    fillColor = if (isPressed) amigaOs30Blue else amigaOs30Grey,
                    topLeftColor = if (isPressed) blackColor else whiteColor,
                    bottomRightColor = if (isPressed) whiteColor else blackColor,
                )
            }
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = topazFont20(),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = blackColor,
        )
    }
}

@Preview
@Composable
private fun AmigaKeyPreview() {
    BoingBallDemoTheme {
        Row(modifier = Modifier.background(Color.Gray).padding(8.dp).height(48.dp)) {
            AmigaKey(text = "7", osStyle = OSStyle.AmigaOS13, modifier = Modifier.size(48.dp))
            AmigaKey(text = "7", osStyle = OSStyle.AmigaOS20, modifier = Modifier.size(48.dp))
        }
    }
}
