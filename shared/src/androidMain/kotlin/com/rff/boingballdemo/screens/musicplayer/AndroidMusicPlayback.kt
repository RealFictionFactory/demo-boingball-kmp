package com.rff.boingballdemo.screens.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import boingball.shared.generated.resources.Res

class AndroidMusicPlayback(
    context: Context,
) : MusicPlayback {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mainHandler = Handler(Looper.getMainLooper())
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
    private var externalStateListener: ((Boolean) -> Unit)? = null
    // Set when a transient focus loss (e.g. a phone call) paused playback.
    private var resumeOnFocusGain = false
    private var noisyReceiverRegistered = false

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                player.setVolume(1f, 1f)
                if (resumeOnFocusGain) {
                    resumeOnFocusGain = false
                    resumeAfterTransientLoss()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (isActive()) {
                    resumeOnFocusGain = true
                    pausePlayer()
                    externalStateListener?.invoke(false)
                }
            }
            // Android O+ ducks automatically; this only fires on older releases.
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> player.setVolume(DUCK_VOLUME, DUCK_VOLUME)
            AudioManager.AUDIOFOCUS_LOSS -> {
                resumeOnFocusGain = false
                if (isActive()) {
                    pause()
                    externalStateListener?.invoke(false)
                }
            }
        }
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != AudioManager.ACTION_AUDIO_BECOMING_NOISY) return
            if (isActive()) {
                pause()
                externalStateListener?.invoke(false)
            }
        }
    }

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
        resumeOnFocusGain = false
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
        resumeOnFocusGain = false
        pausePlayer()
        abandonFocus()
    }

    override fun stop() {
        wantStart = false
        acceptCompletion = false
        ended = false
        prepared = false
        loadedPath = null
        pendingPositionMs = 0L
        resumeOnFocusGain = false
        requestId++
        player.reset()
        unregisterBecomingNoisy()
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

    override fun setOnExternalStateChange(listener: ((isPlaying: Boolean) -> Unit)?) {
        externalStateListener = listener
    }

    override fun release() {
        externalStateListener = null
        stop()
        player.release()
    }

    /** Playing, or about to start once preparation finishes. */
    private fun isActive(): Boolean = wantStart || (prepared && isPlayerPlaying())

    private fun isPlayerPlaying(): Boolean = try {
        player.isPlaying
    } catch (error: IllegalStateException) {
        false
    }

    /** Pauses without giving up audio focus, so a transient loss can still resume. */
    private fun pausePlayer() {
        wantStart = false
        acceptCompletion = false
        try {
            if (prepared && player.isPlaying) player.pause()
        } catch (error: IllegalStateException) {
            Log.w(TAG, "Pause ignored", error)
        }
        unregisterBecomingNoisy()
    }

    private fun resumeAfterTransientLoss() {
        if (loadedPath == null) return
        if (!prepared) {
            wantStart = true
            externalStateListener?.invoke(true)
            return
        }
        try {
            player.start()
            acceptCompletion = true
            registerBecomingNoisy()
            externalStateListener?.invoke(true)
        } catch (error: IllegalStateException) {
            Log.w(TAG, "Resume after focus gain failed", error)
        }
    }

    private fun seekPrepared(positionMs: Long) {
        player.seekTo(positionMs.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
    }

    private fun startPrepared() {
        if (!requestFocus()) {
            // Another app (e.g. an active call) holds focus; do not play over it.
            Log.i(TAG, "Audio focus denied, not starting playback")
            wantStart = false
            externalStateListener?.invoke(false)
            return
        }
        player.setVolume(1f, 1f)
        player.start()
        acceptCompletion = true
        registerBecomingNoisy()
    }

    private fun requestFocus(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = focusRequest ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attributes)
                .setOnAudioFocusChangeListener(focusChangeListener, mainHandler)
                .build()
                .also { focusRequest = it }
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN,
            )
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusChangeListener)
        }
    }

    private fun registerBecomingNoisy() {
        if (noisyReceiverRegistered) return
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // System broadcasts still reach receivers that are not exported.
            appContext.registerReceiver(becomingNoisyReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            appContext.registerReceiver(becomingNoisyReceiver, filter)
        }
        noisyReceiverRegistered = true
    }

    private fun unregisterBecomingNoisy() {
        if (!noisyReceiverRegistered) return
        appContext.unregisterReceiver(becomingNoisyReceiver)
        noisyReceiverRegistered = false
    }

    private companion object {
        const val TAG = "AndroidMusicPlayback"
        const val DUCK_VOLUME = 0.2f
    }
}
