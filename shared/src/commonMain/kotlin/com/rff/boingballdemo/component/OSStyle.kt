package com.rff.boingballdemo.component

import androidx.compose.runtime.staticCompositionLocalOf
import com.rff.boingballdemo.data.local.BoingBallPrefs

enum class OSStyle { AmigaOS13, AmigaOS20 }

/**
 * The Workbench look (OS 1.3 or 2.0+) for everything below it. Provided once by
 * [com.rff.boingballdemo.navigation.NavigationRoot] from the stored preferences, so
 * screens and components read it here instead of each ViewModel re-publishing it.
 * Static: it changes rarely, and then the whole UI restyles anyway.
 */
val LocalOsStyle = staticCompositionLocalOf { BoingBallPrefs.Default.osStyle }
