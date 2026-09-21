package com.rff.boingballdemo.musicplayer

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
    MusicTrack("Shadow of the Beast", "David Whittaker", 224_000L),
    MusicTrack("Turrican II", "Chris Huelsbeck", 198_000L),
    MusicTrack("Lotus III", "Barry Leitch", 186_000L),
    MusicTrack("Lemmings", "Tim Wright", 172_000L),
    MusicTrack("Cannon Fodder", "Richard Joseph", 241_000L),
    MusicTrack("IK+", "Rob Hubbard", 165_000L),
    MusicTrack("Xenon 2", "David Whittaker", 203_000L),
    MusicTrack("The Chaos Engine", "Richard Joseph", 190_000L),
)

class MusicPlayerViewModel(
    private val settings: AppSettings,
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
        val wasPlaying = _uiState.value.isPlaying
        _uiState.update { it.reduce(action) }
        val playing = _uiState.value.isPlaying
        when {
            playing && !wasPlaying -> startProgress()
            !playing && wasPlaying -> stopProgress()
        }
    }

    private fun startProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                delay(PROGRESS_TICK_MS)
                val state = _uiState.value
                if (!state.isPlaying) continue
                val duration = state.currentTrack?.durationMs ?: continue
                val nextPosition = state.positionMs + PROGRESS_TICK_MS
                if (nextPosition >= duration) {
                    _uiState.update { it.reduce(MusicPlayerAction.Next) }
                } else {
                    _uiState.update { it.copy(positionMs = nextPosition) }
                }
            }
        }
    }

    private fun stopProgress() {
        progressJob?.cancel()
        progressJob = null
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
