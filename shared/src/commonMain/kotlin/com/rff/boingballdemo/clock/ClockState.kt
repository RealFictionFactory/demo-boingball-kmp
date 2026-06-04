package com.rff.boingballdemo.clock

import com.rff.boingballdemo.component.OSStyle

data class ClockState(
    val osStyle: OSStyle = OSStyle.AmigaOS13,
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val dateText: String = "",
)
