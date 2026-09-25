package com.rff.boingballdemo.screens.musicplayer

import com.rff.boingballdemo.component.OSStyle

data class MusicTrack(
    val title: String,
    val composer: String,
    val durationMs: Long,
    val resourcePath: String,
)

data class MusicPlayerState(
    val osStyle: OSStyle = OSStyle.AmigaOS13,
    val tracks: List<MusicTrack> = emptyList(),
    val currentTrackIndex: Int = 0,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
) {
    val currentTrack: MusicTrack? get() = tracks.getOrNull(currentTrackIndex)

    val progress: Float
        get() {
            val duration = currentTrack?.durationMs ?: return 0f
            if (duration <= 0L) return 0f
            return (positionMs.toFloat() / duration).coerceIn(0f, 1f)
        }
}
