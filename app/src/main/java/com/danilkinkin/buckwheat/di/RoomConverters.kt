package com.danilkinkin.buckwheat.di

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.*

class RoomConverters {
    @TypeConverter
    fun dateToDateStamp(input: Date): Long = input.time

    @TypeConverter
    fun dateStampToCalendar(input: Long): Date = Date(input)

    @TypeConverter
    fun bigDecimalToString(input: BigDecimal): String = input.toPlainString()

    @TypeConverter
    fun stringToBigDecimal(input: String): BigDecimal = BigDecimal(input)

    @TypeConverter
    fun stringListToString(input: List<String>): String = input
        .joinToString(SEPARATOR) { it.replace("\\", "\\\\").replace("\n", "\\n") }

    @TypeConverter
    fun stringToStringList(input: String): List<String> = if (input.isEmpty()) {
        emptyList()
    } else {
        input.split(SEPARATOR).map { unescape(it) }
    }

    private fun unescape(input: String): String {
        val result = StringBuilder(input.length)
        var escaped = false

        input.forEach { char ->
            when {
                escaped -> {
                    result.append(if (char == 'n') '\n' else char)
                    escaped = false
                }
                char == '\\' -> escaped = true
                else -> result.append(char)
            }
        }

        return result.toString()
    }

    companion object {
        private const val SEPARATOR = "\n"
    }
}