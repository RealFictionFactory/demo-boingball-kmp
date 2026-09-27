package boingball.version

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property

/** Exposes the app version to a build script as `appVersion.code` and `appVersion.name`. */
abstract class AppVersionExtension {
    abstract val code: Property<Int>
    abstract val name: Property<String>
}

/**
 * Reads `version.properties` from the root directory, lazily and without touching the root
 * project model, so it works with the configuration cache and Isolated Projects. Editing the
 * file invalidates the configuration cache and reruns the tasks that use the version.
 */
class AppVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val file = project.isolated.rootProject.projectDirectory.file("version.properties")
        val version = project.providers.fileContents(file).asText.map(AppVersion::parse)

        project.extensions.create("appVersion", AppVersionExtension::class.java).apply {
            code.set(version.map { it.code })
            name.set(version.map { it.name })
            code.finalizeValueOnRead()
            name.finalizeValueOnRead()
        }
    }
}
