package com.rff.boingballdemo.main

sealed interface BoingBallAction {
    data object Preferences : BoingBallAction
}
