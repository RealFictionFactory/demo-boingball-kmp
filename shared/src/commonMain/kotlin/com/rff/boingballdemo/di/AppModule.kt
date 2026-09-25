package com.rff.boingballdemo.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rff.boingballdemo.screens.about.AboutViewModel
import com.rff.boingballdemo.screens.boingball.BoingBallViewModel
import com.rff.boingballdemo.screens.calculator.CalculatorViewModel
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.data.local.createPreferencesDataStore
import com.rff.boingballdemo.screens.clock.ClockViewModel
import com.rff.boingballdemo.screens.copper.CopperBarsViewModel
import com.rff.boingballdemo.screens.workbench.WorkbenchViewModel
import com.rff.boingballdemo.screens.musicplayer.MusicPlayerViewModel
import com.rff.boingballdemo.screens.preferences.PreferencesViewModel
import com.rff.boingballdemo.screens.shell.ShellViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single<DataStore<Preferences>> { createPreferencesDataStore() }
    singleOf(::AppSettings)

    viewModelOf(::WorkbenchViewModel)
    viewModelOf(::BoingBallViewModel)
    viewModelOf(::PreferencesViewModel)
    viewModelOf(::ClockViewModel)
    viewModelOf(::CopperBarsViewModel)
    viewModelOf(::CalculatorViewModel)
    viewModelOf(::ShellViewModel)
    viewModelOf(::AboutViewModel)
    viewModelOf(::MusicPlayerViewModel)
}
