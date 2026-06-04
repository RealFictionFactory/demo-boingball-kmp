package com.rff.boingballdemo.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rff.boingballdemo.component.BoingBallView
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.redColor

@Composable
fun TVBoingBallScreen() {
    val state = BoingBallState(
        themeColor = redColor,
        drawBorders = false,
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        BoingBallView(
            themeColor = state.themeColor,
            altColor = state.altColor,
            drawBorders = state.drawBorders,
        )
    }
}

@Composable
@Preview
fun TVBoingBallScreenPreview() {
    BoingBallDemoTheme {
        TVBoingBallScreen()
    }
}