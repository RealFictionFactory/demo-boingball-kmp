package com.rff.boingballdemo.musicplayer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MusicPlayerStateTest {

    private val tracks = listOf(
        MusicTrack("A", "One", 1_000L),
        MusicTrack("B", "Two", 2_000L),
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
    fun formatPlaybackTimePadsSeconds() {
        assertEquals("0:00", formatPlaybackTime(0L))
        assertEquals("1:12", formatPlaybackTime(72_000L))
        assertEquals("3:44", formatPlaybackTime(224_000L))
    }
}
