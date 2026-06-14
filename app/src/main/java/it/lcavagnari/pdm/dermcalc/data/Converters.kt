package it.lcavagnari.pdm.dermcalc.data

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@kotlin.time.ExperimentalTime
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()

    @TypeConverter
    fun toLocalDate(millis: Long?): LocalDate? = millis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): Long? = dateTime?.toInstant(TimeZone.UTC)?.toEpochMilliseconds()

    @TypeConverter
    fun toLocalDateTime(millis: Long?): LocalDateTime? = millis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC)
    }
}

