package com.rff.boingballdemo.component

import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.preferences_video_ntsc
import boingball.shared.generated.resources.preferences_video_pal
import org.jetbrains.compose.resources.StringResource

enum class VideoSystem(
    val vblankHz: Int,
    val labelRes: StringResource,
) {
    PAL(50, Res.string.preferences_video_pal),
    NTSC(60, Res.string.preferences_video_ntsc);

    companion object {
        fun fromLabel(label: String, labels: Map<VideoSystem, String>): VideoSystem? =
            entries.firstOrNull { labels[it] == label }
    }
}
