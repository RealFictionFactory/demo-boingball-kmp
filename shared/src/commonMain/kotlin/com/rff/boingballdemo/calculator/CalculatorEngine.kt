package com.rff.boingballdemo.calculator

import kotlin.math.abs
import kotlin.math.roundToLong
import kotlin.math.truncate

/**
 * Pure state machine of the calculator, kept out of the ViewModel so it can be unit tested.
 */
fun CalculatorState.reduce(action: CalculatorAction): CalculatorState = when (action) {
    is CalculatorAction.Digit -> appendDigit(action.digit)
    CalculatorAction.Decimal -> appendDecimal()
    is CalculatorAction.Operator -> applyOperator(action.operator)
    CalculatorAction.Equals -> applyEquals()
    CalculatorAction.Clear -> CalculatorState(osStyle = osStyle)
    CalculatorAction.ClearEntry -> clearEntry()
    CalculatorAction.ToggleSign -> toggleSign()
    CalculatorAction.Percent -> applyPercent()
}

private fun CalculatorState.appendDigit(digit: Int): CalculatorState {
    val base = if (isError) CalculatorState(osStyle = osStyle) else this
    val current = if (base.startNewEntry || base.display == "0") "" else base.display
    if (current.count { it.isDigit() } >= CALCULATOR_MAX_DIGITS) return base

    val negative = current.startsWith("-")
    val digits = if (negative) current.drop(1) else current
    val updated = (if (negative) "-" else "") + digits + digit

    return base.copy(display = updated, startNewEntry = false, isError = false)
}

private fun CalculatorState.appendDecimal(): CalculatorState {
    val base = if (isError) CalculatorState(osStyle = osStyle) else this
    return when {
        base.startNewEntry -> base.copy(display = "0.", startNewEntry = false, isError = false)
        base.display.contains('.') -> base
        else -> base.copy(display = base.display + ".")
    }
}

private fun CalculatorState.applyOperator(operator: CalculatorOperator): CalculatorState {
    if (isError) return this

    val entered = displayValue()
    // Chained operators without a new entry only swap the pending operation.
    if (pendingOperator != null && startNewEntry) {
        return copy(pendingOperator = operator)
    }

    val result = if (pendingOperator != null && accumulator != null) {
        compute(accumulator, entered, pendingOperator) ?: return errorState()
    } else {
        entered
    }

    return copy(
        display = result.toDisplayText(),
        accumulator = result,
        pendingOperator = operator,
        startNewEntry = true,
    )
}

private fun CalculatorState.applyEquals(): CalculatorState {
    if (isError) return this
    val operator = pendingOperator ?: return copy(startNewEntry = true)
    val accumulated = accumulator ?: return copy(startNewEntry = true)

    val result = compute(accumulated, displayValue(), operator) ?: return errorState()

    return copy(
        display = result.toDisplayText(),
        accumulator = null,
        pendingOperator = null,
        startNewEntry = true,
    )
}

private fun CalculatorState.clearEntry(): CalculatorState =
    copy(display = "0", startNewEntry = true, isError = false)

private fun CalculatorState.toggleSign(): CalculatorState {
    if (isError) return this
    val updated = when {
        display == "0" -> display
        display.startsWith("-") -> display.drop(1)
        else -> "-$display"
    }
    return copy(display = updated)
}

private fun CalculatorState.applyPercent(): CalculatorState {
    if (isError) return this
    // Amiga style: percentage of the accumulator when an operation is pending.
    val entered = displayValue()
    val result = if (accumulator != null && pendingOperator != null) {
        accumulator * entered / 100.0
    } else {
        entered / 100.0
    }
    return copy(display = result.toDisplayText(), startNewEntry = true)
}

private fun CalculatorState.errorState(): CalculatorState = copy(
    display = CALCULATOR_ERROR_TEXT,
    accumulator = null,
    pendingOperator = null,
    startNewEntry = true,
    isError = true,
)

private fun CalculatorState.displayValue(): Double = display.toDoubleOrNull() ?: 0.0

private fun compute(left: Double, right: Double, operator: CalculatorOperator): Double? {
    val result = when (operator) {
        CalculatorOperator.Add -> left + right
        CalculatorOperator.Subtract -> left - right
        CalculatorOperator.Multiply -> left * right
        CalculatorOperator.Divide -> if (right == 0.0) return null else left / right
    }
    return if (result.isFinite()) result else null
}

fun Double.toDisplayText(): String {
    if (!isFinite()) return CALCULATOR_ERROR_TEXT

    val whole = truncate(this)
    if (this == whole && abs(this) < 1e12) {
        return roundToLong().toString()
    }

    val text = toString()
    if (text.length <= CALCULATOR_MAX_DIGITS) return text

    // Too long for the display: keep the scientific form, otherwise cut the fraction.
    return if (text.contains('e') || text.contains('E')) {
        text
    } else {
        text.take(CALCULATOR_MAX_DIGITS).trimEnd('.')
    }
}
