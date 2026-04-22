package com.jnetai.gdprcompliance.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toDate(date: String?): LocalDate? = date?.let { LocalDate.parse(it) }
}