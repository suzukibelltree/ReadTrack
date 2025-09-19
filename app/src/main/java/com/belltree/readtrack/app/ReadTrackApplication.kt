package com.belltree.readtrack.app

import android.app.Application
import com.belltree.readtrack.core.notification.createNotificationChannel
import com.belltree.readtrack.core.notification.scheduleBookUpdateCheck
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReadTrackApplication : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
        createNotificationChannel(this)
        scheduleBookUpdateCheck(this)
    }
}