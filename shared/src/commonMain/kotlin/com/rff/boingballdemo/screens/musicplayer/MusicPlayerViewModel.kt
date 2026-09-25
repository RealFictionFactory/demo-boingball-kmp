package com.rff.boingballdemo.screens.musicplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PROGRESS_TICK_MS = 200L

internal val AMIGA_MUSIC_TRACKS = listOf(
    MusicTrack(
        "Shadow of the Beast",
        "David Whittaker",
        201_625L,
        "files/Shadow.of.the.Beast.by.D.Whittaker.mp3",
    ),
    MusicTrack(
        "Turrican 2 - The Final Fight",
        "Chris Huelsbeck",
        432_457L,
        "files/Turrican.2.Title-The.Final.Fight.mp3",
    ),
    MusicTrack(
        "Lotus 2",
        "Barry Leitch",
        170_000L,
        "files/Lotus.2.title.mp3",
    ),
    MusicTrack(
        "Lemmings 2",
        "Raymond Usher",
        115_836L,
        "files/Lemmings.2.mp3",
    ),
    MusicTrack(
        "Cannon Fodder",
        "Richard Joseph",
        146_661L,
        "files/Cannon.Fodder.mp3",
    ),
    MusicTrack(
        "IK+",
        "Dave Lowe",
        457_295L,
        "files/IKPlus.by.Dave.Lowe.mp3",
    ),
    MusicTrack(
        "Gods - Into the Wonderful",
        "Nation 12",
        153_624L,
        "files/Gods.Into.the.Wonderful.mp3",
    ),
    MusicTrack(
        "The Chaos Engine",
        "Richard Joseph",
        122_863L,
        "files/Chaos.Engine.by.Richard.Joseph.mp3",
    ),
)

class MusicPlayerViewModel(
    private val settings: AppSettings,
    private val playback: MusicPlayback,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MusicPlayerState(tracks = AMIGA_MUSIC_TRACKS)
    )
    val uiState: StateFlow<MusicPlayerState> = _uiState.asStateFlow()

    private var progressJob: Job? = null

    init {
        settings.boingBallPrefs
            .onEach { prefs -> _uiState.update { it.copy(osStyle = prefs.osStyle) } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: MusicPlayerAction) {
        var command: PlaybackCommand? = null
        _uiState.update { state ->
            val step = state.step(action)
            command = step.command
            step.state
        }
        dispatch(command)
    }

    override fun onCleared() {
        progressJob?.cancel()
        playback.release()
    }

    private fun dispatch(command: PlaybackCommand?) {
        when (command) {
            is PlaybackCommand.Play -> {
                playback.play(command.resourcePath, command.positionMs)
                startProgress()
            }
            PlaybackCommand.Pause -> {
                playback.pause()
                stopProgress()
            }
            PlaybackCommand.Stop -> {
                playback.stop()
                stopProgress()
            }
            is PlaybackCommand.Seek -> playback.seekTo(command.positionMs)
            null -> Unit
        }
    }

    private fun startProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                delay(PROGRESS_TICK_MS)
                if (!_uiState.value.isPlaying) continue
                if (playback.hasEnded()) {
                    onAction(MusicPlayerAction.Next)
                    return@launch
                }
                val position = playback.currentPositionMs()
                _uiState.update { state ->
                    if (!state.isPlaying) state else state.copy(positionMs = position)
                }
            }
        }
    }

    private fun stopProgress() {
        progressJob?.cancel()
        progressJob = null
    }
}

internal sealed interface PlaybackCommand {
    data class Play(val resourcePath: String, val positionMs: Long) : PlaybackCommand
    data object Pause : PlaybackCommand
    data object Stop : PlaybackCommand
    data class Seek(val positionMs: Long) : PlaybackCommand
}

internal data class PlaybackStep(
    val state: MusicPlayerState,
    val command: PlaybackCommand?,
)

internal fun MusicPlayerState.step(action: MusicPlayerAction): PlaybackStep {
    val after = reduce(action)
    val command = when (action) {
        MusicPlayerAction.Play -> after.currentTrack?.let {
            PlaybackCommand.Play(it.resourcePath, after.positionMs)
        }
        MusicPlayerAction.Pause -> PlaybackCommand.Pause
        MusicPlayerAction.Stop -> PlaybackCommand.Stop
        is MusicPlayerAction.Seek -> PlaybackCommand.Seek(after.positionMs)
        MusicPlayerAction.Next,
        MusicPlayerAction.Previous,
        -> trackChangeCommand(after)
        is MusicPlayerAction.SelectTrack -> {
            if (action.index !in tracks.indices) null
            else trackChangeCommand(after)
        }
    }
    return PlaybackStep(after, command)
}

private fun trackChangeCommand(after: MusicPlayerState): PlaybackCommand {
    val path = after.currentTrack?.resourcePath
    return if (after.isPlaying && path != null) {
        PlaybackCommand.Play(path, 0L)
    } else {
        PlaybackCommand.Stop
    }
}

internal fun MusicPlayerState.reduce(action: MusicPlayerAction): MusicPlayerState {
    if (tracks.isEmpty()) return this

    return when (action) {
        MusicPlayerAction.Play -> copy(isPlaying = true)
        MusicPlayerAction.Pause -> copy(isPlaying = false)
        MusicPlayerAction.Stop -> copy(isPlaying = false, positionMs = 0L)
        MusicPlayerAction.Next -> copy(
            currentTrackIndex = (currentTrackIndex + 1) % tracks.size,
            positionMs = 0L,
        )
        MusicPlayerAction.Previous -> copy(
            currentTrackIndex = (currentTrackIndex - 1 + tracks.size) % tracks.size,
            positionMs = 0L,
        )
        is MusicPlayerAction.Seek -> {
            val duration = currentTrack?.durationMs ?: return this
            copy(positionMs = (positionMs + action.deltaMs).coerceIn(0L, duration))
        }
        is MusicPlayerAction.SelectTrack -> {
            if (action.index !in tracks.indices) this
            else copy(currentTrackIndex = action.index, positionMs = 0L)
        }
    }
}

internal fun formatPlaybackTime(positionMs: Long): String {
    val totalSeconds = positionMs.coerceAtLeast(0L) / 1000L
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
