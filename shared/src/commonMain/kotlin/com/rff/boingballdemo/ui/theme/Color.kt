package com.rff.boingballdemo.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val wireframeColor = Color(0xFFAA00AA)
val backgroundColor = Color(0xFFAAAAAA)
val whiteColor = Color(0xFFFFFFFF)
val blackColor = Color(0xFF000000)

val redColor = Color(0xFFFF0000)
val greenColor = Color(0xFF00CC00)
val amigaOs13Blue = Color(0xFF0057AF)
val amigaOs13Orange = Color(0xFFFF8800)

val amigaOs30Grey = Color(0XFFAAAAAA)
val amigaOs30Blue = Color(0XFF6688BB)

val DefaultAmigaOs13PickerColors = listOf(
    redColor,
    amigaOs13Blue,
    greenColor,
    blackColor
)

val AltAmigaOs13PickerColors = DefaultAmigaOs13PickerColors.subList(0, 3).plus(Color.White)
