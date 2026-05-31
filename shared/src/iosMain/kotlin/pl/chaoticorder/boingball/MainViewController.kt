package pl.chaoticorder.boingball

import androidx.compose.ui.window.ComposeUIViewController
import pl.chaoticorder.boingball.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    NavigationRoot()
}