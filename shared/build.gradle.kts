import boingball.version.GenerateVersionConfig
import boingball.version.GenerateXcodeVersionConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    // Compiles the Koin DSL modules (singleOf/viewModelOf/single { }) at build time and fails the
    // build when a dependency has no definition (e.g. [KOIN-D002] Missing definition).
    alias(libs.plugins.koin.compiler)
    id("boingball.app-version")
}

// Generates VersionConfig.kt from version.properties. Added below as a task-backed source
// directory, so every task that reads commonMain sources (compile, lint, IDE sync) depends on it.
val generateVersionConfig = tasks.register<GenerateVersionConfig>("generateVersionConfig") {
    packageName = "com.rff.boingballdemo"
    versionCode = appVersion.code
    versionName = appVersion.name
    outputDir = layout.buildDirectory.dir("generated/source/versioning/commonMain/kotlin")
}

// Writes the committed iOS version file (see GenerateXcodeVersionConfig).
val generateXcodeVersionConfig = tasks.register<GenerateXcodeVersionConfig>("generateXcodeVersionConfig") {
    versionCode = appVersion.code
    versionName = appVersion.name
    outputFile = isolated.rootProject.projectDirectory.file("iosApp/Configuration/GeneratedVersion.xcconfig")
}

kotlin {
    // Compiles the Android target with JDK 17 and targets Java 17 bytecode.
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
        namespace = "com.rff.boingballdemo.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateVersionConfig)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.session)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.navigationevent.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.datastore)
            implementation(libs.datastore.preferences)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        // Compose UI tests run on the JVM through Robolectric.
        getByName("androidHostTest").dependencies {
            implementation(libs.compose.ui.test)
            implementation(libs.androidx.compose.ui.test.manifest)
            implementation(libs.robolectric)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

// Keep the committed GeneratedVersion.xcconfig in sync when building from Xcode.
tasks.matching {
    it.name == "embedAndSignAppleFrameworkForXcode" || it.name == "embedSwiftExportForXcode"
}.configureEach {
    dependsOn(generateXcodeVersionConfig)
}
