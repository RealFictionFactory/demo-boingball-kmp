import java.util.Properties

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.koin.compiler) apply false
}

val versionPropertiesFile = rootProject.file("version.properties")

val versionProperties = Properties().apply {
    versionPropertiesFile.inputStream().use(::load)
}

fun versionPart(name: String): Int =
    versionProperties.getProperty(name)?.trim()?.toInt()
        ?: error("Missing or invalid $name in version.properties")

val versionMajor = versionPart("VERSION_MAJOR")
val versionMinor = versionPart("VERSION_MINOR")
val versionBuild = versionPart("VERSION_BUILD")

val appVersionCode = versionMajor * 100000 + versionMinor * 1000 + versionBuild
val appVersionName = "$versionMajor.$versionMinor.$versionBuild"

extra["appVersionCode"] = appVersionCode
extra["appVersionName"] = appVersionName

tasks.register("generateVersionConfig") {
    val outputFile = layout.buildDirectory.file(
        "generated/source/versioning/commonMain/kotlin/com/rff/boingballdemo/VersionConfig.kt"
    )
    val content = """
        package com.rff.boingballdemo

        object VersionConfig {
            const val VERSION_CODE = $appVersionCode
            const val VERSION_NAME = "$appVersionName"
        }
    """.trimIndent()

    inputs.file(versionPropertiesFile)
    outputs.file(outputFile)

    doLast {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(content)
    }
}

tasks.register("generateXcodeVersionConfig") {
    val outputFile = rootProject.file("iosApp/Configuration/GeneratedVersion.xcconfig")
    val content = """
        CURRENT_PROJECT_VERSION=$appVersionCode
        MARKETING_VERSION=$appVersionName
    """.trimIndent()

    inputs.file(versionPropertiesFile)
    outputs.file(outputFile)

    doLast {
        outputFile.parentFile.mkdirs()
        outputFile.writeText(content)
    }
}
