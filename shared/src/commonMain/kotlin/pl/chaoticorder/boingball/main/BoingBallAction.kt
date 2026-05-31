package pl.chaoticorder.boingball.main

sealed interface BoingBallAction {
    data object Preferences : BoingBallAction
}
