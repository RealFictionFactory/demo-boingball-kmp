package com.rff.boingballdemo.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.about
import boingball.shared.generated.resources.about30
import boingball.shared.generated.resources.app_name
import boingball.shared.generated.resources.calculator
import boingball.shared.generated.resources.clock
import boingball.shared.generated.resources.clock30
import boingball.shared.generated.resources.copper
import boingball.shared.generated.resources.preferences
import boingball.shared.generated.resources.prefs30
import boingball.shared.generated.resources.shell
import boingball.shared.generated.resources.shell13
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaTextBox
import com.rff.boingballdemo.component.GuruMeditationOverlay
import com.rff.boingballdemo.component.AmigaToolbar
import com.rff.boingballdemo.component.BoingBallView
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs13Orange
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.whiteColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Future development plan — making this a real Workbench experience:
 *
 * PHASE 1 — current screen improvements (near term):
 * - [*DONE*] Amiga screen title bar at top (thin strip: "Workbench Screen", right-aligned, OS-style aware)
 * - [*DONE*] Guru Meditation easter egg (long-press triggers iconic red/black error screen)
 * - [*DONE*] About window (3rd desktop icon, shows app/device info in Amiga Topaz style)
 * - [*DONE*] Clock window
 * - [*DONE*] Copper bars demo
 * - [*DONE*] simple Calculator app
 * - [*DONE*] AmigaDOS Shell - simple, no commands, just opens the window with a prompt. clicking anywhere closes it
 *   (built as ghost-typing demo: tap runs next canned command, close via toolbar gadget)
 *
 * PHASE 2 — additional features:
 * - History of Amiga logo by year
 * - Allow user to change rotation speed
 * - Add full screen Boing Ball view like real demo, some back button may be necessary
 *
 * PHASE 3 — full Workbench rework:
 * - Amiga top menu bar on tap (menus: Workbench / Tools / Help, OS-style dropdowns)
 * - Rework main screen to look like real Workbench desktop
 * - App starts at desktop; user taps disk icon to open a drawer window (like a folder)
 * - Drawer window contains app icons: Boing Ball, Clock, Preferences, About, etc.
 * - Each icon launches its window as an overlay on the desktop (multiple windows open at once)
 * - Windows are movable/stackable (bring to front / send to back gadgets become functional)
 *
 * PHASE 4 — polish:
 * - Screen flip/push animation when switching OS style
 * - Amiga-style requester/dialog component (reusable, e.g. "Exit app?" confirmation)
 * - Workbench top menu bar items wired to real actions (About Workbench, etc.)
 */
@Composable
fun BoingBallScreenRoot(
    viewModel: BoingBallViewModel = koinViewModel(),
    onPreferencesClick: () -> Unit,
    onClockClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onCopperBarsClick: () -> Unit = {},
    onCalculatorClick: () -> Unit = {},
    onShellClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BoingBallScreen(
        state = state,
        onAction = { action ->
            when (action) {
                BoingBallAction.Preferences -> onPreferencesClick()
                BoingBallAction.Clock -> onClockClick()
                BoingBallAction.Back -> onCloseClick()
                BoingBallAction.About -> onAboutClick()
                BoingBallAction.CopperBars -> onCopperBarsClick()
                BoingBallAction.Calculator -> onCalculatorClick()
                BoingBallAction.Shell -> onShellClick()
            }
        }
    )
}

