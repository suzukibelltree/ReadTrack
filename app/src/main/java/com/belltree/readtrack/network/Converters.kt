package com.belltree.readtrack.network

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String?>?): String {
        return Json.encodeToString(list ?: emptyList())
    }

    @TypeConverter
    fun toStringList(json: String): List<String?> {
        return Json.decodeFromString(json)
    }
}
