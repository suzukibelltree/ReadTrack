package com.belltree.readtrack.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.belltree.readtrack.R

//TODO: WorkManagerを利用してアプリがバックグラウンドで動いているときに通知を送る

/**
 * 通知チャンネルを作成する
 * @param context コンテキスト
 */
fun createNotificationChannel(context: Context) {
    val channelId = "example_channel"
    val channelName = "Example Channel"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = "This is an example notification channel"
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

/**
 * 通知を表示する
 */
fun showNotification(context: Context) {
    val channelId = "example_channel"
    val notificationId = 1

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.frame) // アイコン
        .setContentTitle(context.getString(R.string.app_notification_title)) // 通知のタイトル
        .setContentText(context.getString(R.string.app_notification_content)) // 通知の内容
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 優先度

    val notificationManager = NotificationManagerCompat.from(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    notificationManager.notify(notificationId, builder.build())
}