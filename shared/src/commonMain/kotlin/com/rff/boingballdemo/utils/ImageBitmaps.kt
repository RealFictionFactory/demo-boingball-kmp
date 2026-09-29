package com.rff.boingballdemo.utils

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Promises that [this] image will not be drawn into again, so it can be drawn many times
 * per frame cheaply. On iOS every draw of a mutable image copies all its pixels into a new
 * Skia image (and a new GPU texture); an immutable one is shared instead.
 */
internal expect fun ImageBitmap.markImmutable()
