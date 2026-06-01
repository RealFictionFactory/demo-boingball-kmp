package com.rff.boingballdemo

import androidx.compose.ui.window.ComposeUIViewController
import com.rff.boingballdemo.di.initKoin
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.Foundation.NSSelectorFromString

@OptIn(ExperimentalForeignApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    BoingBallDemoTheme {
        NavigationRoot(
            onExitApp = {
                UIApplication.sharedApplication.performSelector(
                    NSSelectorFromString("suspend")
                )
            }
        )
    }
}