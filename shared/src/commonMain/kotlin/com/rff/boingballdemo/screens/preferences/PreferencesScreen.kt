package com.rff.boingballdemo.screens.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.preferences
import boingball.shared.generated.resources.preferences_draw_bb_square_borders
import boingball.shared.generated.resources.preferences_pick_alternate_bb_color
import boingball.shared.generated.resources.preferences_pick_main_bb_color
import boingball.shared.generated.resources.preferences_set_amigaos_1_3_style
import boingball.shared.generated.resources.preferences_set_amigaos_2_style
import boingball.shared.generated.resources.preferences_set_app_defaults
import boingball.shared.generated.resources.preferences_set_demo_defaults
import boingball.shared.generated.resources.preferences_video_system
import boingball.shared.generated.resources.preferences_video_system_help
import boingball.shared.generated.resources.questionmark
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaButton
import com.rff.boingballdemo.component.AmigaCheckBox
import com.rff.boingballdemo.component.AmigaColorPicker
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaSelect
import com.rff.boingballdemo.component.AmigaTextBox
import com.rff.boingballdemo.component.AmigaWindow
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.VideoSystem
import com.rff.boingballdemo.ui.theme.AltAmigaOs13PickerColors
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * possible settings to change:
 * DONE:
 * - Boing Ball colors (main [red, blue, green] and alternate [white, other?])
 * - Draw Boing Ball square borders (true/false)
 * - OS 1.3 / 2.0+ - changes toolbar and font
 * - PAL / NTSC video system (ball speed)
 * IN PROGRESS:
 * UPCOMING:
 * - Boing Ball segments number
 */

@Composable
fun PreferencesScreenRoot(
    viewModel: PreferencesViewModel = koinViewModel(),
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PreferencesScreen(
        state = state,
        onCloseClick = onCloseClick,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Composable
fun PreferencesScreen(
    state: PreferencesState,
    onCloseClick: () -> Unit = {},
    onAction: (PreferencesAction) -> Unit,
) {
    var showVideoSystemHelp by remember { mutableStateOf(false) }
    val bg = if (state.osStyle == OSStyle.AmigaOS20)
        backgroundColor
    else
        amigaOs13Blue

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = maxWidth > maxHeight

        Column(modifier = Modifier.fillMaxSize()) {
            AmigaScreenTitleBar(
                text = stringResource(Res.string.workbench),
                osStyle = state.osStyle
            )
            AmigaWindow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(4.dp),
                title = stringResource(Res.string.preferences),
                osStyle = state.osStyle,
                onCloseClick = onCloseClick,
            ) { contentModifier ->
                if (isLandscape) {
                    LandscapePreferencesLayout(state, onAction, { showVideoSystemHelp = true }, contentModifier)
                } else {
                    PortraitPreferencesLayout(state, onAction, { showVideoSystemHelp = true }, contentModifier)
                }
            }
        } // end outer Column

        if (showVideoSystemHelp) {
            VideoSystemHelpWindow(
                osStyle = state.osStyle,
                isLandscape = isLandscape,
                maxWidth = maxWidth,
                onDismiss = { showVideoSystemHelp = false },
            )
        }
    }
}

