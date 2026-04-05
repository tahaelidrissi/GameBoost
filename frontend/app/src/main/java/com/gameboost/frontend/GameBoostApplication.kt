package com.gameboost.frontend

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GameBoostApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialisation de l'app
    }
}
