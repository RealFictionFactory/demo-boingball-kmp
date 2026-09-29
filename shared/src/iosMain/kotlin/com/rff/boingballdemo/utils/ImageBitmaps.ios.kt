package com.rff.boingballdemo.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap

internal actual fun ImageBitmap.markImmutable() {
    asSkiaBitmap().setImmutable()
}
