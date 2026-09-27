package com.rff.boingballdemo

import androidx.compose.ui.window.ComposeUIViewController
import com.rff.boingballdemo.di.initKoin
import com.rff.boingballdemo.navigation.NavigationRoot
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme

// iOS apps must never quit themselves (Human Interface Guidelines), so no
// onExitApp is passed: back on the Workbench is a no-op.
fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    BoingBallDemoTheme {
        NavigationRoot()
    }
}
