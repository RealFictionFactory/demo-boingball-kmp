package com.rff.boingballdemo.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.shell
import boingball.shared.generated.resources.shell13
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaToolbar
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.main.conditional
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs13Orange
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.ui.theme.topazFont20
import com.rff.boingballdemo.ui.theme.whiteColor
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

private const val CURSOR_BLINK_MS = 500L

@Composable
fun ShellScreenRoot(
    viewModel: ShellViewModel = koinViewModel(),
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ShellScreen(
        state = state,
        onTap = viewModel::onTap,
        onCloseClick = onCloseClick,
    )
}

@Composable
fun ShellScreen(
    state: ShellState,
    onTap: () -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    val bg = if (state.osStyle == OSStyle.AmigaOS20) backgroundColor else amigaOs13Blue
    val title = if (state.osStyle == OSStyle.AmigaOS13) {
        stringResource(Res.string.shell13)
    } else {
        stringResource(Res.string.shell)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center,
    ) {
        if (maxWidth > maxHeight) {
            LandscapeShellLayout(state, onTap, onCloseClick, maxWidth * 0.7f, maxHeight * 0.6f, title)
        } else {
            val windowWidth = maxWidth * 0.92f
            PortraitShellLayout(state, onTap, onCloseClick, windowWidth, windowWidth * 0.9f, title)
        }
    }
}

@Composable
private fun PortraitShellLayout(state: ShellState, onTap: () -> Unit, onCloseClick: () -> Unit, windowWidth: Dp, consoleHeight: Dp, title: String) =
    ShellLayout(state, onTap, onCloseClick, windowWidth, consoleHeight, title)

@Composable
private fun LandscapeShellLayout(state: ShellState, onTap: () -> Unit, onCloseClick: () -> Unit, windowWidth: Dp, consoleHeight: Dp, title: String) =
    ShellLayout(state, onTap, onCloseClick, windowWidth, consoleHeight, title)

@Composable
private fun ShellLayout(state: ShellState, onTap: () -> Unit, onCloseClick: () -> Unit, windowWidth: Dp, consoleHeight: Dp, title: String) {
        Column(modifier = Modifier.fillMaxSize()) {
            AmigaScreenTitleBar(
                text = stringResource(Res.string.workbench),
                osStyle = state.osStyle,
            )
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(modifier = Modifier.width(windowWidth)) {
                    AmigaToolbar(
                        title = title,
                        osStyle = state.osStyle,
                        onCloseClick = onCloseClick,
                    )
                    ShellConsole(
                        state = state,
                        consoleHeight = consoleHeight,
                        onTap = onTap,
                    )
                }
            }
        }
}

@Composable
private fun ShellConsole(
    state: ShellState,
    consoleHeight: Dp,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOs13 = state.osStyle == OSStyle.AmigaOS13
    val consoleBackground = if (isOs13) amigaOs13Blue else whiteColor
    val textColor = if (isOs13) whiteColor else blackColor
    val cursorColor = if (isOs13) amigaOs13Orange else blackColor

    val scrollState = rememberScrollState()
    var cursorVisible by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        while (true) {
            delay(CURSOR_BLINK_MS.milliseconds)
            cursorVisible = !cursorVisible
        }
    }

    LaunchedEffect(state.lines.size, state.currentLine) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .conditional(
                condition = isOs13,
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
                },
            )
            .background(color = if (state.osStyle == OSStyle.AmigaOS13) amigaOs13Blue else backgroundColor)
            .height(consoleHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTap,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 6.dp, vertical = 4.dp),
        ) {
            state.lines.forEach { line ->
                ShellText(text = line, isOs13 = isOs13, color = textColor)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShellText(text = state.currentLine, isOs13 = isOs13, color = textColor)
                Box(
                    modifier = Modifier
                        .size(width = 8.dp, height = 14.dp)
                        .background(color = if (cursorVisible) cursorColor else Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun ShellText(
    text: String,
    isOs13: Boolean,
    color: Color,
) {
    Text(
        text = text,
        fontFamily = if (isOs13) topazFont() else topazFont20(),
        fontSize = 11.sp,
        lineHeight = 14.sp,
        softWrap = false,
        color = color,
    )
}

private val previewState = ShellState(
    osStyle = OSStyle.AmigaOS13,
    lines = listOf("New CLI task 2", "", "1.SYS:> version", "Kickstart version 34.5, Workbench version 34.20", ""),
    currentLine = "1.SYS:> ",
)

@Preview(device = "id:pixel_10")
@Composable
private fun ShellScreenOs13Preview() {
    BoingBallDemoTheme {
        ShellScreen(state = previewState)
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun ShellScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        ShellScreen(state = previewState)
    }
}

@Preview(device = "id:pixel_10")
@Composable
private fun ShellScreenOs30Preview() {
    BoingBallDemoTheme {
        ShellScreen(
            state = previewState.copy(
                osStyle = OSStyle.AmigaOS20,
                lines = listOf("New Shell process 2", "", "1.Workbench3.0:> version", "Kickstart 40.68, Workbench 40.42", ""),
                currentLine = "1.Workbench3.0:> ",
            )
        )
    }
}
