package com.rff.boingballdemo.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.clock
import androidx.compose.foundation.layout.Box
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaTextBox
import com.rff.boingballdemo.component.AmigaToolbar
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.main.conditional
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.whiteColor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClockScreenRoot(
    viewModel: ClockViewModel = koinViewModel(),
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ClockScreen(state = state, onCloseClick = onCloseClick)
}

@Composable
fun ClockScreen(
    state: ClockState,
    onCloseClick: () -> Unit = {},
) {
    val bg = if (state.osStyle == OSStyle.AmigaOS20) backgroundColor else amigaOs13Blue

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center,
    ) {
        val isLandscape = maxWidth > maxHeight
        val windowWidth = if (isLandscape) minOf(maxHeight * 0.78f, maxWidth * 0.55f) else maxWidth * 0.82f

        Column(modifier = Modifier.fillMaxSize()) {
            AmigaScreenTitleBar(
                text = stringResource(Res.string.workbench),
                osStyle = state.osStyle
            )
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.width(windowWidth),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AmigaToolbar(
                        title = stringResource(Res.string.clock),
                        osStyle = state.osStyle,
                        onCloseClick = onCloseClick,
                    )
                    ClockContent(state = state)
                }
            }
        }
    }
}

@Composable
private fun ClockContent(
    state: ClockState,
    modifier: Modifier = Modifier,
) {
    val bg = if (state.osStyle == OSStyle.AmigaOS20)
        backgroundColor
    else
        amigaOs13Blue

    Column(
        modifier = modifier
            .conditional(
                condition = state.osStyle == OSStyle.AmigaOS13,
                ifTrue = {
                    background(color = Color.White)
                        .padding(horizontal = 2.dp)
                        .padding(bottom = 2.dp)
                        .background(color = amigaOs13Blue)
                },
                ifFalse = {
                    background(color = Color.White)
                        .padding(horizontal = 1.dp)
                        .background(color = amigaOs30Blue)
                        .padding(horizontal = 2.dp)
                        .background(color = blackColor)
                        .padding(horizontal = 1.dp)
                        .background(color = blackColor)
                        .padding(bottom = 1.dp)
                        .background(color = whiteColor)
                        .padding(bottom = 1.dp)
                },
            )
            .background(color = bg)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AmigaClockFace(
            hour = state.hour,
            minute = state.minute,
            second = state.second,
            osStyle = state.osStyle,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        AmigaTextBox(
            text = state.dateText,
            osStyle = state.osStyle,
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

private val previewState = ClockState(
    osStyle = OSStyle.AmigaOS13,
    hour = 10,
    minute = 10,
    second = 30,
    dateText = "04.06.2026",
)

@Preview(device = "id:pixel_10")
@Composable
private fun ClockScreenOs13Preview() {
    BoingBallDemoTheme {
        ClockScreen(state = previewState)
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun ClockScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        ClockScreen(state = previewState)
    }
}

@Preview(device = "id:pixel_10")
@Composable
private fun ClockScreenOs30Preview() {
    BoingBallDemoTheme {
        ClockScreen(state = previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun ClockScreenLandscapeOs30Preview() {
    BoingBallDemoTheme {
        ClockScreen(state = previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}
