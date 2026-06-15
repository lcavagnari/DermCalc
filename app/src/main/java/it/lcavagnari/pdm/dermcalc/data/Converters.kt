package it.lcavagnari.pdm.dermcalc.data

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * Room type converters for Kotlinx DateTime types.
 * 
 * Converts between [LocalDate] and epoch milliseconds for Room storage.
 */
@kotlin.time.ExperimentalTime
class Converters {
    /**
     * Converts a [LocalDate] to epoch milliseconds for Room storage.
     * 
     * @param date the [LocalDate] to convert, nullable
     * @return epoch milliseconds in UTC timezone, or null if input is null
     */
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()

    /**
     * Converts epoch milliseconds to a [LocalDate].
     * 
     * @param millis epoch milliseconds, nullable
     * @return [LocalDate] in UTC timezone, or null if input is null
     */
    @TypeConverter
    fun toLocalDate(millis: Long?): LocalDate? = millis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
    }
}

