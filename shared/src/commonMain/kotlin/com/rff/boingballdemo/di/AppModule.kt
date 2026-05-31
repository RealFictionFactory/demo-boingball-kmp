package com.rff.boingballdemo.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.data.local.createPreferencesDataStore
import com.rff.boingballdemo.main.BoingBallViewModel
import com.rff.boingballdemo.preferences.PreferencesViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single<DataStore<Preferences>> { createPreferencesDataStore() }
    singleOf(::AppSettings)

    viewModelOf(::BoingBallViewModel)
    viewModelOf(::PreferencesViewModel)
}
