package com.rff.boingballdemo.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rff.boingballdemo.data.local.createPreferencesDataStore
import com.rff.boingballdemo.data.local.preferencesDataStorePath
import com.rff.boingballdemo.screens.boingball.audio.BoingBallAudioPlayer
import com.rff.boingballdemo.screens.musicplayer.AndroidMusicPlayback
import com.rff.boingballdemo.screens.musicplayer.MusicPlayback
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    single<DataStore<Preferences>> { createPreferencesDataStore(preferencesDataStorePath(androidContext())) }
    singleOf(::BoingBallAudioPlayer)
    factory<MusicPlayback> { AndroidMusicPlayback(get()) }
}
