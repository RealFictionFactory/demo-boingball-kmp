package com.rff.boingballdemo.screens.workbench

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import com.rff.boingballdemo.component.AmigaTextBox
import com.rff.boingballdemo.component.OSStyle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal data class WorkbenchShortcut(
    val action: WorkbenchAction,
    val os13Icon: DrawableResource,
    val os20Icon: DrawableResource = os13Icon,
    val os13Label: StringResource,
    val os20Label: StringResource = os13Label,
    val iconWidth: Dp = 40.dp,
)

internal fun workbenchShortcuts() = listOf(
    WorkbenchShortcut(WorkbenchAction.BoingBall, Res.drawable.boing, os13Label = Res.string.boing),
    WorkbenchShortcut(WorkbenchAction.About, Res.drawable.about, Res.drawable.about30, Res.string.about),
    WorkbenchShortcut(WorkbenchAction.Preferences, Res.drawable.preferences, Res.drawable.prefs30, Res.string.preferences, iconWidth = 80.dp),
    WorkbenchShortcut(WorkbenchAction.Clock, Res.drawable.clock, Res.drawable.clock30, Res.string.clock),
    WorkbenchShortcut(WorkbenchAction.Shell, Res.drawable.shell, Res.drawable.shell30, Res.string.shell13, Res.string.shell, iconWidth = 60.dp),
    WorkbenchShortcut(WorkbenchAction.Calculator, Res.drawable.calculator, Res.drawable.calculator30, Res.string.calculator),
    WorkbenchShortcut(WorkbenchAction.CopperBars, Res.drawable.copper, Res.drawable.copper30, Res.string.copper),
    WorkbenchShortcut(WorkbenchAction.MusicPlayer, Res.drawable.mplayer, Res.drawable.mplayer30, Res.string.music_player),
)

@Composable
internal fun WorkbenchShortcutIcon(
    shortcut: WorkbenchShortcut,
    state: WorkbenchState,
    onClick: () -> Unit,
) {
    val isOs13 = state.osStyle == OSStyle.AmigaOS13
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.width(shortcut.iconWidth).height(40.dp),
            painter = painterResource(if (isOs13) shortcut.os13Icon else shortcut.os20Icon),
            contentDescription = stringResource(if (isOs13) shortcut.os13Label else shortcut.os20Label),
        )
        AmigaTextBox(
            text = stringResource(if (isOs13) shortcut.os13Label else shortcut.os20Label),
            osStyle = state.osStyle,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
