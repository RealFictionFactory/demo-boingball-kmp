package boingball.version

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AppVersionTest {
    @Test
    fun parsesVersionAndIgnoresComments() {
        val version = AppVersion.parse(
            """
            # comment
            VERSION_MAJOR=1
            VERSION_MINOR = 5
            VERSION_BUILD=7
            """.trimIndent()
        )
        assertEquals("1.5.7", version.name)
        assertEquals(105_007, version.code)
    }

    @Test
    fun codesKeepVersionOrder() {
        val versions = listOf(AppVersion(1, 9, 999), AppVersion(1, 10, 0), AppVersion(2, 0, 0))
        assertEquals(versions.map { it.code }.sorted(), versions.map { it.code })
    }

    @Test
    fun missingKeyNamesTheKey() {
        val error = assertFailsWith<IllegalArgumentException> {
            AppVersion.parse("VERSION_MAJOR=1\nVERSION_MINOR=0")
        }
        assertTrue("VERSION_BUILD" in error.message.orEmpty())
    }

    @Test
    fun nonNumericValueIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            AppVersion.parse("VERSION_MAJOR=1\nVERSION_MINOR=x\nVERSION_BUILD=0")
        }
    }

    @Test
    fun partsThatWouldCollideAreRejected() {
        assertFailsWith<IllegalArgumentException> { AppVersion(1, 100, 0) }
        assertFailsWith<IllegalArgumentException> { AppVersion(1, 0, 1000) }
    }
}
