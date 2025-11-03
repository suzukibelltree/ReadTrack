package com.belltree.readtrack.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.belltree.readtrack.app.LoginDataStore
import com.belltree.readtrack.domain.model.AnimationType
import com.belltree.readtrack.domain.model.LoginMessageResult
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object LoginPrefsKeys {
    val LAST_LOGIN_DATE = stringPreferencesKey("last_login_date")
    val STREAK_DAYS = intPreferencesKey("streak_days")
}

suspend fun updateLoginInfoAndGenerateMessage(context: Context): LoginMessageResult {
    val dataStore = context.LoginDataStore
    val today = LocalDate.now()

    var messageResult: LoginMessageResult? = null
    dataStore.edit { prefs ->
        val lastLogin = prefs[LoginPrefsKeys.LAST_LOGIN_DATE]?.let { LocalDate.parse(it) }
        val currentStreak = prefs[LoginPrefsKeys.STREAK_DAYS] ?: 0
        val daysDiff = if (lastLogin != null) ChronoUnit.DAYS.between(lastLogin, today) else 0L
        val newStreak = when {
            lastLogin == null -> 1
            daysDiff == 1L -> currentStreak + 1
            daysDiff > 1L -> 1
            else -> currentStreak
        }
        messageResult = when {
            daysDiff == 1L -> LoginMessageResult(
                message = "${newStreak}日連続のログインです！頑張りましょう！",
                animationType = when (newStreak) {
                    in 1..6 -> AnimationType.STREAK_SMALL
                    in 7..14 -> AnimationType.STREAK_MEDIUM
                    else -> AnimationType.STREAK_BIG
                }
            )

            daysDiff > 1L -> LoginMessageResult(
                message = "${daysDiff}日ぶりのログインです！また頑張りましょう！",
                animationType = AnimationType.RETURN
            )

            else -> LoginMessageResult(
                message = "",
                animationType = AnimationType.NOTHING
            )
        }


        prefs[LoginPrefsKeys.STREAK_DAYS] = newStreak
        prefs[LoginPrefsKeys.LAST_LOGIN_DATE] = today.toString()
    }
    return messageResult!!
}

