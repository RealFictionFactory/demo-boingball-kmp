package com.rff.boingballdemo.main

sealed interface BoingBallAction {
    data object Preferences : BoingBallAction
    data object About : BoingBallAction
    data object Clock : BoingBallAction
    data object CopperBars : BoingBallAction
    data object Calculator : BoingBallAction
    data object Shell : BoingBallAction
    data object Back : BoingBallAction
}
