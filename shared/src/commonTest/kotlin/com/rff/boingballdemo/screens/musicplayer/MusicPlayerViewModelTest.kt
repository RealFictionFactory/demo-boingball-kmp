package com.rff.boingballdemo.screens.musicplayer

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.testing.InMemoryDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MusicPlayerViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val playback = FakeMusicPlayback()
    private val settings = AppSettings(InMemoryDataStore())

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsPlaylistOnCreation() {
        MusicPlayerViewModel(settings, playback)
        assertEquals(AMIGA_MUSIC_TRACKS, playback.status.value.tracks)
    }

    @Test
    fun transportActionsReachPlayback() {
        val viewModel = MusicPlayerViewModel(settings, playback)
        viewModel.onAction(MusicPlayerAction.Play)
        viewModel.onAction(MusicPlayerAction.Next)
        viewModel.onAction(MusicPlayerAction.Previous)
        viewModel.onAction(MusicPlayerAction.Pause)
        viewModel.onAction(MusicPlayerAction.Stop)
        assertEquals(listOf("play", "next", "previous", "pause", "stop"), playback.calls)
    }

    @Test
    fun seekIsClampedToTrackDuration() {
        val viewModel = MusicPlayerViewModel(settings, playback)
        val duration = AMIGA_MUSIC_TRACKS[0].durationMs
        playback.positionMs = duration - 1_000L
        viewModel.onAction(MusicPlayerAction.Seek(10_000L))
        assertEquals("seek:$duration", playback.calls.last())
    }

    @Test
    fun invalidSelectionAndMovesAreIgnored() {
        val viewModel = MusicPlayerViewModel(settings, playback)
        viewModel.onAction(MusicPlayerAction.SelectTrack(99))
        viewModel.onAction(MusicPlayerAction.MoveTrack(0, -1))
        viewModel.onAction(MusicPlayerAction.MoveTrack(0, 2))
        assertTrue(playback.calls.isEmpty())
    }

    @Test
    fun moveTrackReordersPlaylist() {
        val viewModel = MusicPlayerViewModel(settings, playback)
        viewModel.onAction(MusicPlayerAction.MoveTrack(0, 1))
        assertEquals("move:0->1", playback.calls.last())
        assertEquals(AMIGA_MUSIC_TRACKS[0], playback.status.value.tracks[1])
        assertEquals(1, playback.status.value.currentIndex)
    }

    @Test
    fun uiStateMirrorsPlaybackAndTracksLivePosition() = runTest(dispatcher) {
        val viewModel = MusicPlayerViewModel(settings, playback)
        val collector = launch { viewModel.uiState.collect {} }
        runCurrent()

        // A system control (e.g. the media notification) skips and starts playback.
        playback.skipToTrack(2)
        playback.play()
        playback.positionMs = 1_500L
        advanceTimeBy(250L)

        val state = viewModel.uiState.value
        assertEquals(2, state.currentTrackIndex)
        assertTrue(state.isPlaying)
        assertEquals(1_500L, state.positionMs)
        collector.cancel()
    }

    @Test
    fun clearingViewModelReleasesPlayback() {
        val store = ViewModelStore()
        ViewModelProvider.create(
            store,
            viewModelFactory { initializer { MusicPlayerViewModel(settings, playback) } },
        )[MusicPlayerViewModel::class]

        store.clear()

        assertTrue(playback.released)
    }
}

private class FakeMusicPlayback : MusicPlayback {
    private val _status = MutableStateFlow(PlaybackStatus())
    override val status: StateFlow<PlaybackStatus> = _status.asStateFlow()
    val calls = mutableListOf<String>()
    var positionMs = 0L
    var released = false

    override fun currentPositionMs(): Long = positionMs

    override fun setPlaylist(tracks: List<MusicTrack>) {
        _status.value = PlaybackStatus(tracks = tracks)
    }

    override fun play() {
        calls += "play"
        _status.value = _status.value.copy(isPlaying = true)
    }

    override fun pause() {
        calls += "pause"
        _status.value = _status.value.copy(isPlaying = false)
    }

    override fun stop() {
        calls += "stop"
        _status.value = _status.value.copy(isPlaying = false, positionMs = 0L)
    }

    override fun seekTo(positionMs: Long) {
        calls += "seek:$positionMs"
    }

    override fun skipToTrack(index: Int) {
        calls += "skip:$index"
        _status.value = _status.value.copy(currentIndex = index)
    }

    override fun skipToNext() {
        calls += "next"
    }

    override fun skipToPrevious() {
        calls += "previous"
    }

    override fun moveTrack(from: Int, to: Int) {
        calls += "move:$from->$to"
        val status = _status.value
        _status.value = status.copy(
            tracks = status.tracks.moved(from, to),
            currentIndex = indexAfterMove(status.currentIndex, from, to),
        )
    }

    override fun release() {
        released = true
    }
}
