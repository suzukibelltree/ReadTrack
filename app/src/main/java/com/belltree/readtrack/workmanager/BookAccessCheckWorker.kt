package com.belltree.readtrack.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.belltree.readtrack.datastore.getValue
import com.belltree.readtrack.notification.showNotification
import com.belltree.readtrack.parseDateToMillis
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class BookUpdateCheckWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val currentTime = System.currentTimeMillis()
        val oneWeekMillis = TimeUnit.DAYS.toMillis(7) // 1週間（7日）

        // Flow<String> を同期的に取得
        val lastUpdatedFlow = getValue(applicationContext, "lastUpdatedDate")

        val lastUpdatedString = runBlocking { lastUpdatedFlow.first() }
        Log.d("huga", "Last updated string: $lastUpdatedString")
        val lastUpdatedMillis = parseDateToMillis(lastUpdatedString)

        // lastUpdated を適切に取得できた場合のみ比較
        if (lastUpdatedMillis != null && currentTime - lastUpdatedMillis > oneWeekMillis) {
            sendNotification()
        }

        return Result.success()
    }

    private fun sendNotification() {
        Log.d("huga", "sendNotification")
        showNotification(applicationContext) // 通知を送る
    }
}
