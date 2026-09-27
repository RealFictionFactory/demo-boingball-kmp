package com.rff.boingballdemo.screens.musicplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.data.local.AppSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

/**
 * Mirrors [MusicPlayback], which owns the playlist and play state so system media
 * controls and this screen always agree. The ViewModel only translates UI actions.
 */
class MusicPlayerViewModel(
    settings: AppSettings,
    private val playback: MusicPlayback,
) : ViewModel() {

    // Polls the live position only while playing and while the UI is subscribed.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val positionMs: Flow<Long> = playback.status.flatMapLatest { status ->
        if (!status.isPlaying) {
            flowOf(status.positionMs)
        } else {
            flow {
                while (true) {
                    emit(playback.currentPositionMs())
                    delay(PROGRESS_TICK_MS)
                }
            }
        }
    }

    val uiState: StateFlow<MusicPlayerState> = combine(
        settings.boingBallPrefs.map { it.osStyle }.distinctUntilChanged(),
        playback.status,
        positionMs,
    ) { osStyle, status, position ->
        MusicPlayerState(
            osStyle = osStyle,
            tracks = status.tracks,
            currentTrackIndex = status.currentIndex,
            isPlaying = status.isPlaying,
            positionMs = position,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MusicPlayerState(tracks = AMIGA_MUSIC_TRACKS),
    )

    init {
        playback.setPlaylist(AMIGA_MUSIC_TRACKS)
    }

    fun onAction(action: MusicPlayerAction) {
        val tracks = playback.status.value.tracks
        when (action) {
            MusicPlayerAction.Play -> playback.play()
            MusicPlayerAction.Pause -> playback.pause()
            MusicPlayerAction.Stop -> playback.stop()
            MusicPlayerAction.Next -> playback.skipToNext()
            MusicPlayerAction.Previous -> playback.skipToPrevious()
            is MusicPlayerAction.Seek -> {
                val duration = tracks.getOrNull(playback.status.value.currentIndex)?.durationMs ?: return
                playback.seekTo(seekTarget(playback.currentPositionMs(), action.deltaMs, duration))
            }
            is MusicPlayerAction.SelectTrack -> {
                if (action.index in tracks.indices) playback.skipToTrack(action.index)
            }
            is MusicPlayerAction.MoveTrack -> {
                val target = moveTarget(tracks, action.index, action.direction) ?: return
                playback.moveTrack(action.index, target)
            }
        }
    }

    override fun onCleared() {
        // Closing the player window stops the music.
        playback.release()
    }
}

internal fun seekTarget(positionMs: Long, deltaMs: Long, durationMs: Long): Long =
    (positionMs + deltaMs).coerceIn(0L, durationMs.coerceAtLeast(0L))

/** Target index for moving a track one step up (-1) or down (+1), or null if not possible. */
internal fun moveTarget(tracks: List<MusicTrack>, index: Int, direction: Int): Int? {
    if (direction != -1 && direction != 1) return null
    val target = index + direction
    return if (index in tracks.indices && target in tracks.indices) target else null
}

/** Moves the element at [from] to [to], shifting the elements in between. */
internal fun <T> List<T>.moved(from: Int, to: Int): List<T> =
    toMutableList().apply { add(to, removeAt(from)) }

/** Index of the current element after [moved] with the same [from] and [to]. */
internal fun indexAfterMove(current: Int, from: Int, to: Int): Int = when {
    current == from -> to
    from < to && current in (from + 1)..to -> current - 1
    to < from && current in to until from -> current + 1
    else -> current
}

internal fun formatPlaybackTime(positionMs: Long): String {
    val totalSeconds = positionMs.coerceAtLeast(0L) / 1000L
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
