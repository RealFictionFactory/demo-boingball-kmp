package com.rff.boingballdemo.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.about
import boingball.shared.generated.resources.about_app_name
import boingball.shared.generated.resources.about_application_version
import boingball.shared.generated.resources.amiga_check_w
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaTextBox
import com.rff.boingballdemo.component.AmigaWindow
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AboutScreenRoot(
    viewModel: AboutViewModel = koinViewModel(),
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AboutScreen(state = state, onCloseClick = onCloseClick)
}

@Composable
fun AboutScreen(
    state: AboutState,
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
        if (maxWidth > maxHeight) {
            LandscapeAboutLayout(state, onCloseClick, minOf(maxWidth * 0.55f, 420.dp))
        } else {
            PortraitAboutLayout(state, onCloseClick, maxWidth * 0.86f)
        }
    }
}

@Composable
private fun PortraitAboutLayout(state: AboutState, onCloseClick: () -> Unit, windowWidth: Dp) =
    AboutLayout(state, onCloseClick, windowWidth)

@Composable
private fun LandscapeAboutLayout(state: AboutState, onCloseClick: () -> Unit, windowWidth: Dp) =
    AboutLayout(state, onCloseClick, windowWidth)

@Composable
private fun AboutLayout(state: AboutState, onCloseClick: () -> Unit, windowWidth: Dp) {
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
                    title = stringResource(Res.string.about),
                    osStyle = state.osStyle,
                    onCloseClick = onCloseClick,
                ) {
                    contentModifier ->
                    AboutContent(state = state, modifier = contentModifier)
                }
            }
        }
}

@Composable
private fun AboutContent(
    state: AboutState,
    modifier: Modifier = Modifier,
) {
    val bg = if (state.osStyle == OSStyle.AmigaOS20)
        backgroundColor
    else
        amigaOs13Blue

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Image(
            painter = painterResource(Res.drawable.amiga_check_w),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.72f)
                .sizeIn(maxWidth = 300.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        AmigaTextBox(
            text = stringResource(Res.string.about_app_name, state.appName),
            osStyle = state.osStyle,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AmigaTextBox(
            text = stringResource(Res.string.about_application_version, state.appVersion),
            osStyle = state.osStyle,
        )
    }
}

private val previewState = AboutState(
    appVersion = "1.0.0",
    osStyle = OSStyle.AmigaOS20,
)

@Preview
@Composable
private fun AboutScreenPortraitOs13Preview() {
    BoingBallDemoTheme {
        AboutScreen(state = previewState.copy(osStyle = OSStyle.AmigaOS13))
    }
}

@Preview(device = "spec:parent=Nexus 5,orientation=landscape")
@Composable
private fun AboutScreenLandscapeOs30Preview() {
    BoingBallDemoTheme {
        AboutScreen(state = previewState)
    }
}
