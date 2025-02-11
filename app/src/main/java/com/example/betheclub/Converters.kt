package com.example.betheclub

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Converters class provides methods for converting between a List of Floats and a String,
 * which is necessary for storing List<Float> in Room database.
 * Room doesn't support storing List<Float> directly, so it needs to be converted to a String (typically JSON)
 * before storing, and then converted back to List<Float> when retrieved.
 */
class Converters {
    @TypeConverter
    fun fromString(value: String?): List<Float>? {
        val listType = object : TypeToken<List<Float>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Float>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}