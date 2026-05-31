package com.rff.boingballdemo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.backgroundColor

@Composable
fun BoingBallView(
    modifier: Modifier = Modifier,
    themeColor: Color,
    altColor: Color,
    drawBorders: Boolean,
) {
    Box(
        modifier = modifier
            .aspectRatio(4f / 3f)
            .background(color = backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        BoingBallBackground(
            modifier = Modifier.fillMaxSize(),
        )

        BoingBall(
            modifier = Modifier.fillMaxSize(),
            themeColor = themeColor,
            altColor = altColor,
            drawBorders = drawBorders,
        )
    }
}

@Preview
@Composable
private fun BoingBallViewPreview() {
    BoingBallDemoTheme {
        BoingBallView(
            themeColor = Color.Red,
            altColor = Color.White,
            drawBorders = true,
        )
    }
}
