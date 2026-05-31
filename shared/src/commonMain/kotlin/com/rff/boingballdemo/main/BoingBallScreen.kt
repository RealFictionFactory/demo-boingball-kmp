package com.rff.boingballdemo.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.app_name
import boingball.shared.generated.resources.preferences
import boingball.shared.generated.resources.prefs30
import com.rff.boingballdemo.component.AmigaToolbar
import com.rff.boingballdemo.component.BoingBallView
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.ui.theme.whiteColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.rff.boingballdemo.component.AmigaTextBox
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import kotlin.collections.copy

@Composable
fun BoingBallScreenRoot(
    viewModel: BoingBallViewModel = koinViewModel(),
    onPreferencesClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BoingBallScreen(
        state = state,
        onAction = { action ->
            when (action) {
                BoingBallAction.Preferences -> {
                    onPreferencesClick()
                }
            }
        }
    )
}

@Composable
fun BoingBallScreen(
    state: BoingBallState,
    onAction: (BoingBallAction) -> Unit = {},
) {
    val bg = if (state.osStyle == OSStyle.AmigaOS20)
        backgroundColor
    else
        amigaOs13Blue

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        val availableWidth = maxWidth
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                BoingBallWindow(
                    state = state,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .widthIn(max = availableWidth * 0.72f)
                )
                PreferencesShortcut(
                    state = state,
                    onClick = { onAction(BoingBallAction.Preferences) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                PreferencesShortcut(
                    state = state,
                    onClick = { onAction(BoingBallAction.Preferences) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
                BoingBallWindow(
                    state = state,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .widthIn(max = availableWidth)
                )
            }
        }
    }
}

@Composable
private fun BoingBallWindow(
    state: BoingBallState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AmigaToolbar(
            title = stringResource(Res.string.app_name),
            osStyle = state.osStyle
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .conditional(
                    condition = state.osStyle == OSStyle.AmigaOS13,
                    ifTrue = {
                        background(color = Color.White)
                            .padding(horizontal = 2.dp)
                            .padding(bottom = 2.dp)
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
                    }
                )
                .background(color = backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            BoingBallView(
                modifier = Modifier.padding(16.dp),
                themeColor = state.themeColor,
                altColor = state.altColor,
                drawBorders = state.drawBorders,
            )
        }
    }
}

@Composable
private fun PreferencesShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resId = if (state.osStyle == OSStyle.AmigaOS13)
        Res.drawable.preferences
    else
        Res.drawable.prefs30

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .width(120.dp)
                .height(40.dp),
            painter = painterResource(resId),
            contentDescription = stringResource(Res.string.preferences)
        )
        AmigaTextBox(
            text = stringResource(Res.string.preferences),
            osStyle = state.osStyle
        )
    }
}

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}

private val previewState = BoingBallState(
    themeColor = Color.Red,
    altColor = Color.White,
    drawBorders = false,
    osStyle = OSStyle.AmigaOS13
)

@Preview(device = "id:pixel_10")
@Composable
private fun BoingBallScreenOs13Preview() {
    BoingBallDemoTheme {
        BoingBallScreen(previewState)
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun BoingBallScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        BoingBallScreen(previewState)
    }
}

@Preview(device = "id:Nexus 4")
@Composable
private fun BoingBallScreenPreview() {
    BoingBallDemoTheme {
        BoingBallScreen(previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}

@Preview(device = "spec:parent=Nexus 4,orientation=landscape")
@Composable
private fun BoingBallScreenLandscapePreview() {
    BoingBallDemoTheme {
        BoingBallScreen(previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}
