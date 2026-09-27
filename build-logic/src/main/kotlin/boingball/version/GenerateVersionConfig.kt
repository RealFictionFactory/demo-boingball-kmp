package boingball.version

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/** Generates a Kotlin `VersionConfig` object; add [outputDir] as a source directory. */
@CacheableTask
abstract class GenerateVersionConfig : DefaultTask() {
    @get:Input abstract val packageName: Property<String>
    @get:Input abstract val versionCode: Property<Int>
    @get:Input abstract val versionName: Property<String>
    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val pkg = packageName.get()
        val dir = outputDir.get().asFile
        dir.deleteRecursively()
        val file = dir.resolve(pkg.replace('.', '/')).resolve("VersionConfig.kt")
        file.parentFile.mkdirs()
        file.writeText(
            """
            |package $pkg
            |
            |object VersionConfig {
            |    const val VERSION_CODE = ${versionCode.get()}
            |    const val VERSION_NAME = "${versionName.get()}"
            |}
            |""".trimMargin()
        )
    }
}
