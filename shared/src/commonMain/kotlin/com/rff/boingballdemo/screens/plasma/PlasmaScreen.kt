package com.rff.boingballdemo.screens.plasma

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.plasma_demo
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.LocalOsStyle
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaWindow
import com.rff.boingballdemo.screens.plasma.ui.PlasmaView
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlasmaScreenRoot(
    onCloseClick: () -> Unit = {},
) {
    PlasmaScreen(onCloseClick = onCloseClick)
}

@Composable
fun PlasmaScreen(
    onCloseClick: () -> Unit = {},
) {
    val bg = if (LocalOsStyle.current == OSStyle.AmigaOS20) backgroundColor else amigaOs13Blue

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
            LandscapePlasmaLayout(onCloseClick, minOf(maxWidth * 0.6f, widthLimitedByHeight))
        } else {
            PortraitPlasmaLayout(onCloseClick, minOf(maxWidth * 0.9f, widthLimitedByHeight))
        }
    }
}

@Composable
private fun PortraitPlasmaLayout(onCloseClick: () -> Unit, windowWidth: Dp) =
    PlasmaLayout(onCloseClick, windowWidth)

@Composable
private fun LandscapePlasmaLayout(onCloseClick: () -> Unit, windowWidth: Dp) =
    PlasmaLayout(onCloseClick, windowWidth)

@Composable
private fun PlasmaLayout(onCloseClick: () -> Unit, windowWidth: Dp) {
        Column(modifier = Modifier.fillMaxSize()) {
            AmigaScreenTitleBar(
                text = stringResource(Res.string.workbench),
            )
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                AmigaWindow(
                    modifier = Modifier.width(windowWidth),
                    title = stringResource(Res.string.plasma_demo),
                    onCloseClick = onCloseClick,
                ) { contentModifier ->
                    PlasmaContent(modifier = contentModifier)
                }
            }
        }
}

@Composable
private fun PlasmaContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor),
    ) {
        PlasmaView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f),
        )
    }
}

@Preview(device = "id:pixel_10")
@Composable
private fun PlasmaScreenOs13Preview() {
    BoingBallDemoTheme {
        PlasmaScreen()
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun PlasmaScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        PlasmaScreen()
    }
}

@Preview(device = "id:pixel_10")
@Composable
private fun PlasmaScreenOs30Preview() {
    BoingBallDemoTheme(osStyle = OSStyle.AmigaOS20) {
        PlasmaScreen()
    }
}
