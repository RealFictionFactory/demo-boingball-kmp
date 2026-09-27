package com.rff.boingballdemo.screens.shell

data class ShellState(
    /** Lines already printed and scrolled up. */
    val lines: List<String> = emptyList(),
    /** Line currently being "typed" (prompt + partial command). */
    val currentLine: String = "",
    val isBusy: Boolean = false,
)
