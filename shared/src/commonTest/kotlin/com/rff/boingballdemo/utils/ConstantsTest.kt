package com.rff.boingballdemo.utils

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstantsTest {

    @Test
    fun tauIsTwoPi() {
        assertEquals((2f * PI).toFloat(), TAU, 1e-7f)
    }

    @Test
    fun epsilonIsSmall() {
        assertTrue(EPSILON > 0f)
        assertTrue(EPSILON < 1e-4f)
    }
}
