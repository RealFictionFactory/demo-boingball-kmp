package com.rff.boingballdemo.screens.calculator

import com.rff.boingballdemo.component.OSStyle

const val CALCULATOR_ERROR_TEXT = "ERROR"

/** Maximum number of characters the Amiga-style display shows. */
const val CALCULATOR_MAX_DIGITS = 12

data class CalculatorState(
    val osStyle: OSStyle = OSStyle.AmigaOS13,
    val display: String = "0",
    val accumulator: Double? = null,
    val pendingOperator: CalculatorOperator? = null,
    val startNewEntry: Boolean = true,
    val isError: Boolean = false,
)
