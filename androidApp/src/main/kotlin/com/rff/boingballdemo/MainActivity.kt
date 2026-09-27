package com.rff.boingballdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.navigation.NavigationRoot
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settings: AppSettings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep the splash until the stored preferences are read, then compose with them,
        // so the first frame already has the user's OS style (no OS 1.3 → 2.0 flash).
        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }
        lifecycleScope.launch {
            val prefs = settings.boingBallPrefs.first()
            setContent {
                BoingBallDemoTheme(osStyle = prefs.osStyle) {
                    NavigationRoot(
                        onExitApp = { finish() }
                    )
                }
            }
            isReady = true
        }
    }
}
