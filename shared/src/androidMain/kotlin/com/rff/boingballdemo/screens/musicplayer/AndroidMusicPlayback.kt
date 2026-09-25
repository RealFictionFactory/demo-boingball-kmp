package com.rff.boingballdemo.screens.musicplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import boingball.shared.generated.resources.Res

class AndroidMusicPlayback(
    context: Context,
) : MusicPlayback {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val player = MediaPlayer()
    private val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    private var focusRequest: AudioFocusRequest? = null
    private var requestId = 0
    private var prepared = false
    private var wantStart = false
    private var acceptCompletion = false
    private var ended = false
    private var loadedPath: String? = null
    private var pendingPositionMs = 0L

    init {
        player.setOnCompletionListener {
            if (acceptCompletion) ended = true
        }
        player.setOnErrorListener { _, what, extra ->
            Log.e(TAG, "Music playback error what=$what extra=$extra")
            prepared = false
            acceptCompletion = false
            true
        }
    }

    override fun play(resourcePath: String, positionMs: Long) {
        ended = false
        pendingPositionMs = positionMs.coerceAtLeast(0L)
        if (prepared && loadedPath == resourcePath) {
            try {
                seekPrepared(pendingPositionMs)
                startPrepared()
                return
            } catch (error: IllegalStateException) {
                Log.w(TAG, "Restart failed, reloading $resourcePath", error)
                prepared = false
            }
        }
        val request = ++requestId
        prepared = false
        wantStart = true
        acceptCompletion = false
        loadedPath = resourcePath
        try {
            player.reset()
            player.setAudioAttributes(attributes)
            // Res.getUri() is file:///android_asset/..., not a filesystem path.
            val assetPath = Res.getUri(resourcePath).removePrefix("file:///android_asset/")
            appContext.assets.openFd(assetPath).use { descriptor ->
                player.setDataSource(
                    descriptor.fileDescriptor,
                    descriptor.startOffset,
                    descriptor.length,
                )
            }
            player.setOnPreparedListener { ready ->
                if (request != requestId) return@setOnPreparedListener
                prepared = true
                seekPrepared(pendingPositionMs)
                if (wantStart) startPrepared()
            }
            player.prepareAsync()
        } catch (error: Exception) {
            prepared = false
            Log.e(TAG, "Failed to play $resourcePath", error)
        }
    }

    override fun pause() {
        wantStart = false
        acceptCompletion = false
        try {
            if (prepared && player.isPlaying) player.pause()
        } catch (error: IllegalStateException) {
            Log.w(TAG, "Pause ignored", error)
        }
        abandonFocus()
    }

    override fun stop() {
        wantStart = false
        acceptCompletion = false
        ended = false
        prepared = false
        loadedPath = null
        pendingPositionMs = 0L
        requestId++
        player.reset()
        abandonFocus()
    }

    override fun seekTo(positionMs: Long) {
        pendingPositionMs = positionMs.coerceAtLeast(0L)
        ended = false
        if (!prepared) return
        try {
            seekPrepared(pendingPositionMs)
        } catch (error: IllegalStateException) {
            Log.w(TAG, "Seek ignored", error)
        }
    }

    override fun currentPositionMs(): Long {
        if (!prepared) return pendingPositionMs
        return try {
            player.currentPosition.toLong()
        } catch (error: IllegalStateException) {
            pendingPositionMs
        }
    }

    override fun hasEnded(): Boolean = ended

    override fun release() {
        stop()
        player.release()
    }

    private fun seekPrepared(positionMs: Long) {
        player.seekTo(positionMs.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
    }

    private fun startPrepared() {
        requestFocus()
        player.start()
        acceptCompletion = true
    }

    private fun requestFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = focusRequest ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attributes)
                .build()
                .also { focusRequest = it }
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN,
            )
        }
    }

    private fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    private companion object {
        const val TAG = "AndroidMusicPlayback"
    }
}
