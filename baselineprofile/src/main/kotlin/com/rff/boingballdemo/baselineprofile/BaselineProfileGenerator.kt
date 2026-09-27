package com.rff.boingballdemo.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Records the code paths of a typical session: cold start into the Boing Ball demo, the
 * Workbench, and the most used windows. Run with
 * `./gradlew :androidApp:generateBaselineProfile`; commit the files it writes to
 * `androidApp/src/release/generated/baselineProfiles/`.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = "com.rff.boingballdemo",
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()

        // The Boing Ball demo opens first; let it animate, then tap to reveal the Workbench.
        Thread.sleep(ANIMATION_MS)
        device.click(device.displayWidth / 2, device.displayHeight / 2)

        for (window in listOf("Clock", "MusicPlayer", "Preferences", "Calculator", "Shell", "CopperBars")) {
            val icon = device.wait(Until.findObject(By.res("workbench_$window")), TIMEOUT_MS)
                ?: error("Workbench icon for $window not found")
            icon.click()
            Thread.sleep(ANIMATION_MS)
            device.pressBack()
        }
    }

    private companion object {
        const val ANIMATION_MS = 3_000L
        const val TIMEOUT_MS = 5_000L
    }
}
