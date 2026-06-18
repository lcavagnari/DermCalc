/*
 * Copyright (C) 2026 Luca Cavagnari
 *
 * This file is part of DermCalc, final project for the Mobile Device Programming course of Univerità Degli Studi Dell'Insubria.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 */
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

