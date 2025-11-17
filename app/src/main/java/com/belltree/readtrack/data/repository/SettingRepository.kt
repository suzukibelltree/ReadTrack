package com.belltree.readtrack.data.repository

import android.content.Context
import com.belltree.readtrack.data.local.datastore.getValue
import com.belltree.readtrack.data.local.datastore.saveValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val notificationEnabled: Flow<Boolean> =
        getValue(context, "notification_enabled").map { it == "true" }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        saveValue(context, "notification_enabled", enabled.toString())
    }
}
