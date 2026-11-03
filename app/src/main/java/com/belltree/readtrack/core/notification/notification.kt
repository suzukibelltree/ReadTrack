package com.belltree.readtrack.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.belltree.readtrack.R

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
        .setContentIntent(createLaunchAppPendingIntent(context)) // タップでアプリを開く
        .setAutoCancel(true) // タップ後に通知を消す
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

/**
 * アプリを開くPendingIntentを作成する
 * ランチャーと同じIntentを使うことで、起動中のタスクがあればそのまま前面に戻す
 */
private fun createLaunchAppPendingIntent(context: Context): PendingIntent? {
    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        ?: return null
    return PendingIntent.getActivity(
        context,
        0,
        launchIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
}
