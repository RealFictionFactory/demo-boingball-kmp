package com.rff.boingballdemo.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Keeps upstream work alive across configuration changes, but not in the background. */
private const val STOP_TIMEOUT_MS = 5_000L

/**
 * Exposes UI state that is computed only while the UI collects it
 * (plus [STOP_TIMEOUT_MS] after it stops, so a rotation does not restart it).
 */
fun <T> Flow<T>.stateInWhileSubscribed(scope: CoroutineScope, initialValue: T): StateFlow<T> =
    stateIn(scope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), initialValue)
