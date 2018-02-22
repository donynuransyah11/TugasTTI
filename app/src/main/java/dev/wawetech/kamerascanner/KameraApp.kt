package dev.wawetech.kamerascanner

import android.app.Application

/**
 * Created by donynuransyah on 2/8/18.
 */
class KameraApp : Application() {
    companion object {
        lateinit var instance: KameraApp
            private set
    }
    override fun onCreate() {
        super.onCreate()
    }


}