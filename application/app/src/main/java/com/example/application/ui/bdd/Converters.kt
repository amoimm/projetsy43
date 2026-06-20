package com.example.application.ui.bdd

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromActivityCategory(value: ActivityCategory): String {
        return value.name
    }

    @TypeConverter
    fun toActivityCategory(value: String): ActivityCategory {
        return ActivityCategory.valueOf(value)
    }

    @TypeConverter
    fun fromFrequency(value: Frequency): String {
        return value.name
    }

    @TypeConverter
    fun toFrequency(value: String): Frequency {
        return Frequency.valueOf(value)
    }
}
