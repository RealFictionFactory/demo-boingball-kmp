package com.rff.boingballdemo.copper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.copper
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaToolbar
import com.rff.boingballdemo.component.AmigaWindow
import com.rff.boingballdemo.component.CopperBarsView
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
fun CopperBarsScreenRoot(
    viewModel: CopperBarsViewModel = koinViewModel(),
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CopperBarsScreen(state = state, onCloseClick = onCloseClick)
}

@Composable
fun CopperBarsScreen(
    state: CopperBarsState,
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
        // Window chrome: screen title bar, toolbar, borders and padding.
        val chromeHeight = 80.dp
        val contentHeight = (maxHeight - chromeHeight).coerceAtLeast(0.dp)
        // Content is 4:3, so never let it get wider than the free height allows.
        val widthLimitedByHeight = contentHeight * 4f / 3f
        if (maxWidth > maxHeight) {
            LandscapeCopperBarsLayout(state, onCloseClick, minOf(maxWidth * 0.6f, widthLimitedByHeight))
        } else {
            PortraitCopperBarsLayout(state, onCloseClick, minOf(maxWidth * 0.9f, widthLimitedByHeight))
        }
    }
}

@Composable
private fun PortraitCopperBarsLayout(state: CopperBarsState, onCloseClick: () -> Unit, windowWidth: Dp) =
    CopperBarsLayout(state, onCloseClick, windowWidth)

@Composable
private fun LandscapeCopperBarsLayout(state: CopperBarsState, onCloseClick: () -> Unit, windowWidth: Dp) =
    CopperBarsLayout(state, onCloseClick, windowWidth)

@Composable
private fun CopperBarsLayout(state: CopperBarsState, onCloseClick: () -> Unit, windowWidth: Dp) {
        Column(modifier = Modifier.fillMaxSize()) {
            AmigaScreenTitleBar(
                text = stringResource(Res.string.workbench),
                osStyle = state.osStyle,
            )
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                AmigaWindow(
                    modifier = Modifier.width(windowWidth),
                    title = stringResource(Res.string.copper),
                    osStyle = state.osStyle,
                    onCloseClick = onCloseClick,
                ) { contentModifier ->
                    CopperBarsContent(state = state, modifier = contentModifier)
                }
            }
        }
}

@Composable
private fun CopperBarsContent(
    state: CopperBarsState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor),
    ) {
        CopperBarsView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f),
        )
    }
}

private val previewState = CopperBarsState(osStyle = OSStyle.AmigaOS13)

@Preview(device = "id:pixel_10")
@Composable
private fun CopperBarsScreenOs13Preview() {
    BoingBallDemoTheme {
        CopperBarsScreen(state = previewState)
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun CopperBarsScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        CopperBarsScreen(state = previewState)
    }
}

@Preview(device = "id:pixel_10")
@Composable
private fun CopperBarsScreenOs30Preview() {
    BoingBallDemoTheme {
        CopperBarsScreen(state = previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}
