package com.rff.boingballdemo.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rff.boingballdemo.data.local.createPreferencesDataStore
import com.rff.boingballdemo.data.local.preferencesDataStorePath
import com.rff.boingballdemo.screens.boingball.audio.BoingBallAudioPlayer
import com.rff.boingballdemo.screens.musicplayer.IosMusicPlayback
import com.rff.boingballdemo.screens.musicplayer.MusicPlayback
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    single<DataStore<Preferences>> { createPreferencesDataStore(preferencesDataStorePath()) }
    singleOf(::BoingBallAudioPlayer)
    factory<MusicPlayback> { IosMusicPlayback() }
}
