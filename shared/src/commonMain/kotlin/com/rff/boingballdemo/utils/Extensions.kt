package com.rff.boingballdemo.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.abs

internal fun Float.toRadians() = this * (PI / 180f).toFloat()

fun Offset.sameAs(o: Offset) = abs(x - o.x) < EPSILON && abs(y - o.y) < EPSILON
