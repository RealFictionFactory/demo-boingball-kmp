package com.rff.boingballdemo.di

import org.koin.core.qualifier.named

/**
 * Qualifier for the application-wide `CoroutineScope`. Work launched in it outlives
 * any screen, e.g. a settings write started just before the user closes a window.
 */
val AppScope = named("AppScope")