@Composable
fun PortraitPreferencesLayout(
    state: PreferencesState,
    onAction: (PreferencesAction) -> Unit,
    onVideoSystemHelpClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        AmigaTextBox(
            text = stringResource(Res.string.preferences_pick_main_bb_color),
            osStyle = state.osStyle
        )
        AmigaColorPicker(
            selectedIndex = state.themeColorIndex,
            osStyle = state.osStyle,
            onColorSelected = { index ->
                onAction(PreferencesAction.ChangeThemeColor(index))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        AmigaTextBox(
            text = stringResource(Res.string.preferences_pick_alternate_bb_color),
            osStyle = state.osStyle
        )
        AmigaColorPicker(
            selectedIndex = state.altColorIndex,
            osStyle = state.osStyle,
            colors = AltAmigaOs13PickerColors,
            onColorSelected = { index ->
                onAction(PreferencesAction.ChangeAltColor(index))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AmigaTextBox(
                text = stringResource(Res.string.preferences_draw_bb_square_borders),
                osStyle = state.osStyle
            )
            Spacer(modifier = Modifier.width(8.dp))
            AmigaCheckBox(
                isChecked = state.drawBorders,
                osStyle = state.osStyle,
                onCheckChanged = { newState ->
                    onAction(PreferencesAction.ChangeFrameDraw(newState))
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        VideoSystemSelector(state = state, onAction = onAction, onHelpClick = onVideoSystemHelpClick)
        Spacer(modifier = Modifier.height(16.dp))
        AmigaButton(
            text = stringResource(
                if (state.osStyle == OSStyle.AmigaOS13) Res.string.preferences_set_amigaos_2_style
                else Res.string.preferences_set_amigaos_1_3_style
            ),
            osStyle = state.osStyle,
            onClick = {
                onAction(
                    if (state.osStyle == OSStyle.AmigaOS13) PreferencesAction.SetAmigaOS20
                    else PreferencesAction.SetAmigaOS13
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        AmigaButton(
            text = stringResource(Res.string.preferences_set_demo_defaults),
            osStyle = state.osStyle,
            onClick = { onAction(PreferencesAction.BringDefaults) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        AmigaButton(
            text = stringResource(Res.string.preferences_set_app_defaults),
            osStyle = state.osStyle,
            onClick = { onAction(PreferencesAction.BringAppDefaults) }
        )
    }
}

@Composable
fun LandscapePreferencesLayout(
    state: PreferencesState,
    onAction: (PreferencesAction) -> Unit,
    onVideoSystemHelpClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AmigaTextBox(
                    text = stringResource(Res.string.preferences_pick_main_bb_color),
                    osStyle = state.osStyle
                )
                AmigaColorPicker(
                    selectedIndex = state.themeColorIndex,
                    osStyle = state.osStyle,
                    onColorSelected = { index ->
                        onAction(PreferencesAction.ChangeThemeColor(index))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                AmigaTextBox(
                    text = stringResource(Res.string.preferences_pick_alternate_bb_color),
                    osStyle = state.osStyle
                )
                AmigaColorPicker(
                    selectedIndex = state.altColorIndex,
                    osStyle = state.osStyle,
                    colors = AltAmigaOs13PickerColors,
                    onColorSelected = { index ->
                        onAction(PreferencesAction.ChangeAltColor(index))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AmigaTextBox(
                        text = stringResource(Res.string.preferences_draw_bb_square_borders),
                        osStyle = state.osStyle
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AmigaCheckBox(
                        isChecked = state.drawBorders,
                        osStyle = state.osStyle,
                        onCheckChanged = { newState ->
                            onAction(PreferencesAction.ChangeFrameDraw(newState))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                VideoSystemSelector(state = state, onAction = onAction, onHelpClick = onVideoSystemHelpClick)
                Spacer(modifier = Modifier.height(16.dp))
                AmigaButton(
                    text = stringResource(
                        if (state.osStyle == OSStyle.AmigaOS13) Res.string.preferences_set_amigaos_2_style
                        else Res.string.preferences_set_amigaos_1_3_style
                    ),
                    osStyle = state.osStyle,
                    onClick = {
                        onAction(
                            if (state.osStyle == OSStyle.AmigaOS13) PreferencesAction.SetAmigaOS20
                            else PreferencesAction.SetAmigaOS13
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                AmigaButton(
                    text = stringResource(Res.string.preferences_set_demo_defaults),
                    osStyle = state.osStyle,
                    onClick = { onAction(PreferencesAction.BringDefaults) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                AmigaButton(
                    text = stringResource(Res.string.preferences_set_app_defaults),
                    osStyle = state.osStyle,
                    onClick = { onAction(PreferencesAction.BringAppDefaults) }
                )
            }
        }
    }
}

@Composable
private fun VideoSystemSelector(
    state: PreferencesState,
    onAction: (PreferencesAction) -> Unit,
    onHelpClick: () -> Unit,
) {
    val labels = VideoSystem.entries.associateWith { stringResource(it.labelRes) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AmigaSelect(
            modifier = Modifier.width(120.dp),
            text = stringResource(Res.string.preferences_video_system),
            options = VideoSystem.entries.map { labels.getValue(it) },
            selectedOption = labels.getValue(state.videoSystem),
            osStyle = state.osStyle,
            onOptionSelected = { selectedOption ->
                VideoSystem.fromLabel(selectedOption, labels)?.let { system ->
                    onAction(PreferencesAction.SetVideoSystem(system))
                }
            },
        )
        Spacer(modifier = Modifier.width(16.dp))
        AmigaButton(
            text = "?",
            osStyle = state.osStyle,
            onClick = onHelpClick,
        )
    }
}

@Composable
private fun VideoSystemHelpWindow(
    osStyle: OSStyle,
    isLandscape: Boolean,
    maxWidth: Dp,
    onDismiss: () -> Unit,
) {
    val windowWidth = if (isLandscape) maxWidth * 0.5f else maxWidth - 48.dp
    val background = if (osStyle == OSStyle.AmigaOS13) amigaOs13Blue else backgroundColor

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AmigaWindow(
            modifier = Modifier.width(windowWidth),
            title = stringResource(Res.string.preferences_video_system),
            osStyle = osStyle,
            onCloseClick = onDismiss,
        ) { _ ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(background)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(Res.drawable.questionmark),
                    contentDescription = null,
                    modifier = Modifier.size(width = 80.dp, height = 96.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                AmigaTextBox(
                    text = stringResource(Res.string.preferences_video_system_help),
                    osStyle = osStyle,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private val previewState = PreferencesState(
    osStyle = OSStyle.AmigaOS20
)

@Preview(locale = "pl")
@Composable
private fun PreferencesScreenPortraitOs13Preview() {
    BoingBallDemoTheme {
        PreferencesScreen(
            state = previewState.copy(osStyle = OSStyle.AmigaOS13),
            onAction = {}
        )
    }
}

@Preview(device = "spec:parent=Nexus 5,orientation=landscape")
@Composable
private fun PreferencesScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        PreferencesScreen(
            state = previewState.copy(osStyle = OSStyle.AmigaOS13),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun PreferencesScreenPortraitOs30Preview() {
    BoingBallDemoTheme {
        PreferencesScreen(
            state = previewState,
            onAction = {}
        )
    }
}

@Preview(device = "spec:parent=Nexus 5,orientation=landscape")
@Composable
private fun PreferencesScreenLandscapeOs30Preview() {
    BoingBallDemoTheme {
        PreferencesScreen(
            state = previewState,
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun VideoSystemHelpWindowPreview() {
    BoingBallDemoTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
        ) {
            VideoSystemHelpWindow(
                osStyle = previewState.osStyle,
                isLandscape = false,
                maxWidth = 360.dp,
                onDismiss = {},
            )
        }
    }
}
