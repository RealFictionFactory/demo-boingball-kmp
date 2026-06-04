package com.rff.boingballdemo.main

sealed interface BoingBallAction {
    data object Preferences : BoingBallAction
    data object Clock : BoingBallAction
    data object Back : BoingBallAction
}
