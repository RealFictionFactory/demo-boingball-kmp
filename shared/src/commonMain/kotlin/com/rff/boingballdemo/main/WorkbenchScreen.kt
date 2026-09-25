package com.rff.boingballdemo.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.about
import boingball.shared.generated.resources.about30
import boingball.shared.generated.resources.boing
import boingball.shared.generated.resources.calculator
import boingball.shared.generated.resources.calculator30
import boingball.shared.generated.resources.clock
import boingball.shared.generated.resources.clock30
import boingball.shared.generated.resources.copper
import boingball.shared.generated.resources.copper30
import boingball.shared.generated.resources.mplayer
import boingball.shared.generated.resources.mplayer30
import boingball.shared.generated.resources.music_player
import boingball.shared.generated.resources.preferences
import boingball.shared.generated.resources.prefs30
import boingball.shared.generated.resources.shell
import boingball.shared.generated.resources.shell13
import boingball.shared.generated.resources.shell30
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaTextBox
import com.rff.boingballdemo.component.GuruMeditationOverlay
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
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
 * - [*DONE*] music player with a list of most iconic Amiga musics
 * - [*DROPPED*] History of Amiga logo by year
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
fun WorkbenchScreenRoot(
    viewModel: BoingBallViewModel = koinViewModel(),
    onBoingBallClick: () -> Unit = {},
    onPreferencesClick: () -> Unit,
    onClockClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onCopperBarsClick: () -> Unit = {},
    onCalculatorClick: () -> Unit = {},
    onShellClick: () -> Unit = {},
    onMusicPlayerClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    WorkbenchScreen(
        state = state,
        onAction = { action ->
            when (action) {
                BoingBallAction.BoingBall -> onBoingBallClick()
                BoingBallAction.Preferences -> onPreferencesClick()
                BoingBallAction.Clock -> onClockClick()
                BoingBallAction.Back -> onCloseClick()
                BoingBallAction.About -> onAboutClick()
                BoingBallAction.CopperBars -> onCopperBarsClick()
                BoingBallAction.Calculator -> onCalculatorClick()
                BoingBallAction.Shell -> onShellClick()
                BoingBallAction.MusicPlayer -> onMusicPlayerClick()
            }
        }
    )
}

@Composable
fun WorkbenchScreen(
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
            val isLandscape = maxWidth > maxHeight
            val landscapeGridWidth = minOf(maxWidth - 32.dp, 400.dp)
            Column(modifier = Modifier.fillMaxSize()) {
                AmigaScreenTitleBar(
                    text = stringResource(Res.string.workbench),
                    osStyle = state.osStyle
                )

                val shortcuts = listOf<@Composable () -> Unit>(
                    { BoingBallShortcut(state, onClick = { onAction(BoingBallAction.BoingBall) }) },
                    { AboutShortcut(state, onClick = { onAction(BoingBallAction.About) }) },
                    { PreferencesShortcut(state, onClick = { onAction(BoingBallAction.Preferences) }) },
                    { ClockShortcut(state, onClick = { onAction(BoingBallAction.Clock) }) },
                    { ShellShortcut(state, onClick = { onAction(BoingBallAction.Shell) }) },
                    { CalculatorShortcut(state, onClick = { onAction(BoingBallAction.Calculator) }) },
                    { CopperBarsShortcut(state, onClick = { onAction(BoingBallAction.CopperBars) }) },
                    { MusicPlayerShortcut(state, onClick = { onAction(BoingBallAction.MusicPlayer) }) },
                )
                if (isLandscape) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .width(landscapeGridWidth)
                                .height(176.dp)
                                .align(Alignment.TopEnd),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            userScrollEnabled = false,
                        ) {
                            items(shortcuts) { shortcut ->
                                Box(contentAlignment = Alignment.TopCenter) { shortcut() }
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(shortcuts) { shortcut ->
                            Box(contentAlignment = Alignment.TopCenter) { shortcut() }
                        }
                    }
                }
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
private fun BoingBallShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            painter = painterResource(Res.drawable.boing),
            contentDescription = stringResource(Res.string.about)
        )
        DesktopShortcutLabel(text = stringResource(Res.string.boing), osStyle = state.osStyle)
    }
}

@Composable
private fun DesktopShortcutLabel(
    text: String,
    osStyle: OSStyle,
) {
    AmigaTextBox(
        text = text,
        osStyle = osStyle,
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
    )
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
        DesktopShortcutLabel(text = stringResource(Res.string.preferences), osStyle = state.osStyle)
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
        DesktopShortcutLabel(text = stringResource(Res.string.clock), osStyle = state.osStyle)
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
        DesktopShortcutLabel(text = stringResource(Res.string.about), osStyle = state.osStyle)
    }
}

@Composable
private fun CopperBarsShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resId = if (state.osStyle == OSStyle.AmigaOS13)
        Res.drawable.copper
    else
        Res.drawable.copper30

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
        DesktopShortcutLabel(text = stringResource(Res.string.copper), osStyle = state.osStyle)
    }
}

@Composable
private fun CalculatorShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resId = if (state.osStyle == OSStyle.AmigaOS13)
        Res.drawable.calculator
    else
        Res.drawable.calculator30

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
        DesktopShortcutLabel(text = stringResource(Res.string.calculator), osStyle = state.osStyle)
    }
}

@Composable
private fun ShellShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resId = if (state.osStyle == OSStyle.AmigaOS13)
        Res.drawable.shell
    else
        Res.drawable.shell30

    val label = if (state.osStyle == OSStyle.AmigaOS13) {
        stringResource(Res.string.shell13)
    } else {
        stringResource(Res.string.shell)
    }

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .width(60.dp)
                .height(40.dp),
            painter = painterResource(resId),
            contentDescription = stringResource(Res.string.about)
        )
        DesktopShortcutLabel(text = label, osStyle = state.osStyle)
    }
}

@Composable
private fun MusicPlayerShortcut(
    state: BoingBallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            painter = painterResource(if (state.osStyle == OSStyle.AmigaOS13) Res.drawable.mplayer else Res.drawable.mplayer30),
            contentDescription = stringResource(Res.string.music_player),
        )
        DesktopShortcutLabel(text = stringResource(Res.string.music_player), osStyle = state.osStyle)
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
        WorkbenchScreen(previewState)
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun BoingBallScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        WorkbenchScreen(previewState)
    }
}

@Preview(device = "id:Nexus 4")
@Composable
private fun BoingBallScreenPreview() {
    BoingBallDemoTheme {
        WorkbenchScreen(previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}

@Preview(showSystemUi = true, device = "spec:parent=Nexus 4,orientation=landscape")
@Composable
private fun BoingBallScreenLandscapePreview() {
    BoingBallDemoTheme {
        WorkbenchScreen(previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}
