package com.rff.boingballdemo.data.local

import android.content.Context

internal fun preferencesDataStorePath(context: Context): String =
    context.filesDir.resolve(dataStoreFileName).absolutePath
