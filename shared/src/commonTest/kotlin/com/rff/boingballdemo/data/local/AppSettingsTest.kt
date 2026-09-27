package com.rff.boingballdemo.data.local

import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.VideoSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class AppSettingsTest {
    @Test
    fun decodesStoredName() {
        assertEquals(OSStyle.AmigaOS20, decodeEnum("AmigaOS20", null, OSStyle.AmigaOS13))
        assertEquals(VideoSystem.NTSC, decodeEnum("NTSC", null, VideoSystem.PAL))
    }

    @Test
    fun nameTakesPrecedenceOverLegacyOrdinal() {
        assertEquals(OSStyle.AmigaOS13, decodeEnum("AmigaOS13", 1, OSStyle.AmigaOS20))
    }

    @Test
    fun fallsBackToLegacyOrdinal() {
        assertEquals(OSStyle.AmigaOS20, decodeEnum(null, 1, OSStyle.AmigaOS13))
    }

    @Test
    fun unknownNameUsesLegacyOrdinal() {
        assertEquals(VideoSystem.NTSC, decodeEnum("SECAM", 1, VideoSystem.PAL))
    }

    @Test
    fun outOfRangeOrdinalUsesDefault() {
        assertEquals(OSStyle.AmigaOS13, decodeEnum(null, 7, OSStyle.AmigaOS13))
        assertEquals(OSStyle.AmigaOS13, decodeEnum(null, -1, OSStyle.AmigaOS13))
    }

    @Test
    fun missingValuesUseDefault() {
        assertEquals(VideoSystem.PAL, decodeEnum<VideoSystem>(null, null, VideoSystem.PAL))
    }
}
