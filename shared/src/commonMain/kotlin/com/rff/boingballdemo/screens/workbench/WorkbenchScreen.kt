package com.rff.boingballdemo.screens.workbench

import androidx.compose.foundation.background
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.GuruMeditationOverlay
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Future development plan — making this a real Workbench experience:
 *
 * PHASE 1 — current screen improvements (near term, version 1.3.x):
 * - [*DONE*] Amiga screen title bar at top (thin strip: "Workbench Screen", right-aligned, OS-style aware)
 * - [*DONE*] Guru Meditation easter egg (long-press triggers iconic red/black error screen)
 * - [*DONE*] About window (3rd desktop icon, shows app/device info in Amiga Topaz style)
 * - [*DONE*] Clock window
 * - [*DONE*] Copper bars demo
 * - [*DONE*] simple Calculator app
 * - [*DONE*] AmigaDOS Shell - simple, no commands, just opens the window with a prompt. clicking anywhere closes it
 *   (built as ghost-typing demo: tap runs next canned command, close via toolbar gadget)
 *
 * PHASE 2 — additional features (version 1.4.x):
 * - [*DONE*] music player with a list of most iconic Amiga musics
 * - [*DROPPED*] History of Amiga logo by year
 * - [*DONE*] Add full screen Boing Ball view like real demo, some back button may be necessary
 *
 * PHASE 3 — more nice to have features (version 1.5.x):
 * - Allow user to change rotation speed
 * - Music Player interactive playlist window
 *
 * PHASE 4 — full Workbench rework (version 2.0.x):
 * - Amiga top menu bar on tap (menus: Workbench / Tools / Help, OS-style dropdowns)
 * - Rework main screen to look like real Workbench desktop
 * - App starts at desktop; user taps disk icon to open a drawer window (like a folder)
 * - Drawer window contains app icons: Boing Ball, Clock, Preferences, About, etc.
 * - Each icon launches its window as an overlay on the desktop (multiple windows open at once)
 * - Windows are movable/stackable (bring to front / send to back gadgets become functional)
 *
 * PHASE 5 — polish (version 2.1.x):
 * - Screen flip/push animation when switching OS style
 * - Amiga-style requester/dialog component (reusable, e.g. "Exit app?" confirmation)
 * - Workbench top menu bar items wired to real actions (About Workbench, etc.)
 */
@Composable
fun WorkbenchScreenRoot(
    viewModel: WorkbenchViewModel = koinViewModel(),
    onBoingBallClick: () -> Unit = {},
    onPreferencesClick: () -> Unit,
    onClockClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onCopperBarsClick: () -> Unit = {},
    onCalculatorClick: () -> Unit = {},
    onShellClick: () -> Unit = {},
    onMusicPlayerClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    WorkbenchScreen(
        state = state,
        onAction = { action ->
            when (action) {
                WorkbenchAction.BoingBall -> onBoingBallClick()
                WorkbenchAction.Preferences -> onPreferencesClick()
                WorkbenchAction.Clock -> onClockClick()
                WorkbenchAction.About -> onAboutClick()
                WorkbenchAction.CopperBars -> onCopperBarsClick()
                WorkbenchAction.Calculator -> onCalculatorClick()
                WorkbenchAction.Shell -> onShellClick()
                WorkbenchAction.MusicPlayer -> onMusicPlayerClick()
            }
        }
    )
}

@Composable
fun WorkbenchScreen(
    state: WorkbenchState,
    onAction: (WorkbenchAction) -> Unit = {},
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

                val shortcuts = workbenchShortcuts()
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
                                Box(contentAlignment = Alignment.TopCenter) {
                                    WorkbenchShortcutIcon(shortcut, state) { onAction(shortcut.action) }
                                }
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
                            Box(contentAlignment = Alignment.TopCenter) {
                                WorkbenchShortcutIcon(shortcut, state) { onAction(shortcut.action) }
                            }
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

private val previewState = WorkbenchState(
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

@Preview(device = "spec:parent=Nexus 4,orientation=landscape")
@Composable
private fun BoingBallScreenLandscapePreview() {
    BoingBallDemoTheme {
        WorkbenchScreen(previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}
