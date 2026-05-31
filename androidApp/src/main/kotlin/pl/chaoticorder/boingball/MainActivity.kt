package pl.chaoticorder.boingball

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.chaoticorder.boingball.NavigationRoot
import pl.chaoticorder.boingball.ui.theme.BoingBallDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoingBallDemoTheme {
                NavigationRoot()
            }
        }
    }
}