package com.rff.boingballdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rff.boingballdemo.screens.boingball.TVBoingBallScreen
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme

class TVActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            BoingBallDemoTheme {
                TVBoingBallScreen()
            }
        }
    }
}
