package com.rff.boingballdemo.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLog
import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSUserDomainMask

/**
 * App-internal data belongs in Application Support, which is never shown to the user
 * (unlike Documents, which is exposed once file sharing is enabled). Application
 * Support is still included in iCloud and device backups.
 */
@OptIn(ExperimentalForeignApi::class)
fun getPreferencesDataStorePath(): String {
    val newPath = directoryPath(NSApplicationSupportDirectory, create = true) + "/$dataStoreFileName"
    return migrateFromDocuments(newPath)
}

actual fun createPreferencesDataStore(): DataStore<Preferences> {
    val path = getPreferencesDataStorePath()
    return getPreferencesDataStore(path)
}

/**
 * Earlier versions kept the file in Documents. Moves it to [newPath] once so users keep
 * their settings. If the move fails, keeps using the old file rather than resetting them.
 */
@OptIn(ExperimentalForeignApi::class)
private fun migrateFromDocuments(newPath: String): String {
    val fileManager = NSFileManager.defaultManager
    val legacyPath = directoryPath(NSDocumentDirectory, create = false) + "/$dataStoreFileName"
    if (!fileManager.fileExistsAtPath(legacyPath) || fileManager.fileExistsAtPath(newPath)) {
        return newPath
    }
    return if (fileManager.moveItemAtPath(legacyPath, toPath = newPath, error = null)) {
        newPath
    } else {
        NSLog("Could not move preferences from Documents to Application Support; using old location")
        legacyPath
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun directoryPath(directory: NSSearchPathDirectory, create: Boolean): String {
    // Application Support does not exist in a fresh app container, hence create = true.
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = directory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = create,
        error = null,
    )
    return requireNotNull(url?.path) { "Could not resolve directory $directory" }
}
