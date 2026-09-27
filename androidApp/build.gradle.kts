import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.baselineprofile)
    id("boingball.app-version")
}

val keystorePropsFile = isolated.rootProject.projectDirectory.file("keystore.properties").asFile
val keystoreProps = if (keystorePropsFile.exists()) {
    Properties().also { props -> keystorePropsFile.inputStream().use { props.load(it) } }
} else null

kotlin {
    // Compiles Kotlin and Java with JDK 17 and targets Java 17 bytecode.
    jvmToolchain(17)
}
dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.core.splashscreen)
    // Installs the baseline profile on devices that did not get it from Play (e.g. sideloads).
    implementation(libs.androidx.profileinstaller)
    // Generates src/release/generated/baselineProfiles with :baselineprofile.
    baselineProfile(projects.baselineprofile)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.android)
}

android {
    namespace = "com.rff.boingballdemo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.rff.boingballdemo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersion.code.get()
        versionName = appVersion.name.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    if (keystoreProps != null) {
        signingConfigs {
            create("release") {
                storeFile = file(keystoreProps["storeFile"] as String)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = if (keystoreProps != null) signingConfigs.getByName("release") else null
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

// Any Android build also refreshes the committed iOS version file after a version bump.
tasks.named("preBuild") {
    dependsOn(":shared:generateXcodeVersionConfig")
}
