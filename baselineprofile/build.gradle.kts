plugins {
    alias(libs.plugins.androidTest)
    alias(libs.plugins.baselineprofile)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.rff.boingballdemo.baselineprofile"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        // Baseline profile collection needs API 28+.
        minSdk = 28
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":androidApp"
}

// Collects on a connected device, which must run Android 13+ (or be rooted). With several
// devices attached, pick one with ANDROID_SERIAL=<serial>.
baselineProfile {
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.testExt.junit)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.uiautomator)
}
