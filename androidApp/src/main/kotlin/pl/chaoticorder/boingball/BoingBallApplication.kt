package pl.chaoticorder.boingball

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import pl.chaoticorder.boingball.data.local.initPreferencesDataStore
import pl.chaoticorder.boingball.di.initKoin

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
