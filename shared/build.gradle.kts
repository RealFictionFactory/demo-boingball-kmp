plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.koin.compiler)
}

val generatedVersionSourceDir = rootProject.layout.buildDirectory.dir(
    "generated/source/versioning/commonMain/kotlin"
)

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
            kotlin.srcDir(generatedVersionSourceDir)
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
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

tasks.matching {
    it.name.startsWith("compile") ||
        it.name == "embedAndSignAppleFrameworkForXcode" ||
        it.name == "embedSwiftExportForXcode"
}.configureEach {
    dependsOn(rootProject.tasks.named("generateVersionConfig"))
    dependsOn(rootProject.tasks.named("generateXcodeVersionConfig"))
}
