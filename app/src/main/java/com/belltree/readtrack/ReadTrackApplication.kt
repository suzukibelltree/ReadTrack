package com.belltree.readtrack

import android.app.Application
import com.belltree.readtrack.notification.createNotificationChannel
import com.belltree.readtrack.room.AppContainer
import com.belltree.readtrack.room.AppDataContainer
import com.belltree.readtrack.workmanager.scheduleBookUpdateCheck
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