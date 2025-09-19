package com.belltree.readtrack.core.notification

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleBookUpdateCheck(context: Context) {
    Log.d("huga", "scheduleBookUpdateCheck")
    val workRequest =
        PeriodicWorkRequestBuilder<BookUpdateCheckWorker>(1, TimeUnit.DAYS) // TimeUnit.DAYS
            .setConstraints(
                Constraints.Builder()
                    .build()
            )
            .build()

    WorkManager.Companion.getInstance(context).enqueueUniquePeriodicWork(
        "book_update_check",
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )

}