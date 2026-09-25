package com.rff.boingballdemo.screens.musicplayer

import com.rff.boingballdemo.screens.musicplayer.MusicPlayerAction
import com.rff.boingballdemo.screens.musicplayer.MusicPlayerState
import com.rff.boingballdemo.screens.musicplayer.MusicTrack
import com.rff.boingballdemo.screens.musicplayer.PlaybackCommand
import com.rff.boingballdemo.screens.musicplayer.formatPlaybackTime
import com.rff.boingballdemo.screens.musicplayer.reduce
import com.rff.boingballdemo.screens.musicplayer.step
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MusicPlayerStateTest {

    private val tracks = listOf(
        MusicTrack("A", "One", 1_000L, "files/a.mp3"),
        MusicTrack("B", "Two", 2_000L, "files/b.mp3"),
    )
    private val state = MusicPlayerState(tracks = tracks)

    @Test
    fun playStartsPlayback() {
        val next = state.reduce(MusicPlayerAction.Play)
        assertTrue(next.isPlaying)
        assertEquals(0L, next.positionMs)
    }

    @Test
    fun stopClearsPosition() {
        val next = state.copy(isPlaying = true, positionMs = 500L)
            .reduce(MusicPlayerAction.Stop)
        assertFalse(next.isPlaying)
        assertEquals(0L, next.positionMs)
    }

    @Test
    fun nextWrapsAroundAndResetsPosition() {
        val next = state.copy(currentTrackIndex = 1, positionMs = 400L)
            .reduce(MusicPlayerAction.Next)
        assertEquals(0, next.currentTrackIndex)
        assertEquals(0L, next.positionMs)
    }

    @Test
    fun previousWrapsAround() {
        val next = state.reduce(MusicPlayerAction.Previous)
        assertEquals(1, next.currentTrackIndex)
    }

    @Test
    fun selectTrackIgnoresOutOfRangeIndex() {
        val next = state.reduce(MusicPlayerAction.SelectTrack(9))
        assertEquals(state, next)
    }

    @Test
    fun progressUsesTrackDuration() {
        val playing = state.copy(positionMs = 500L)
        assertEquals(0.5f, playing.progress)
    }

    @Test
    fun seekClampsToTrackDuration() {
        val next = state.copy(positionMs = 800L)
            .reduce(MusicPlayerAction.Seek(500L))
        assertEquals(1_000L, next.positionMs)
    }

    @Test
    fun playCommandsCurrentFile() {
        val step = state.step(MusicPlayerAction.Play)
        assertEquals(PlaybackCommand.Play("files/a.mp3", 0L), step.command)
        assertTrue(step.state.isPlaying)
    }

    @Test
    fun pauseAndStopCommandPlayback() {
        val playing = state.step(MusicPlayerAction.Play).state
        assertEquals(PlaybackCommand.Pause, playing.step(MusicPlayerAction.Pause).command)
        assertEquals(PlaybackCommand.Stop, playing.step(MusicPlayerAction.Stop).command)
    }

    @Test
    fun nextWhilePlayingCommandsNextFile() {
        val playing = state.step(MusicPlayerAction.Play).state
        val step = playing.step(MusicPlayerAction.Next)
        assertEquals(PlaybackCommand.Play("files/b.mp3", 0L), step.command)
        assertEquals(0L, step.state.positionMs)
    }

    @Test
    fun nextWhilePausedStopsPlayback() {
        val step = state.step(MusicPlayerAction.Next)
        assertEquals(PlaybackCommand.Stop, step.command)
        assertEquals(1, step.state.currentTrackIndex)
    }

    @Test
    fun seekCommandsClampedPosition() {
        val step = state.copy(positionMs = 800L).step(MusicPlayerAction.Seek(500L))
        assertEquals(PlaybackCommand.Seek(1_000L), step.command)
    }

    @Test
    fun selectTrackOutOfRangeDoesNotCommandPlayback() {
        val step = state.step(MusicPlayerAction.SelectTrack(9))
        assertEquals(null, step.command)
        assertEquals(state, step.state)
    }

    @Test
    fun formatPlaybackTimePadsSeconds() {
        assertEquals("0:00", formatPlaybackTime(0L))
        assertEquals("1:12", formatPlaybackTime(72_000L))
        assertEquals("3:44", formatPlaybackTime(224_000L))
    }
}
