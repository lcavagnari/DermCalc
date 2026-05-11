package it.lcavagnari.pdm.dermcalc.models


import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime


/**
 * Material3's DatePicker works in epoch milliseconds; the model uses kotlinx LocalDate.
 * These two functions bridge the two representations at the boundary.
 * @return Long — epoch milliseconds.
 */
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

/**
 * Gender types.
 */
@Serializable
enum class Sex { Male, Female, Other }

/**
 * Height measurements, Metric (Most of the world), Imperial(America and the UK).
 */
@Serializable
enum class HeightMeasurements { Metric, Imperial }


/**
 * Weight measurements, Pounds (America and the UK), Kilos (Most of the world).
 */
@Serializable
enum class WeightMeasurements { Kilos, Pounds }

/*   ---------    */

/**
 * interface for all input types.
 *
 * @property id - unique identifier
 * @property label - label for the field
 * @property value - default value of the field, type-safe, allows to read the value generically without knowing the subtype.
 * @property isValid - whether the value in the field is valid or not, based on specific criteria. Defaults to false.
 * @property isRequired - whether a value is required or not. Defaults to true.
 */
@Serializable
sealed interface InputField {
    val id: String
    val label: String
    val isRequired: Boolean get() = true
    val isValid: Boolean get() = false

    val value: Any? get() = null
}


/**
 * Text-based input, e.g. name, address, etc.
 *
 * @property id - unique identifier
 * @property label - label for the field
 * @property value - default value of the field, type-safe. Here an empty string.
 * @property isValid - whether the value in the field is valid or not, based on specific criteria. Defaults to false.
 * @property isRequired - whether a value is required or not. Defaults to true.
 *
 * @constructor Create empty Text input
 */
@Serializable
data class TextInput(
    override val id: String,
    override val label: String,
    override val value: String = "",
    override val isValid: Boolean = false
) : InputField

/**
 * Sex/gender input, e.g. biological sex of the patient.
 * Supports three values: Male, Female, Other.
 *
 * @property id - unique identifier
 * @property label - label for the field
 * @property value - selected sex. Defaults to [Sex.Other].
 * @property isValid - whether the selected sex is valid or not. Defaults to false.
 * @property isRequired - whether a value is required or not. Defaults to false.
 *
 * @constructor Create empty Sex input
 */
@Serializable
data class SexInput(
    override val id: String,
    override val label: String,
    override val isValid: Boolean = false,
    override val isRequired: Boolean = false,

    override val value: Sex? = Sex.Other

) : InputField

/**
 * Height input, e.g. user's height
 * Based on the metric for the tallest(272cm) and shortest(50cm) person ever lived.
 *
 * @property id - unique identifier
 * @property label - label for the field
 * @property value - height stored in centimetres. Defaults to null.
 * @property isValid - whether the height is within plausible bounds (50–272 cm). Defaults to false.
 * @property isRequired - whether a value is required or not. Defaults to true.
 *
 * @constructor Create empty Height input
 */
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

    /**
     * Converts height cm to feet and inches.
     *
     * @param cm - height in cm.
     * @return Pair<feet, Inches> - height in imperial system as Double.
     */
    fun cmToFeetInches(cm: Double = value ?: 0.0): Pair<Double, Double> {
        val totalInches = cm / 2.54
        return totalInches / 12.0 to totalInches % 12.0
    }

    /**
     * Converts feet and inches to centimetres.
     *
     * @param feet - height in feet.
     * @param inches - remaining height in inches.
     * @return height in centimetres as Double.
     */
    fun feetInchesToCm(feet: Int, inches: Int): Double =
        (feet * 12 + inches) * 2.54
}

/**
 * Weight input, e.g. user's weight.
 *
 * @property id - unique identifier
 * @property label - label for the field
 * @property value - weight stored in kilograms internally; pounds input is converted on the way in. Defaults to null.
 * @property isValid - whether the weight is valid or not, based on specific criteria. Defaults to false.
 * @property isRequired - whether a value is required or not. Defaults to true.
 *
 * @constructor Create empty Weight input
 */
@Serializable
data class WeightInput(
    override val id: String,
    override val label: String,
    override val isRequired: Boolean = true,
    override val isValid: Boolean = false,

    override val value: Double? = null,
    val isKilos: Boolean = true
) : InputField {

    /**
     * Converts kilograms to pounds.
     *
     * @param kilos - weight in kg.
     * @return weight in pounds as Double.
     */
    fun kilosToPounds(kilos: Double = value ?: 0.0): Double = kilos * 2.2046
}

/**
 * Date input, e.g. date of birth.
 *
 * @property id - unique identifier
 * @property label - label for the field
 * @property value - selected date as [LocalDate], or null if no date has been picked yet.
 * @property isValid - whether the date is valid or not, based on specific criteria. Defaults to false.
 * @property isRequired - whether a value is required or not. Defaults to true.
 *
 * @constructor Create empty Date input
 */
@Serializable
data class DateInput(
    override val id: String,
    override val label: String,
    override val value: LocalDate? = null,
    override val isValid: Boolean = false
) : InputField