package com.rff.boingballdemo.workbench

sealed interface BoingBallAction {
    data object BoingBall : BoingBallAction
    data object Preferences : BoingBallAction
    data object About : BoingBallAction
    data object Clock : BoingBallAction
    data object CopperBars : BoingBallAction
    data object Calculator : BoingBallAction
    data object Shell : BoingBallAction
    data object MusicPlayer : BoingBallAction
    data object Back : BoingBallAction
}
