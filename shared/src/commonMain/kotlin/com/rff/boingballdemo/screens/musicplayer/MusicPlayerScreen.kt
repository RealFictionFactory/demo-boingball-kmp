package com.rff.boingballdemo.screens.musicplayer

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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.chaotic_player
import boingball.shared.generated.resources.ic_fast_forward
import boingball.shared.generated.resources.ic_fast_rewind
import boingball.shared.generated.resources.ic_move_down
import boingball.shared.generated.resources.ic_move_up
import boingball.shared.generated.resources.ic_pause
import boingball.shared.generated.resources.ic_play
import boingball.shared.generated.resources.ic_playlist
import boingball.shared.generated.resources.ic_skip_next
import boingball.shared.generated.resources.ic_skip_previous
import boingball.shared.generated.resources.ic_stop
import boingball.shared.generated.resources.music_player_version
import boingball.shared.generated.resources.playlist
import boingball.shared.generated.resources.playlist_move_down
import boingball.shared.generated.resources.playlist_move_up
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.AmigaWindow
import com.rff.boingballdemo.component.drawFrame
import com.rff.boingballdemo.utils.conditional
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
    initiallyShowPlaylist: Boolean = false,
) {
    val bg = if (state.osStyle == OSStyle.AmigaOS20) backgroundColor else amigaOs13Blue
    var showPlaylist by rememberSaveable { mutableStateOf(initiallyShowPlaylist) }
    var selectedTrackIndex by rememberSaveable { mutableIntStateOf(state.currentTrackIndex) }
    val moveTrack: (Int) -> Unit = { direction ->
        val target = selectedTrackIndex + direction
        if (selectedTrackIndex in state.tracks.indices && target in state.tracks.indices) {
            onAction(MusicPlayerAction.MoveTrack(selectedTrackIndex, direction))
            selectedTrackIndex = target
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        val isLandscape = maxWidth > maxHeight
        val screenWidth = maxWidth
        Column(modifier = Modifier.fillMaxSize()) {
            AmigaScreenTitleBar(
                text = stringResource(Res.string.workbench),
                osStyle = state.osStyle,
            )
            if (isLandscape) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth().padding(4.dp)) {
                    PlayerWindow(
                        state = state,
                        onAction = onAction,
                        onCloseClick = onCloseClick,
                        onPlaylistClick = {
                            selectedTrackIndex = state.currentTrackIndex
                            showPlaylist = true
                        },
                        modifier = Modifier.width(
                            if (showPlaylist) minOf(screenWidth * 0.55f, 520.dp)
                            else minOf(screenWidth * 0.72f, 520.dp)
                        ),
                    )
                    if (showPlaylist) {
                        Spacer(Modifier.width(8.dp))
                        BoxWithConstraints(Modifier.weight(1f).fillMaxHeight()) {
                            PlaylistWindow(
                                state = state,
                                selectedIndex = selectedTrackIndex,
                                onSelect = { selectedTrackIndex = it },
                                onMove = moveTrack,
                                onCloseClick = { showPlaylist = false },
                                maxListHeight = (maxHeight - 52.dp).coerceAtLeast(1.dp),
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val windowWidth = screenWidth * 0.92f
                    PlayerWindow(
                        state = state,
                        onAction = onAction,
                        onCloseClick = onCloseClick,
                        onPlaylistClick = {
                            selectedTrackIndex = state.currentTrackIndex
                            showPlaylist = true
                        },
                        modifier = Modifier.width(windowWidth),
                    )
                    if (showPlaylist) {
                        Spacer(Modifier.height(8.dp))
                        BoxWithConstraints(Modifier.weight(1f).width(windowWidth)) {
                            PlaylistWindow(
                                state = state,
                                selectedIndex = selectedTrackIndex,
                                onSelect = { selectedTrackIndex = it },
                                onMove = moveTrack,
                                onCloseClick = { showPlaylist = false },
                                maxListHeight = (maxHeight - 52.dp).coerceAtLeast(1.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerWindow(
    state: MusicPlayerState,
    onAction: (MusicPlayerAction) -> Unit,
    onCloseClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AmigaWindow(
        modifier = modifier,
        title = stringResource(Res.string.chaotic_player) + " " + stringResource(Res.string.music_player_version),
        osStyle = state.osStyle,
        onCloseClick = onCloseClick,
    ) { contentModifier ->
        MusicPlayerContent(state, onAction, onPlaylistClick, contentModifier)
    }
}

@Composable
private fun PlaylistWindow(
    state: MusicPlayerState,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onMove: (Int) -> Unit,
    onCloseClick: () -> Unit,
    maxListHeight: Dp,
) {
    AmigaWindow(
        title = stringResource(Res.string.playlist),
        osStyle = state.osStyle,
        onCloseClick = onCloseClick,
    ) { contentModifier ->
        Row(
            modifier = contentModifier
                .background(if (state.osStyle == OSStyle.AmigaOS13) amigaOs13Blue else backgroundColor)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = maxListHeight)
                    .drawBehind {
                        val stroke = 1.dp.toPx()
                        val topLeft = if (state.osStyle == OSStyle.AmigaOS13) whiteColor else blackColor
                        drawRect(topLeft, size = Size(size.width, stroke))
                        drawRect(topLeft, size = Size(stroke, size.height))
                        drawRect(whiteColor, topLeft = Offset(0f, size.height - stroke), size = Size(size.width, stroke))
                        drawRect(whiteColor, topLeft = Offset(size.width - stroke, 0f), size = Size(stroke, size.height))
                    }
                    .padding(1.dp),
            ) {
                itemsIndexed(state.tracks) { index, track ->
                    val isSelected = index == selectedIndex
                    val isOs13 = state.osStyle == OSStyle.AmigaOS13
                    Text(
                        text = track.title,
                        fontFamily = if (isOs13) topazFont() else topazFont20(),
                        fontSize = 16.sp,
                        color = if (isOs13 || isSelected) whiteColor else blackColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isOs13 && isSelected) {
                                    Modifier.border(1.dp, amigaOs13Orange)
                                } else if (!isOs13 && isSelected) {
                                    Modifier.background(blackColor)
                                } else Modifier
                            )
                            .clickable { onSelect(index) }
                            .padding(8.dp),
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TransportButton(
                    icon = Res.drawable.ic_move_up,
                    osStyle = state.osStyle,
                    contentDescription = stringResource(Res.string.playlist_move_up),
                    onClick = { onMove(-1) },
                    modifier = Modifier.size(40.dp),
                )
                TransportButton(
                    icon = Res.drawable.ic_move_down,
                    osStyle = state.osStyle,
                    contentDescription = stringResource(Res.string.playlist_move_down),
                    onClick = { onMove(1) },
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }
}

@Composable
private fun MusicPlayerContent(
    state: MusicPlayerState,
    onAction: (MusicPlayerAction) -> Unit,
    onPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = if (state.osStyle == OSStyle.AmigaOS13) amigaOs13Blue else backgroundColor)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NowPlayingPanel(state = state, onPlaylistClick = onPlaylistClick)
        TransportBar(state = state, onAction = onAction)
    }
}

@Composable
private fun NowPlayingPanel(
    state: MusicPlayerState,
    onPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOs13 = state.osStyle == OSStyle.AmigaOS13
    val title = state.currentTrack?.title.orEmpty()
    val timeText = buildString {
        append(formatPlaybackTime(state.positionMs))
        append(" / ")
        append(formatPlaybackTime(state.currentTrack?.durationMs ?: 0L))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
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
        Spacer(modifier = Modifier.width(4.dp))
        TransportButton(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f, matchHeightConstraintsFirst = true),
            icon = Res.drawable.ic_playlist,
            osStyle = state.osStyle,
            contentDescription = stringResource(Res.string.playlist),
            onClick = onPlaylistClick,
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

@Preview(device = "id:pixel_10")
@Composable
private fun MusicPlayerScreenWithPlaylistOs13Preview() {
    BoingBallDemoTheme {
        MusicPlayerScreen(state = previewState, initiallyShowPlaylist = true)
    }
}

@Preview(device = "spec:parent=pixel_4,orientation=landscape")
@Composable
private fun MusicPlayerScreenWithPlaylistOs30Preview() {
    BoingBallDemoTheme {
        MusicPlayerScreen(
            state = previewState.copy(osStyle = OSStyle.AmigaOS20),
            initiallyShowPlaylist = true,
        )
    }
}
