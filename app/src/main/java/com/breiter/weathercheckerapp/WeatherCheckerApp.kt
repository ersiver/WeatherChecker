package com.breiter.weathercheckerapp

import android.app.Application
import timber.log.Timber

class WeatherCheckerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}