package pl.chaoticorder.boingball.audio

import boingball.shared.generated.resources.Res
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
actual class BoingBallAudioPlayer {
    private var avAudioPlayer: AVAudioPlayer? = null

    init {
        // Configure audio session for playback
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)

        // Load audio once and reuse
        val url = Res.getUri(BOUNCE_AUDIO_FILE_PATH).let { path ->
            NSURL.URLWithString(URLString = path)
        }
        avAudioPlayer = url?.let { AVAudioPlayer(it, error = null) }
        avAudioPlayer?.prepareToPlay()
    }

    actual fun play() {
        avAudioPlayer?.let {
            it.currentTime = 0.0
            it.pan = 0f
            it.play()
        }
    }

    actual fun playLeft() {
        avAudioPlayer?.let {
            it.currentTime = 0.0
            it.pan = -1f
            it.play()
        }
    }

    actual fun playRight() {
        avAudioPlayer?.let {
            it.currentTime = 0.0
            it.pan = 1f
            it.play()
        }
    }

    actual fun release() {
        avAudioPlayer?.stop()
        avAudioPlayer = null
    }
}
