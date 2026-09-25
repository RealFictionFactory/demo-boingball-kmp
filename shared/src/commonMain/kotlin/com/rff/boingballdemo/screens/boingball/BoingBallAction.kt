package com.rff.boingballdemo.screens.boingball

sealed interface BoingBallAction {
    data object Dismiss : BoingBallAction
}
