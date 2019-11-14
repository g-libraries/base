package com.core.base.usecases

import androidx.room.TypeConverter

object RoomConverterNullString {
    @TypeConverter
    fun fromNullToString(value: String?): String {
        return value ?: "0"
    }
}