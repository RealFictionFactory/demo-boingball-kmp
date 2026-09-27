package boingball.version

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Writes the Xcode build settings file with the app version. The file is committed: Xcode
 * reads it before its Gradle build phase runs, so it has to be current when Xcode starts.
 * Only rewritten when the version changes.
 */
abstract class GenerateXcodeVersionConfig : DefaultTask() {
    @get:Input abstract val versionCode: Property<Int>
    @get:Input abstract val versionName: Property<String>
    @get:OutputFile abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        outputFile.get().asFile.writeText(
            "CURRENT_PROJECT_VERSION=${versionCode.get()}\nMARKETING_VERSION=${versionName.get()}\n"
        )
    }
}
