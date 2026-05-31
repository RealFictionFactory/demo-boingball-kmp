package pl.chaoticorder.boingball.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.SoundPool
import android.util.Log
import boingball.shared.generated.resources.Res

actual class BoingBallAudioPlayer(
    private val context: Context
) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2).build()
    private var soundId: Int = 0
    private var isSoundLoaded: Boolean = false
    private val bounce = Res.getUri(BOUNCE_AUDIO_FILE_PATH)

    init {
        // Set up load complete listener
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                // Success
                isSoundLoaded = true
                Log.d(TAG, "Sound loaded successfully: $sampleId")
            } else {
                // Failed to load
                Log.e(TAG, "Failed to load sound. Status: $status")
            }
        }

        // Load the sound from Android assets using AssetFileDescriptor.
        // Res.getUri() returns a URI like "file:///android_asset/...", which is not
        // a real filesystem path. SoundPool.load(String) requires a filesystem path,
        // so we extract the asset path and use SoundPool.load(AssetFileDescriptor).
        val assetPath = bounce.removePrefix("file:///android_asset/")
        Log.d(TAG, "Loading sound from asset path: $assetPath")
        try {
            val afd: AssetFileDescriptor = context.assets.openFd(assetPath)
            soundId = soundPool.load(afd, 1)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open asset for sound: $assetPath", e)
        }

        if (soundId == 0) {
            Log.e(TAG, "Failed to initiate sound loading. SoundId is 0.")
        }
    }

    actual fun play() {
        if (!canPlaySound()) return

        soundPool.play(
            soundId,
            1f,
            1f,
            0,
            0,
            1f,
        )
    }

    actual fun playLeft() {
        if (!canPlaySound()) return

        soundPool.play(
            soundId,
            1f,
            0f,
            0,
            0,
            1f,
        )
    }

    actual fun playRight() {
        if (!canPlaySound()) return

        soundPool.play(
            soundId,
            0f,
            1f,
            0,
            0,
            1f,
        )
    }

    actual fun release() {
        soundPool.release()
    }

    private fun canPlaySound(): Boolean {
        if (soundId == 0) {
            Log.w(TAG, "Cannot play sound: soundId is 0 (load failed)")
            return false
        }
        if (!isSoundLoaded) {
            Log.w(TAG, "Cannot play sound: sound is still loading")
            return false
        }
        return true
    }

    companion object {
        private val TAG: String = BoingBallAudioPlayer::class.java.simpleName
    }
}
