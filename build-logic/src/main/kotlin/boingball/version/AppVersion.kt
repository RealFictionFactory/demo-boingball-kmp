package boingball.version

import java.io.StringReader
import java.util.Properties

/**
 * The app version from `version.properties`, shared by Android and iOS.
 *
 * [code] is `major * 100000 + minor * 1000 + build`, so [minor] and [build] must stay below
 * 100 and 1000 respectively, or two different versions would get the same code.
 */
data class AppVersion(val major: Int, val minor: Int, val build: Int) {
    init {
        require(major in 0..20_000) { "VERSION_MAJOR must be 0..20000, was $major" }
        require(minor in 0..99) { "VERSION_MINOR must be 0..99, was $minor" }
        require(build in 0..999) { "VERSION_BUILD must be 0..999, was $build" }
    }

    val code: Int get() = major * 100_000 + minor * 1_000 + build
    val name: String get() = "$major.$minor.$build"

    companion object {
        /** Parses the contents of a `version.properties` file. */
        fun parse(text: String): AppVersion {
            val properties = Properties().apply { load(StringReader(text)) }
            fun part(key: String): Int {
                val value = properties.getProperty(key)?.trim()
                    ?: throw IllegalArgumentException("Missing $key in version.properties")
                return value.toIntOrNull()
                    ?: throw IllegalArgumentException("$key in version.properties is not a number: '$value'")
            }
            return AppVersion(part("VERSION_MAJOR"), part("VERSION_MINOR"), part("VERSION_BUILD"))
        }
    }
}