@Composable
fun BoingBallScreen(
    state: BoingBallState,
    onAction: (BoingBallAction) -> Unit = {},
) {
    var showGuruMeditation by remember { mutableStateOf(false) }

    val bg = if (state.osStyle == OSStyle.AmigaOS20)
        backgroundColor
    else
        amigaOs13Blue

    Box(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(color = bg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = { showGuruMeditation = true })
                },
        ) {
            val availableWidth = maxWidth
            val isLandscape = maxWidth > maxHeight

            Column(modifier = Modifier.fillMaxSize()) {
                AmigaScreenTitleBar(
                    text = stringResource(Res.string.workbench),
                    osStyle = state.osStyle
                )

                if (isLandscape) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        BoingBallWindow(
                            state = state,
                            onCloseClick = { onAction(BoingBallAction.Back) },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .widthIn(max = availableWidth * 0.72f)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    CopperBarsShortcut(
                                        state = state,
                                        onClick = { onAction(BoingBallAction.CopperBars) },
                                    )
                                    CalculatorShortcut(
                                        state = state,
                                        onClick = { onAction(BoingBallAction.Calculator) },
                                    )
                                    ShellShortcut(
                                        state = state,
                                        onClick = { onAction(BoingBallAction.Shell) },
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    PreferencesShortcut(
                                        state = state,
                                        onClick = { onAction(BoingBallAction.Preferences) },
                                    )
                                    ClockShortcut(
                                        state = state,
                                        onClick = { onAction(BoingBallAction.Clock) },
                                    )
                                    AboutShortcut(
                                        state = state,
                                        onClick = { onAction(BoingBallAction.About) },
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.End,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                AboutShortcut(
                                    state = state,
                                    onClick = { onAction(BoingBallAction.About) },
                                )
                                ClockShortcut(
                                    state = state,
                                    onClick = { onAction(BoingBallAction.Clock) },
                                )
                                PreferencesShortcut(
                                    state = state,
                                    onClick = { onAction(BoingBallAction.Preferences) },
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                ShellShortcut(
                                    state = state,
                                    onClick = { onAction(BoingBallAction.Shell) },
                                )
                                CalculatorShortcut(
                                    state = state,
                                    onClick = { onAction(BoingBallAction.Calculator) },
                                )
                                CopperBarsShortcut(
                                    state = state,
                                    onClick = { onAction(BoingBallAction.CopperBars) },
                                )
                            }
                        }
                        BoingBallWindow(
                            state = state,
                            onCloseClick = { onAction(BoingBallAction.Back) },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .widthIn(max = availableWidth)
                        )
                    }
                } // end if/else landscape
            } // end Column
        }

        if (showGuruMeditation) {
            GuruMeditationOverlay(
                osStyle = state.osStyle,
                onDismiss = { showGuruMeditation = false })
        }
    }
}

@Composable
private fun BoingBallWindow(
    state: BoingBallState,
    onCloseClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AmigaToolbar(
            title = stringResource(Res.string.app_name),
            osStyle = state.osStyle,
            onCloseClick = onCloseClick
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
                .width(80.dp)
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

@Composable
private fun ClockShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resId = if (state.osStyle == OSStyle.AmigaOS13)
        Res.drawable.clock
    else
        Res.drawable.clock30

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            painter = painterResource(resId),
            contentDescription = stringResource(Res.string.clock)
        )
        AmigaTextBox(
            text = stringResource(Res.string.clock),
            osStyle = state.osStyle
        )
    }
}

@Composable
private fun AboutShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resId = if (state.osStyle == OSStyle.AmigaOS13)
        Res.drawable.about
    else
        Res.drawable.about30

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            painter = painterResource(resId),
            contentDescription = stringResource(Res.string.about)
        )
        AmigaTextBox(
            text = stringResource(Res.string.about),
            osStyle = state.osStyle
        )
    }
}

@Composable
private fun CopperBarsShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CopperBarsIcon(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )
        AmigaTextBox(
            text = stringResource(Res.string.copper),
            osStyle = state.osStyle
        )
    }
}

