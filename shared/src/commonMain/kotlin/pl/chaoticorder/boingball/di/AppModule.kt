package pl.chaoticorder.boingball.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import pl.chaoticorder.boingball.data.local.AppSettings
import pl.chaoticorder.boingball.data.local.createPreferencesDataStore
import pl.chaoticorder.boingball.main.BoingBallViewModel
import pl.chaoticorder.boingball.preferences.PreferencesViewModel
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
