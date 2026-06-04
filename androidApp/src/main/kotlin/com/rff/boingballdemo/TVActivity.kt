package com.rff.boingballdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rff.boingballdemo.main.TVBoingBallScreen
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme

class TVActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoingBallDemoTheme {
                TVBoingBallScreen()
            }
        }
    }
}
