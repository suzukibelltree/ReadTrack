package com.belltree.readtrack.ui.mybookdetail

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

fun postToX(bookTitle: String, context: Context) {
    val message =
        "${bookTitle}を読了しました！ #readtrack \nhttps://play.google.com/store/apps/details?id=com.belltree.readtrack"

    val tweetIntent = Intent(Intent.ACTION_SEND).apply {
        setType("text/plain")
        putExtra(Intent.EXTRA_TEXT, message)
    }

    val packageManager = context.packageManager
    val resolvedInfoList =
        packageManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY)

    val hasTwitter =
        resolvedInfoList.any { it.activityInfo.packageName.startsWith("com.twitter.android") }

    try {
        if (hasTwitter) {
            // Twitterアプリへ
            tweetIntent.setPackage("com.twitter.android")
            context.startActivity(tweetIntent)
        } else {
            // ブラウザへ
            val url = "https://twitter.com/intent/tweet?text=" + Uri.encode(message)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}