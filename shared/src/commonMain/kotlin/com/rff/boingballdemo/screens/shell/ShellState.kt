package com.rff.boingballdemo.screens.shell

import com.rff.boingballdemo.component.OSStyle

data class ShellState(
    val osStyle: OSStyle = OSStyle.AmigaOS13,
    /** Lines already printed and scrolled up. */
    val lines: List<String> = emptyList(),
    /** Line currently being "typed" (prompt + partial command). */
    val currentLine: String = "",
    val isBusy: Boolean = false,
)
