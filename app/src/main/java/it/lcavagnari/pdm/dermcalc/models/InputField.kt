package it.lcavagnari.pdm.dermcalc.models


import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

// Material3's DatePicker works in epoch milliseconds; the model uses kotlinx LocalDate.
// These two functions bridge the two representations at the boundary.
@OptIn(ExperimentalTime::class)
fun LocalDate.toEpochMillis(): Long {
    return atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}

@OptIn(ExperimentalTime::class)
fun Long.toLocalDate(): LocalDate {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
}

@Serializable
enum class Sex { Male, Female, Other }

@Serializable
enum class HeightMeasurements { Metric, Imperial }

@Serializable
enum class WeightMeasurements { Kilos, Pounds }

/*   ---------    */

@Serializable
sealed interface InputField {
    val id: String
    val label: String
    val isRequired: Boolean get() = true

    // Defaults to false so every new field is automatically gated until the model validates it.
    val isValid: Boolean get() = false

    // Any? allows OnBoardItem to read the value generically without knowing the subtype.
    val value: Any? get() = null
}


@Serializable
data class TextInput(
    override val id: String,
    override val label: String,
    override val value: String = "",
    override val isValid: Boolean = false
) : InputField

@Serializable
data class SexInput(
    override val id: String,
    override val label: String,
    override val isValid: Boolean = false,

    override val value: Sex? = null

) : InputField

@Serializable
data class HeightInput(
    override val id: String,
    override val label: String,
    override val isRequired: Boolean = true,
    override val isValid: Boolean = false,

    // Always stored as cm internally; imperial input is converted on the way in.
    override val value: Double? = null,
    val isMetric: Boolean = true

) : InputField {
    fun cmToFeetInches(cm: Double = value ?: 0.0): Pair<Double, Double> {
        val totalInches = cm / 2.54
        return totalInches / 12.0 to totalInches % 12.0
    }

    fun feetInchesToCm(feet: Int, inches: Int): Double =
        (feet * 12 + inches) * 2.54
}

@Serializable
data class WeightInput(
    override val id: String,
    override val label: String,
    override val isRequired: Boolean = true,
    override val isValid: Boolean = false,

    override val value: Double? = null,
    val isKilos: Boolean = true
) : InputField {
    fun kilosToPounds(kilos: Double = value ?: 0.0): Double = kilos * 2.2046
}

@Serializable
data class DateInput(
    override val id: String,
    override val label: String,
    override val value: LocalDate? = null,
    override val isValid: Boolean = false
) : InputField