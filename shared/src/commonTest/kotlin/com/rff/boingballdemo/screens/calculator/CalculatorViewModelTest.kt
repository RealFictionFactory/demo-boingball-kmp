package com.rff.boingballdemo.screens.calculator

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorViewModelTest {
    @Test
    fun actionsUpdateTheDisplay() {
        val viewModel = CalculatorViewModel()
        listOf(
            CalculatorAction.Digit(1),
            CalculatorAction.Digit(2),
            CalculatorAction.Operator(CalculatorOperator.Add),
            CalculatorAction.Digit(3),
            CalculatorAction.Equals,
        ).forEach(viewModel::onAction)

        assertEquals("15", viewModel.uiState.value.display)
    }

    @Test
    fun clearResetsState() {
        val viewModel = CalculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(7))
        viewModel.onAction(CalculatorAction.Clear)

        assertEquals(CalculatorState(), viewModel.uiState.value)
    }
}
