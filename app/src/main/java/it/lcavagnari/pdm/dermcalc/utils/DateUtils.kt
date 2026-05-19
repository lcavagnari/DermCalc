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
