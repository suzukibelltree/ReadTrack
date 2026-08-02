package com.belltree.readtrack.app

import android.app.Application
import androidx.work.Configuration
import com.belltree.readtrack.core.notification.createNotificationChannel
import com.belltree.readtrack.core.notification.scheduleBookUpdateCheck
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class ReadTrackApplication : Application(), Configuration.Provider {
    lateinit var appContainer: AppContainer

    // WorkManagerInitializerを無効化した代わりに、初回 WorkManager.getInstance() 呼び出し時に
    // この設定で遅延初期化される(on-demand initialization)。
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()

    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
        createNotificationChannel(this)
        // WorkManagerのDB構築等をメインスレッドから逃がすためバックグラウンドで初期化する
        CoroutineScope(Dispatchers.IO).launch {
            scheduleBookUpdateCheck(this@ReadTrackApplication)
        }
    }
}