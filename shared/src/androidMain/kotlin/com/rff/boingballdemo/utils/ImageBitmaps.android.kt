package com.rff.boingballdemo.utils

import androidx.compose.ui.graphics.ImageBitmap

// Android draws the backing Bitmap directly, with no per-draw copy.
internal actual fun ImageBitmap.markImmutable() = Unit
