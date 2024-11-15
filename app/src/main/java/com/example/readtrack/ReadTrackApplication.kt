package com.example.readtrack

import android.app.Application
import com.example.readtrack.room.AppContainer
import com.example.readtrack.room.AppDataContainer

class ReadTrackApplication : Application(){
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
    }
}