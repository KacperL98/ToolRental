package com.kacper.itemxxx.db.converts

import androidx.room.TypeConverter
import java.util.*

class DateTimeConverters {
    @TypeConverter
    fun toCalendar(l: Long): Calendar? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = l
        return calendar
    }

    @TypeConverter
    fun fromCalendar(calendar: Calendar?): Long? {
        return calendar?.time?.time
    }
}