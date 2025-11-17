package com.belltree.readtrack.core.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.belltree.readtrack.core.parseDateToMillis
import com.belltree.readtrack.data.local.datastore.getValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class BookUpdateCheckWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val enabled = getValue(applicationContext, "notification_enabled").first() == "true"
        if (!enabled) return Result.success()
        else {
            val currentTime = System.currentTimeMillis()
            val oneWeekMillis = TimeUnit.DAYS.toMillis(7) // 1週間（7日）

            // Flow<String> を同期的に取得
            val lastUpdatedFlow = getValue(applicationContext, "lastUpdatedDate")

            val lastUpdatedString = runBlocking { lastUpdatedFlow.first() }
            val lastUpdatedMillis = parseDateToMillis(lastUpdatedString)

            // lastUpdated を適切に取得できた場合のみ比較
            if (lastUpdatedMillis != null && currentTime - lastUpdatedMillis > oneWeekMillis) {
                sendNotification()
            }

            return Result.success()
        }
    }

    private fun sendNotification() {
        showNotification(applicationContext) // 通知を送る
    }
}