package com.rff.boingballdemo

import android.app.Application
import android.content.pm.ApplicationInfo
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import com.rff.boingballdemo.di.initKoin

class BoingBallApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Debuggable builds only; avoids generating BuildConfig just for this flag.
        val isDebuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

        initKoin {
            // Verbose DI logs while developing; only errors in release builds.
            androidLogger(if (isDebuggable) Level.DEBUG else Level.ERROR)
            androidContext(this@BoingBallApplication)
        }
    }
}
