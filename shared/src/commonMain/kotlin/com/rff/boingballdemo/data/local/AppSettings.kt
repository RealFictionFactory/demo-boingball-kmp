package com.rff.boingballdemo.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.component.VideoSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import com.rff.boingballdemo.VersionConfig
import kotlin.enums.enumEntries

/**
 * @param externalScope application-wide scope that runs writes, so a write finishes even
 * if the caller (e.g. a ViewModel) is cancelled while it is in progress.
 */
class AppSettings(
    private val preferences: DataStore<Preferences>,
    private val externalScope: CoroutineScope,
) {
    val boingBallPrefs: Flow<BoingBallPrefs> = preferences.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { it.toBoingBallPrefs() }

    val osStyle: Flow<OSStyle> = boingBallPrefs.map { it.osStyle }.distinctUntilChanged()

    fun getVersion() = VersionConfig.VERSION_NAME

    /**
     * Applies [transform] to the stored prefs in one atomic DataStore transaction, so
     * quick successive changes to different settings never overwrite each other.
     *
     * The write runs in [externalScope]: cancelling the caller only stops the wait,
     * not the write. Write errors reach the caller if it is still waiting.
     */
    suspend fun updateBoingBallPrefs(transform: (BoingBallPrefs) -> BoingBallPrefs) {
        externalScope.async {
            preferences.edit { preferences ->
                preferences.write(transform(preferences.toBoingBallPrefs()))
            }
        }.await()
    }

    private fun Preferences.toBoingBallPrefs() = BoingBallPrefs(
        themeColorIndex = this[KEY_THEME_COLOR_INDEX] ?: BoingBallPrefs.Default.themeColorIndex,
        altColorIndex = this[KEY_ALT_COLOR_INDEX] ?: BoingBallPrefs.Default.altColorIndex,
        drawBorders = this[KEY_DRAW_BORDERS] ?: BoingBallPrefs.Default.drawBorders,
        osStyle = decodeEnum(
            name = this[KEY_OS_STYLE],
            legacyOrdinal = this[LEGACY_KEY_OS_STYLE],
            default = BoingBallPrefs.Default.osStyle,
        ),
        videoSystem = decodeEnum(
            name = this[KEY_VIDEO_SYSTEM],
            legacyOrdinal = this[LEGACY_KEY_VIDEO_SYSTEM],
            default = BoingBallPrefs.Default.videoSystem,
        ),
    )

    private fun MutablePreferences.write(value: BoingBallPrefs) {
        this[KEY_THEME_COLOR_INDEX] = value.themeColorIndex
        this[KEY_ALT_COLOR_INDEX] = value.altColorIndex
        this[KEY_DRAW_BORDERS] = value.drawBorders
        this[KEY_OS_STYLE] = value.osStyle.name
        this[KEY_VIDEO_SYSTEM] = value.videoSystem.name
        remove(LEGACY_KEY_OS_STYLE)
        remove(LEGACY_KEY_VIDEO_SYSTEM)
    }

    companion object {
        private val KEY_THEME_COLOR_INDEX = intPreferencesKey("theme_color_index")
        private val KEY_ALT_COLOR_INDEX = intPreferencesKey("alt_color_index")
        private val KEY_DRAW_BORDERS = booleanPreferencesKey("draw_borders")
        private val KEY_OS_STYLE = stringPreferencesKey("os_style_name")
        private val KEY_VIDEO_SYSTEM = stringPreferencesKey("video_system_name")

        // Enums were stored as ordinals before; read them once as a fallback and
        // drop them on the next save.
        private val LEGACY_KEY_OS_STYLE = intPreferencesKey("os_style")
        private val LEGACY_KEY_VIDEO_SYSTEM = intPreferencesKey("video_system")
    }
}

/**
 * Resolves a persisted enum by [name], falling back to a legacy [legacyOrdinal] and then to
 * [default]. Unknown or out-of-range values never throw.
 */
internal inline fun <reified E : Enum<E>> decodeEnum(
    name: String?,
    legacyOrdinal: Int?,
    default: E,
): E {
    val entries = enumEntries<E>()
    return name?.let { stored -> entries.firstOrNull { it.name == stored } }
        ?: legacyOrdinal?.let { entries.getOrNull(it) }
        ?: default
}

data class BoingBallPrefs(
    val themeColorIndex: Int,
    val altColorIndex: Int,
    val drawBorders: Boolean,
    val osStyle: OSStyle,
    val videoSystem: VideoSystem,
) {
    /** The original demo look with this OS style kept ("Set original demo defaults"). */
    fun withDemoDefaults(): BoingBallPrefs = Default.copy(osStyle = osStyle)

    companion object {
        /**
         * The original 1984 demo look: red and white ball, no tile borders, PAL speed.
         * Used until the user changes a setting, and as the fallback for invalid values.
         */
        val Default = BoingBallPrefs(
            themeColorIndex = 0,
            altColorIndex = 3,
            drawBorders = false,
            osStyle = OSStyle.AmigaOS13,
            videoSystem = VideoSystem.PAL,
        )

        /** This app's own look: blue and white ball with tile borders ("Set app defaults"). */
        val App = BoingBallPrefs(
            themeColorIndex = 1,
            altColorIndex = 3,
            drawBorders = true,
            osStyle = OSStyle.AmigaOS13,
            videoSystem = VideoSystem.PAL,
        )
    }
}
