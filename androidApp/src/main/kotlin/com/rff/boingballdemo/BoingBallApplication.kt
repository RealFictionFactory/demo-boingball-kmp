package com.rff.boingballdemo

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import com.rff.boingballdemo.data.local.initPreferencesDataStore
import com.rff.boingballdemo.di.initKoin

class BoingBallApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initPreferencesDataStore(this)

        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BoingBallApplication)
        }
    }
}
