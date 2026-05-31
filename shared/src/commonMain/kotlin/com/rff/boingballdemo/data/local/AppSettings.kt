package com.rff.boingballdemo.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.rff.boingballdemo.component.OSStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import com.rff.boingballdemo.VersionConfig

class AppSettings(
    private val preferences: DataStore<Preferences>
) {
    val boingBallPrefs: Flow<BoingBallPrefs> = preferences.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            BoingBallPrefs(
                themeColorIndex =  preferences[KEY_THEME_COLOR_INDEX] ?: 1,
                altColorIndex = preferences[KEY_ALT_COLOR_INDEX] ?: 3,
                drawBorders = preferences[KEY_DRAW_BORDERS] ?: true,
                osStyle = OSStyle.entries[preferences[KEY_OS_STYLE] ?: OSStyle.AmigaOS13.ordinal]
            )
        }

    fun getVersion() = VersionConfig.VERSION_NAME

    suspend fun saveBoingBallPrefs(value: BoingBallPrefs) {
        preferences.edit { preferences ->
            preferences[KEY_THEME_COLOR_INDEX] = value.themeColorIndex
            preferences[KEY_ALT_COLOR_INDEX] = value.altColorIndex
            preferences[KEY_DRAW_BORDERS] = value.drawBorders
            preferences[KEY_OS_STYLE] = value.osStyle.ordinal
        }
    }

    companion object {
        private val KEY_THEME_COLOR_INDEX = intPreferencesKey("theme_color_index")
        private val KEY_ALT_COLOR_INDEX = intPreferencesKey("alt_color_index")
        private val KEY_DRAW_BORDERS = booleanPreferencesKey("draw_borders")
        private val KEY_OS_STYLE = intPreferencesKey("os_style")
    }
}

data class BoingBallPrefs(
    val themeColorIndex: Int,
    val altColorIndex: Int,
    val drawBorders: Boolean,
    val osStyle: OSStyle = OSStyle.AmigaOS13
)
