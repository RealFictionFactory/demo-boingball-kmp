package com.rff.boingballdemo.musicplayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.ic_fast_forward
import boingball.shared.generated.resources.ic_fast_rewind
import boingball.shared.generated.resources.ic_pause
import boingball.shared.generated.resources.ic_play
import boingball.shared.generated.resources.ic_skip_next
import boingball.shared.generated.resources.ic_skip_previous
import boingball.shared.generated.resources.ic_stop
import boingball.shared.generated.resources.music_player
import boingball.shared.generated.resources.music_player_version
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.AmigaToolbar
import com.rff.boingballdemo.component.AmigaWindow
import com.rff.boingballdemo.component.drawFrame
import com.rff.boingballdemo.main.conditional
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs13Orange
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.amigaOs30Grey
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.ui.theme.topazFont20
import com.rff.boingballdemo.ui.theme.whiteColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val SEEK_STEP_MS = 10_000L

@Composable
fun MusicPlayerScreenRoot(
    viewModel: MusicPlayerViewModel = koinViewModel(),
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MusicPlayerScreen(
        state = state,
        onAction = viewModel::onAction,
        onCloseClick = onCloseClick,
    )
}

@Composable
fun MusicPlayerScreen(
    state: MusicPlayerState,
    onAction: (MusicPlayerAction) -> Unit = {},
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
            LandscapeMusicPlayerLayout(state, onAction, onCloseClick, minOf(maxWidth * 0.72f, 520.dp))
        } else {
            PortraitMusicPlayerLayout(state, onAction, onCloseClick, maxWidth * 0.92f)
        }
    }
}

@Composable
private fun PortraitMusicPlayerLayout(state: MusicPlayerState, onAction: (MusicPlayerAction) -> Unit, onCloseClick: () -> Unit, windowWidth: Dp) =
    MusicPlayerLayout(state, onAction, onCloseClick, windowWidth)

@Composable
private fun LandscapeMusicPlayerLayout(state: MusicPlayerState, onAction: (MusicPlayerAction) -> Unit, onCloseClick: () -> Unit, windowWidth: Dp) =
    MusicPlayerLayout(state, onAction, onCloseClick, windowWidth)

@Composable
private fun MusicPlayerLayout(state: MusicPlayerState, onAction: (MusicPlayerAction) -> Unit, onCloseClick: () -> Unit, windowWidth: Dp) {
        Column(modifier = Modifier.fillMaxSize()) {
            AmigaScreenTitleBar(
                text = stringResource(Res.string.workbench),
                osStyle = state.osStyle,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                AmigaWindow(
                    modifier = Modifier.width(windowWidth),
                    title = stringResource(Res.string.music_player) + " " + stringResource(Res.string.music_player_version),
                    osStyle = state.osStyle,
                    onCloseClick = onCloseClick,
                ) { contentModifier ->
                    MusicPlayerContent(state = state, onAction = onAction, modifier = contentModifier)
                }
            }
        }
}

@Composable
private fun MusicPlayerContent(
    state: MusicPlayerState,
    onAction: (MusicPlayerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = if (state.osStyle == OSStyle.AmigaOS13) amigaOs13Blue else backgroundColor)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NowPlayingPanel(state = state)
        TransportBar(state = state, onAction = onAction)
    }
}

@Composable
private fun NowPlayingPanel(
    state: MusicPlayerState,
    modifier: Modifier = Modifier,
) {
    val isOs13 = state.osStyle == OSStyle.AmigaOS13
    val title = state.currentTrack?.title.orEmpty()
    val timeText = buildString {
        append(formatPlaybackTime(state.positionMs))
        append(" / ")
        append(formatPlaybackTime(state.currentTrack?.durationMs ?: 0L))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .conditional(
                condition = isOs13,
                ifTrue = { background(color = whiteColor).padding(1.dp) },
                ifFalse = {
                    background(color = blackColor)
                        .padding(start = 1.dp, top = 1.dp)
                        .background(color = whiteColor)
                        .padding(end = 1.dp, bottom = 1.dp)
                },
            )
            .background(color = blackColor)
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Text(
            text = title,
            fontFamily = if (isOs13) topazFont() else topazFont20(),
            fontSize = 16.sp,
            color = whiteColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = timeText,
            fontFamily = if (isOs13) topazFont() else topazFont20(),
            fontSize = 16.sp,
            color = whiteColor,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TransportBar(
    state: MusicPlayerState,
    onAction: (MusicPlayerAction) -> Unit,
) {
    val playPauseIcon = if (state.isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play
    val playPauseAction = if (state.isPlaying) MusicPlayerAction.Pause else MusicPlayerAction.Play
    val playPauseDescription = if (state.isPlaying) "Pause" else "Play"
    val buttons = listOf(
        Triple(Res.drawable.ic_skip_previous, MusicPlayerAction.Previous, "Previous"),
        Triple(Res.drawable.ic_fast_rewind, MusicPlayerAction.Seek(-SEEK_STEP_MS), "Rewind"),
        Triple(playPauseIcon, playPauseAction, playPauseDescription),
        Triple(Res.drawable.ic_stop, MusicPlayerAction.Stop, "Stop"),
        Triple(Res.drawable.ic_fast_forward, MusicPlayerAction.Seek(SEEK_STEP_MS), "Forward"),
        Triple(Res.drawable.ic_skip_next, MusicPlayerAction.Next, "Next"),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        buttons.forEach { (icon, action, description) ->
            TransportButton(
                icon = icon,
                osStyle = state.osStyle,
                contentDescription = description,
                onClick = { onAction(action) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
            )
        }
    }
}

@Composable
private fun TransportButton(
    icon: DrawableResource,
    osStyle: OSStyle,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isOs13 = osStyle == OSStyle.AmigaOS13
    val iconTint = when {
        isOs13 && isPressed -> blackColor
        isOs13 -> whiteColor
        else -> blackColor
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .then(
                if (isOs13) {
                    Modifier
                        .background(color = if (isPressed) amigaOs13Orange else amigaOs13Blue)
                        .border(width = 1.dp, color = whiteColor)
                } else {
                    Modifier.drawBehind {
                        drawFrame(
                            strokePx = size.minDimension / 24f,
                            fillColor = if (isPressed) amigaOs30Blue else amigaOs30Grey,
                            topLeftColor = if (isPressed) blackColor else whiteColor,
                            bottomRightColor = if (isPressed) whiteColor else blackColor,
                        )
                    }
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(iconTint),
            modifier = Modifier.size(20.dp),
        )
    }
}

private val previewState = MusicPlayerState(
    osStyle = OSStyle.AmigaOS13,
    tracks = AMIGA_MUSIC_TRACKS,
    currentTrackIndex = 0,
    isPlaying = true,
    positionMs = 72_000L,
)

@Preview(device = "id:pixel_10")
@Composable
private fun MusicPlayerScreenOs13Preview() {
    BoingBallDemoTheme {
        MusicPlayerScreen(state = previewState)
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun MusicPlayerScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        MusicPlayerScreen(state = previewState)
    }
}

@Preview(device = "id:pixel_10")
@Composable
private fun MusicPlayerScreenOs30Preview() {
    BoingBallDemoTheme {
        MusicPlayerScreen(state = previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun MusicPlayerScreenLandscapeOs30Preview() {
    BoingBallDemoTheme {
        MusicPlayerScreen(state = previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}
