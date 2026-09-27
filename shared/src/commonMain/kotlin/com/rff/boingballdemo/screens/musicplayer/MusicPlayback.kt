package com.rff.boingballdemo.screens.musicplayer

import kotlinx.coroutines.flow.StateFlow

/** Discrete playback state. Changes on events only, not while the position advances. */
data class PlaybackStatus(
    val tracks: List<MusicTrack> = emptyList(),
    val isPlaying: Boolean = false,
    val currentIndex: Int = 0,
    /** Position when the status last changed; use [MusicPlayback.currentPositionMs] for live position. */
    val positionMs: Long = 0L,
)

/**
 * Plays a playlist and owns its state, so system controls (Android media notification,
 * iOS lock screen) and the in-app UI always agree. Skipping wraps around the playlist,
 * and playback advances to the next track when one ends.
 */
interface MusicPlayback {
    val status: StateFlow<PlaybackStatus>

    fun currentPositionMs(): Long

    /** Replaces the playlist and rewinds to the first track, paused. */
    fun setPlaylist(tracks: List<MusicTrack>)
    fun play()
    fun pause()

    /** Pauses and rewinds the current track. */
    fun stop()
    fun seekTo(positionMs: Long)

    /** Jumps to the start of track [index]; keeps playing if playback was active. */
    fun skipToTrack(index: Int)
    fun skipToNext()
    fun skipToPrevious()

    /** Moves the track at [from] to position [to], keeping the current track playing. */
    fun moveTrack(from: Int, to: Int)
    fun release()
}
