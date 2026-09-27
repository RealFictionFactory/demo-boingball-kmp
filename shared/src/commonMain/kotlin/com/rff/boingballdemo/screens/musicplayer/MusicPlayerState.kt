package com.rff.boingballdemo.screens.musicplayer

data class MusicTrack(
    val title: String,
    val composer: String,
    val durationMs: Long,
    val resourcePath: String,
)

data class MusicPlayerState(
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

    /**
     * Index of the track identified by [resourcePath], or the current track if it is null
     * or no longer in the playlist. Identifying by track (not position) keeps a selection
     * on the same track however the playlist is reordered.
     */
    fun indexOfTrack(resourcePath: String?): Int =
        tracks.indexOfFirst { it.resourcePath == resourcePath }.takeIf { it >= 0 } ?: currentTrackIndex
}
