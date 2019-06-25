package k.t.sample_ble

import android.app.Application

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        @Volatile
        lateinit var appContext: BaseApplication
            private set
    }
}