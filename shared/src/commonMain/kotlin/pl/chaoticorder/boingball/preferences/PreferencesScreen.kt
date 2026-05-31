package pl.chaoticorder.boingball.preferences

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LocalTextStyle
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
import boingball.shared.generated.resources.preferences
import boingball.shared.generated.resources.preferences_application_version
import boingball.shared.generated.resources.preferences_draw_bb_square_borders
import boingball.shared.generated.resources.preferences_pick_alternate_bb_color
import boingball.shared.generated.resources.preferences_pick_main_bb_color
import boingball.shared.generated.resources.preferences_save_current_settings
import boingball.shared.generated.resources.preferences_set_amigaos_1_3_style
import boingball.shared.generated.resources.preferences_set_amigaos_2_style
import boingball.shared.generated.resources.preferences_set_app_defaults
import boingball.shared.generated.resources.preferences_set_demo_defaults
import pl.chaoticorder.boingball.component.AmigaButton
import pl.chaoticorder.boingball.component.AmigaCheckBox
import pl.chaoticorder.boingball.component.AmigaColorPicker
import pl.chaoticorder.boingball.component.AmigaToolbar
import pl.chaoticorder.boingball.component.OSStyle
import pl.chaoticorder.boingball.main.conditional
import pl.chaoticorder.boingball.ui.theme.AltAmigaOs13PickerColors
import pl.chaoticorder.boingball.ui.theme.BoingBallDemoTheme
import pl.chaoticorder.boingball.ui.theme.amigaOs13Blue
import pl.chaoticorder.boingball.ui.theme.amigaOs30Blue
import pl.chaoticorder.boingball.ui.theme.backgroundColor
import pl.chaoticorder.boingball.ui.theme.blackColor
import pl.chaoticorder.boingball.ui.theme.topazFont
import pl.chaoticorder.boingball.ui.theme.topazFont20
import pl.chaoticorder.boingball.ui.theme.whiteColor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pl.chaoticorder.boingball.component.AmigaTextBox
import kotlin.collections.copy

/**
 * possible settings to change:
 * DONE:
 * - Boing Ball colors (main [red, blue, green] and alternate [white, other?])
 * - Draw Boing Ball square borders (true/false)
 * - OS 1.3 / 2.0+ - changes toolbar and font
 * IN PROGRESS:
 * UPCOMING:
 * - Boing Ball segments number
 */

@Composable
fun PreferencesScreenRoot(
    viewModel: PreferencesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PreferencesScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Composable
fun PreferencesScreen(
    state: PreferencesState,
    onAction: (PreferencesAction) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = maxWidth > maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmigaToolbar(
                title = stringResource(Res.string.preferences),
                osStyle = state.osStyle
            )

            if (isLandscape) {
                LandscapePreferencesLayout(state, onAction)
            } else {
                PortraitPreferencesLayout(state, onAction)
            }
        }
    }
}

@Composable
fun PortraitPreferencesLayout(
    state: PreferencesState,
    onAction: (PreferencesAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
                        .background(color = backgroundColor)
                }
            )
            .padding(16.dp)
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
            modifier = Modifier.padding(vertical = 8.dp),
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
        Spacer(modifier = Modifier.height(24.dp))
        AmigaButton(
            text = stringResource(Res.string.preferences_save_current_settings),
            osStyle = state.osStyle,
            onClick = { onAction(PreferencesAction.SaveSettings) }
        )
        Spacer(modifier = Modifier.weight(1f))
        AmigaTextBox(
            text = stringResource(Res.string.preferences_application_version, state.appVersion),
            osStyle = state.osStyle
        )
    }
}

@Composable
fun LandscapePreferencesLayout(
    state: PreferencesState,
    onAction: (PreferencesAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
                        .background(color = backgroundColor)
                }
            )
            .padding(16.dp)
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
                    modifier = Modifier.padding(vertical = 8.dp),
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
                Spacer(modifier = Modifier.height(24.dp))
                AmigaButton(
                    text = stringResource(Res.string.preferences_save_current_settings),
                    osStyle = state.osStyle,
                    onClick = { onAction(PreferencesAction.SaveSettings) }
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        AmigaTextBox(
            text = stringResource(Res.string.preferences_application_version, state.appVersion),
            osStyle = state.osStyle
        )
    }
}

private val previewState = PreferencesState(
    osStyle = OSStyle.AmigaOS20
)

@Preview
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
