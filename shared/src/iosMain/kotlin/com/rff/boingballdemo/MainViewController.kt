package com.rff.boingballdemo

import androidx.compose.ui.window.ComposeUIViewController
import com.rff.boingballdemo.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    NavigationRoot()
}