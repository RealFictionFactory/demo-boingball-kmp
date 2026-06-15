package com.rff.boingballdemo.about

import com.rff.boingballdemo.component.OSStyle

data class AboutState(
    val appName: String = "Boing Ball Demo",
    val appVersion: String = "",
    val osStyle: OSStyle = OSStyle.AmigaOS13,
)
