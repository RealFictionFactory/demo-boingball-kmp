package com.rff.boingballdemo.calculator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculatorEngineTest {

    private fun CalculatorState.press(vararg actions: CalculatorAction): CalculatorState =
        actions.fold(this) { state, action -> state.reduce(action) }

    @Test
    fun typesDigitsIntoDisplay() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(1),
            CalculatorAction.Digit(2),
            CalculatorAction.Digit(3),
        )

        assertEquals("123", state.display)
    }

    @Test
    fun addsTwoNumbers() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(2),
            CalculatorAction.Operator(CalculatorOperator.Add),
            CalculatorAction.Digit(3),
            CalculatorAction.Equals,
        )

        assertEquals("5", state.display)
    }

    @Test
    fun chainsOperationsWithoutEquals() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(2),
            CalculatorAction.Operator(CalculatorOperator.Add),
            CalculatorAction.Digit(3),
            CalculatorAction.Operator(CalculatorOperator.Multiply),
            CalculatorAction.Digit(4),
            CalculatorAction.Equals,
        )

        assertEquals("20", state.display)
    }

    @Test
    fun keepsSingleDecimalSeparator() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(1),
            CalculatorAction.Decimal,
            CalculatorAction.Decimal,
            CalculatorAction.Digit(5),
        )

        assertEquals("1.5", state.display)
    }

    @Test
    fun showsErrorOnDivisionByZero() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(8),
            CalculatorAction.Operator(CalculatorOperator.Divide),
            CalculatorAction.Digit(0),
            CalculatorAction.Equals,
        )

        assertTrue(state.isError)
        assertEquals(CALCULATOR_ERROR_TEXT, state.display)
    }

    @Test
    fun clearsErrorWhenTypingAgain() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(8),
            CalculatorAction.Operator(CalculatorOperator.Divide),
            CalculatorAction.Digit(0),
            CalculatorAction.Equals,
            CalculatorAction.Digit(7),
        )

        assertEquals("7", state.display)
        assertTrue(!state.isError)
    }

    @Test
    fun togglesSign() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(4),
            CalculatorAction.ToggleSign,
        )

        assertEquals("-4", state.display)
    }

    @Test
    fun clearEntryKeepsPendingOperation() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(9),
            CalculatorAction.Operator(CalculatorOperator.Add),
            CalculatorAction.Digit(5),
            CalculatorAction.ClearEntry,
            CalculatorAction.Digit(1),
            CalculatorAction.Equals,
        )

        assertEquals("10", state.display)
    }

    @Test
    fun percentUsesAccumulatorWhenOperationPending() {
        val state = CalculatorState().press(
            CalculatorAction.Digit(2),
            CalculatorAction.Digit(0),
            CalculatorAction.Digit(0),
            CalculatorAction.Operator(CalculatorOperator.Subtract),
            CalculatorAction.Digit(1),
            CalculatorAction.Digit(0),
            CalculatorAction.Percent,
            CalculatorAction.Equals,
        )

        assertEquals("180", state.display)
    }

    @Test
    fun formatsWholeResultsWithoutFraction() {
        assertEquals("5", 5.0.toDisplayText())
        assertEquals("-5", (-5.0).toDisplayText())
        assertEquals("0.5", 0.5.toDisplayText())
    }
}
