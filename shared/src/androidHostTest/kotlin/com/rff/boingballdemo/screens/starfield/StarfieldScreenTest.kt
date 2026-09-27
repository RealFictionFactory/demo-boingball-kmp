package com.rff.boingballdemo.screens.starfield

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class StarfieldScreenTest {
    @Test
    fun showsStarfieldWindowInBothStyles() {
        for (style in OSStyle.entries) {
            runComposeUiTest {
                // The animation never settles, so drive the clock by hand instead of
                // waiting for Compose to become idle.
                mainClock.autoAdvance = false
                setContent { BoingBallDemoTheme(osStyle = style) { StarfieldScreen() } }
                // A few frames, so the animation loop runs.
                mainClock.advanceTimeBy(100)
                onNodeWithText("Starfield").assertIsDisplayed()
            }
        }
    }
}
