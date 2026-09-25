package com.rff.boingballdemo.screens.calculator

sealed interface CalculatorAction {
    data class Digit(val digit: Int) : CalculatorAction
    data object Decimal : CalculatorAction
    data class Operator(val operator: CalculatorOperator) : CalculatorAction
    data object Equals : CalculatorAction
    data object Clear : CalculatorAction
    data object ClearEntry : CalculatorAction
    data object ToggleSign : CalculatorAction
    data object Percent : CalculatorAction
}

enum class CalculatorOperator(val symbol: String) {
    Add("+"),
    Subtract("-"),
    Multiply("*"),
    Divide("/"),
}
