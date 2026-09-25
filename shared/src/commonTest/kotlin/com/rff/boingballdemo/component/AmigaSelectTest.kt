package com.rff.boingballdemo.component

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AmigaSelectTest {

    @Test
    fun selectingSameOptionDoesNotNotify() {
        var notified: String? = null
        notifyIfOptionChanged("PAL", "PAL") { notified = it }
        assertNull(notified)
    }

    @Test
    fun selectingDifferentOptionNotifies() {
        var notified: String? = null
        notifyIfOptionChanged("PAL", "NTSC") { notified = it }
        assertEquals("NTSC", notified)
    }
}
