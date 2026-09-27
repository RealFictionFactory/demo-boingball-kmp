package com.rff.boingballdemo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.topaz_a1200
import boingball.shared.generated.resources.topaz_a500
import org.jetbrains.compose.resources.Font

// Font() from compose resources is @Composable and must be called inside a composable.
@Composable
fun topazFont() = FontFamily(
    Font(Res.font.topaz_a500, FontWeight.Normal)
)

@Composable
fun topazFont20() = FontFamily(
    Font(Res.font.topaz_a1200)
)

/** Text colors are not part of the styles: they come from LocalContentColor or the call site. */
@Composable
fun toolbarTextStyle() = TextStyle(
    fontFamily = topazFont20(),
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)

@Composable
fun appTypography(): Typography {
    val topaz = topazFont()
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = topaz,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = topaz,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.3.sp
        ),
        bodySmall = TextStyle(
            fontFamily = topaz,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.1.sp
        ),
        labelLarge = TextStyle(
            fontFamily = topaz,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
    )
}
