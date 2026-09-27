package com.rff.boingballdemo.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "boing.preferences_pb"

/**
 * Creates the preferences DataStore at [path]. Each platform's Koin `platformModule`
 * binds it as a single instance, since DataStore allows only one instance per file.
 */
fun createPreferencesDataStore(path: String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath { path.toPath() }
