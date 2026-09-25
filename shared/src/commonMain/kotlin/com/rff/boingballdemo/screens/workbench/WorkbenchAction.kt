package com.rff.boingballdemo.screens.workbench

sealed interface WorkbenchAction {
    data object BoingBall : WorkbenchAction
    data object Preferences : WorkbenchAction
    data object About : WorkbenchAction
    data object Clock : WorkbenchAction
    data object CopperBars : WorkbenchAction
    data object Calculator : WorkbenchAction
    data object Shell : WorkbenchAction
    data object MusicPlayer : WorkbenchAction
}
