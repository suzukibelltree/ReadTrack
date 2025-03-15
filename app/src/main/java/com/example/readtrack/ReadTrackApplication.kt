package com.example.readtrack

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.readtrack.notification.createNotificationChannel
import com.example.readtrack.room.AppContainer
import com.example.readtrack.room.AppDataContainer
import com.example.readtrack.workmanager.scheduleBookUpdateCheck
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReadTrackApplication : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        createNotificationChannel(this)
        scheduleBookUpdateCheck(this)
    }
}