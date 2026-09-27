package com.rff.boingballdemo.di

import com.rff.boingballdemo.screens.about.AboutViewModel
import com.rff.boingballdemo.screens.boingball.BoingBallViewModel
import com.rff.boingballdemo.screens.calculator.CalculatorViewModel
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.screens.clock.ClockViewModel
import com.rff.boingballdemo.screens.musicplayer.MusicPlayerViewModel
import com.rff.boingballdemo.screens.preferences.PreferencesViewModel
import com.rff.boingballdemo.screens.shell.ShellViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Platform bindings, including the preferences `DataStore<Preferences>` that [AppSettings] needs. */
expect val platformModule: Module

val sharedModule = module {
    single<CoroutineScope>(AppScope) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single { AppSettings(preferences = get(), externalScope = get(AppScope)) }

    viewModelOf(::BoingBallViewModel)
    viewModelOf(::PreferencesViewModel)
    viewModelOf(::ClockViewModel)
    viewModelOf(::CalculatorViewModel)
    viewModelOf(::ShellViewModel)
    viewModelOf(::AboutViewModel)
    viewModelOf(::MusicPlayerViewModel)
}
