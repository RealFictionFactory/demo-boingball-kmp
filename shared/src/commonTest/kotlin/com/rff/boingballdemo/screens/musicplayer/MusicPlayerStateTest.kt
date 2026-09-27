package com.rff.boingballdemo.screens.musicplayer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MusicPlayerStateTest {

    private val tracks = listOf(
        MusicTrack("A", "One", 1_000L, "files/a.mp3"),
        MusicTrack("B", "Two", 2_000L, "files/b.mp3"),
        MusicTrack("C", "Three", 3_000L, "files/c.mp3"),
    )

    @Test
    fun progressUsesTrackDuration() {
        val state = MusicPlayerState(tracks = tracks, positionMs = 500L)
        assertEquals(0.5f, state.progress)
    }

    @Test
    fun seekTargetClampsToTrackBounds() {
        assertEquals(1_000L, seekTarget(positionMs = 800L, deltaMs = 500L, durationMs = 1_000L))
        assertEquals(0L, seekTarget(positionMs = 300L, deltaMs = -500L, durationMs = 1_000L))
        assertEquals(700L, seekTarget(positionMs = 200L, deltaMs = 500L, durationMs = 1_000L))
    }

    @Test
    fun moveTargetAllowsOneStepInsidePlaylist() {
        assertEquals(1, moveTarget(tracks, index = 0, direction = 1))
        assertEquals(1, moveTarget(tracks, index = 2, direction = -1))
    }

    @Test
    fun moveTargetRejectsMovesOutsidePlaylist() {
        assertNull(moveTarget(tracks, index = 0, direction = -1))
        assertNull(moveTarget(tracks, index = 2, direction = 1))
        assertNull(moveTarget(tracks, index = 9, direction = -1))
        assertNull(moveTarget(tracks, index = 0, direction = 2))
        assertNull(moveTarget(tracks, index = 0, direction = 0))
    }

    @Test
    fun movedShiftsElementsInBetween() {
        assertEquals(listOf(tracks[1], tracks[0], tracks[2]), tracks.moved(0, 1))
        assertEquals(listOf(tracks[1], tracks[2], tracks[0]), tracks.moved(0, 2))
        assertEquals(listOf(tracks[2], tracks[0], tracks[1]), tracks.moved(2, 0))
    }

    @Test
    fun indexAfterMoveFollowsCurrentElement() {
        for (from in tracks.indices) {
            for (to in tracks.indices) {
                val reordered = tracks.moved(from, to)
                for (current in tracks.indices) {
                    assertEquals(
                        tracks[current],
                        reordered[indexAfterMove(current, from, to)],
                        "current=$current from=$from to=$to",
                    )
                }
            }
        }
    }

    @Test
    fun selectionFollowsTrackThroughReorder() {
        val state = MusicPlayerState(tracks = tracks, currentTrackIndex = 0)
        val selected = tracks[0].resourcePath
        assertEquals(0, state.indexOfTrack(selected))

        val reordered = state.copy(tracks = tracks.moved(0, 2))
        assertEquals(2, reordered.indexOfTrack(selected))
    }

    @Test
    fun missingSelectionFallsBackToCurrentTrack() {
        val state = MusicPlayerState(tracks = tracks, currentTrackIndex = 1)
        assertEquals(1, state.indexOfTrack(null))
        assertEquals(1, state.indexOfTrack("files/removed.mp3"))
    }

    @Test
    fun formatPlaybackTimePadsSeconds() {
        assertEquals("0:00", formatPlaybackTime(0L))
        assertEquals("1:12", formatPlaybackTime(72_000L))
        assertEquals("3:44", formatPlaybackTime(224_000L))
    }
}
