package com.rff.boingballdemo.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.rff.boingballdemo.component.LocalOsStyle
import com.rff.boingballdemo.component.OSStyle

/**
 * The app draws its own fixed Amiga palette, so the Material scheme only backs the few
 * Material defaults still in play (e.g. ripples). No dark mode and no dynamic color: the
 * Workbench looks the same on every device, as it did on the Amiga.
 */
private val AmigaColorScheme = lightColorScheme(
    primary = amigaOs13Blue,
    onPrimary = whiteColor,
    secondary = amigaOs13Orange,
    onSecondary = blackColor,
    background = backgroundColor,
    onBackground = blackColor,
    surface = backgroundColor,
    onSurface = blackColor,
)

@Composable
fun BoingBallDemoTheme(
    // Screens and previews inherit the Workbench look from here.
    osStyle: OSStyle = LocalOsStyle.current,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AmigaColorScheme,
        typography = appTypography(),
    ) {
        ProvideOsStyle(osStyle, content)
    }
}

/**
 * Provides [osStyle] and its default content color (text and icons): white on the
 * OS 1.3 blue Workbench, black on the OS 2.0+ grey one. Use this wherever the style
 * changes, so the two never disagree.
 */
@Composable
fun ProvideOsStyle(osStyle: OSStyle, content: @Composable () -> Unit) {
    val contentColor = if (osStyle == OSStyle.AmigaOS13) whiteColor else blackColor
    CompositionLocalProvider(
        LocalOsStyle provides osStyle,
        LocalContentColor provides contentColor,
        content = content,
    )
}
