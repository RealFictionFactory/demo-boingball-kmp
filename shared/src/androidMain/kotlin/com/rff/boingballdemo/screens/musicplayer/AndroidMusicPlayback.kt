package com.rff.boingballdemo.screens.musicplayer

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import boingball.shared.generated.resources.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Controls the ExoPlayer hosted in [PlaybackService] through a MediaController.
 * Audio focus, "becoming noisy" and the media notification are handled by Media3.
 *
 * Commands issued before the controller connects are queued and replayed.
 * All calls and callbacks happen on the main thread.
 */
class AndroidMusicPlayback(
    context: Context,
) : MusicPlayback {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val _status = MutableStateFlow(PlaybackStatus())
    override val status: StateFlow<PlaybackStatus> = _status.asStateFlow()

    private var playlist: List<MusicTrack> = emptyList()
    private var controller: MediaController? = null
    private val pendingCommands = mutableListOf<(MediaController) -> Unit>()
    private var released = false

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            publishStatus()
        }
    }

    private val controllerFuture = MediaController.Builder(
        appContext,
        SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java)),
    ).buildAsync()

    init {
        controllerFuture.addListener({ onControllerReady() }, mainHandler::post)
    }

    override fun currentPositionMs(): Long =
        controller?.currentPosition ?: _status.value.positionMs

    override fun setPlaylist(tracks: List<MusicTrack>) {
        playlist = tracks
        _status.value = PlaybackStatus(tracks = tracks)
        withController { controller ->
            controller.setMediaItems(tracks.map(::toMediaItem), /* startIndex = */ 0, /* startPositionMs = */ 0L)
            controller.prepare()
        }
    }

    override fun play() = withController { controller ->
        if (controller.playbackState == Player.STATE_IDLE) controller.prepare()
        controller.play()
    }

    override fun pause() = withController { it.pause() }

    override fun stop() = withController { controller ->
        controller.pause()
        controller.seekTo(0L)
    }

    override fun seekTo(positionMs: Long) = withController { it.seekTo(positionMs.coerceAtLeast(0L)) }

    override fun skipToTrack(index: Int) = withController { controller ->
        if (index in 0 until controller.mediaItemCount) controller.seekTo(index, 0L)
    }

    override fun skipToNext() = withController { it.seekToNextMediaItem() }

    override fun skipToPrevious() = withController { it.seekToPreviousMediaItem() }

    override fun moveTrack(from: Int, to: Int) {
        if (from !in playlist.indices || to !in playlist.indices) return
        playlist = playlist.moved(from, to)
        // Publish right away so the list and the highlighted track never disagree.
        _status.value = _status.value.copy(
            tracks = playlist,
            currentIndex = indexAfterMove(_status.value.currentIndex, from, to),
        )
        withController { it.moveMediaItem(from, to) }
    }

    override fun release() {
        if (released) return
        released = true
        pendingCommands.clear()
        controller?.run {
            removeListener(playerListener)
            // Emptying the player lets PlaybackService stop itself.
            stop()
            clearMediaItems()
        }
        controller = null
        MediaController.releaseFuture(controllerFuture)
    }

    private fun onControllerReady() {
        if (released) return
        val connected = try {
            controllerFuture.get()
        } catch (error: Exception) {
            Log.e(TAG, "Could not connect to PlaybackService", error)
            return
        }
        connected.addListener(playerListener)
        controller = connected
        pendingCommands.forEach { it(connected) }
        pendingCommands.clear()
        publishStatus()
    }

    private fun withController(command: (MediaController) -> Unit) {
        if (released) return
        val current = controller
        if (current != null) command(current) else pendingCommands += command
    }

    private fun publishStatus() {
        val current = controller ?: return
        _status.value = PlaybackStatus(
            tracks = playlist,
            // playWhenReady reflects intent, so the button does not flicker while buffering.
            // A transient focus loss (e.g. a call) suppresses playback: show it as paused,
            // like iOS does during an interruption. Media3 resumes it afterwards.
            isPlaying = current.playWhenReady &&
                current.playbackSuppressionReason == Player.PLAYBACK_SUPPRESSION_REASON_NONE,
            currentIndex = current.currentMediaItemIndex,
            positionMs = current.currentPosition,
        )
    }

    private fun toMediaItem(track: MusicTrack): MediaItem =
        MediaItem.Builder()
            .setMediaId(track.resourcePath)
            // file:///android_asset/... is read through ExoPlayer's asset data source.
            .setUri(Res.getUri(track.resourcePath))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.composer)
                    .setDurationMs(track.durationMs)
                    .build(),
            )
            .build()

    private companion object {
        const val TAG = "AndroidMusicPlayback"
    }
}
