package com.rff.boingballdemo.testing

import com.rff.boingballdemo.data.local.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.TestDispatcher

/** [AppSettings] over an in-memory store, with the app scope running on [dispatcher]. */
fun testAppSettings(dispatcher: TestDispatcher) = AppSettings(
    preferences = InMemoryDataStore(),
    externalScope = CoroutineScope(SupervisorJob() + dispatcher),
)
