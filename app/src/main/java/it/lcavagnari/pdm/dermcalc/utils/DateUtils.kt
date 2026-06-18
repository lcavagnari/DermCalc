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
package it.lcavagnari.pdm.dermcalc.utils


import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Returns the current local date-time in the device's default time zone.
 *
 * @return Current date-time as [kotlinx.datetime.LocalDateTime].
 */
// Clock.System requires opt-in until kotlinx.datetime stabilises the Time API.
@OptIn(ExperimentalTime::class)
fun today(): LocalDateTime = Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())
