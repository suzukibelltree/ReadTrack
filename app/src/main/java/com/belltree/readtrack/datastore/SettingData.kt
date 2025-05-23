package com.belltree.readtrack.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.belltree.readtrack.datastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// character_size
// theme_color
// datastoreに値を保存する
suspend fun saveValue(context: Context, key: String, value: String) {
    // ここにコードを追加
    context.datastore.edit { preferences ->
        preferences[stringPreferencesKey(key)] = value

    }
}

fun getValue(context: Context, key: String): Flow<String> {
    return context.datastore.data.map { preferences ->
        preferences[stringPreferencesKey(key)] ?: "unknown"
    }
}

/**
 * アプリが初回起動かどうかを判定する
 */
suspend fun isFirstLaunch(context: Context): Boolean {
    val isFirstLaunchKey = booleanPreferencesKey("is_first_launch")
    val preferences = context.datastore.data.first()
    val isFirstLaunch = preferences[isFirstLaunchKey] ?: true

    if (isFirstLaunch) {
        context.datastore.edit { preferences ->
            preferences[isFirstLaunchKey] = false
        }
    }

    return isFirstLaunch
}