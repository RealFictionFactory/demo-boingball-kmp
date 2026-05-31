package com.rff.boingballdemo.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class Face(
    val path: Path,
    val depth: Float, // average z, for painter’s sort
    val color: Color
)