@Composable
private fun CopperBarsIcon(modifier: Modifier = Modifier) {
    val barColors = listOf(
        Color(0xFFFF2200),
        Color(0xFFFF8800),
        Color(0xFF00CC33),
        Color(0xFF00AAFF),
    )

    Canvas(modifier = modifier) {
        val border = size.minDimension * 0.08f
        drawRect(color = Color.White)
        drawRect(
            color = Color.Black,
            topLeft = Offset(border, border),
            size = Size(size.width - 2 * border, size.height - 2 * border),
        )

        val inner = size.height - 2 * border
        val barHeight = inner / (barColors.size * 2f)
        barColors.forEachIndexed { index, color ->
            val top = border + barHeight * (index * 2 + 0.5f)
            drawRect(
                color = color,
                topLeft = Offset(border * 2, top),
                size = Size(size.width - 4 * border, barHeight),
            )
        }
    }
}

@Composable
private fun CalculatorShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CalculatorIcon(
            osStyle = state.osStyle,
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )
        AmigaTextBox(
            text = stringResource(Res.string.calculator),
            osStyle = state.osStyle
        )
    }
}

@Composable
private fun CalculatorIcon(
    osStyle: OSStyle,
    modifier: Modifier = Modifier,
) {
    val body = if (osStyle == OSStyle.AmigaOS13) amigaOs13Blue else backgroundColor
    val keyColor = if (osStyle == OSStyle.AmigaOS13) Color.White else blackColor

    Canvas(modifier = modifier) {
        val border = size.minDimension * 0.08f
        drawRect(color = Color.White)
        drawRect(
            color = body,
            topLeft = Offset(border, border),
            size = Size(size.width - 2 * border, size.height - 2 * border),
        )

        // display
        val inset = border * 3
        drawRect(
            color = blackColor,
            topLeft = Offset(inset, inset),
            size = Size(size.width - 2 * inset, size.height * 0.18f),
        )

        // 3 x 3 keys
        val keyTop = inset + size.height * 0.26f
        val keyArea = size.height - keyTop - inset
        val step = keyArea / 3f
        val keySize = step * 0.62f
        val columnStep = (size.width - 2 * inset) / 3f
        repeat(3) { row ->
            repeat(3) { column ->
                drawRect(
                    color = keyColor,
                    topLeft = Offset(inset + column * columnStep, keyTop + row * step),
                    size = Size(minOf(keySize, columnStep * 0.62f), keySize),
                )
            }
        }
    }
}

@Composable
private fun ShellShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = if (state.osStyle == OSStyle.AmigaOS13) {
        stringResource(Res.string.shell13)
    } else {
        stringResource(Res.string.shell)
    }

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShellIcon(
            osStyle = state.osStyle,
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )
        AmigaTextBox(
            text = label,
            osStyle = state.osStyle
        )
    }
}

@Composable
private fun ShellIcon(
    osStyle: OSStyle,
    modifier: Modifier = Modifier,
) {
    val isOs13 = osStyle == OSStyle.AmigaOS13
    val console = if (isOs13) amigaOs13Blue else whiteColor
    val ink = if (isOs13) whiteColor else blackColor

    Canvas(modifier = modifier) {
        val border = size.minDimension * 0.08f
        drawRect(color = if (isOs13) whiteColor else blackColor)
        drawRect(
            color = console,
            topLeft = Offset(border, border),
            size = Size(size.width - 2 * border, size.height - 2 * border),
        )

        // title strip
        drawRect(
            color = ink,
            topLeft = Offset(border, border),
            size = Size(size.width - 2 * border, border * 1.5f),
        )

        // prompt marks and cursor block
        val lineHeight = border * 1.2f
        val lineTop = border * 4f
        val left = border * 2.5f
        repeat(3) { row ->
            val top = lineTop + row * border * 3f
            drawRect(
                color = ink,
                topLeft = Offset(left, top),
                size = Size(border * 2f, lineHeight),
            )
            drawRect(
                color = ink,
                topLeft = Offset(left + border * 3f, top),
                size = Size(size.width * (0.3f + 0.15f * row), lineHeight),
            )
        }
        drawRect(
            color = if (isOs13) amigaOs13Orange else blackColor,
            topLeft = Offset(left, lineTop + 3 * border * 3f),
            size = Size(border * 2f, lineHeight * 1.4f),
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
