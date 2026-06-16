package it.lcavagnari.pdm.dermcalc.data


import androidx.annotation.StringRes
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.math.floor
import kotlin.time.ExperimentalTime


/** Converts this [LocalDate] to epoch milliseconds in UTC, for use with Material3 date APIs. @return Epoch milliseconds. */
@OptIn(ExperimentalTime::class)
fun LocalDate.toEpochMillis(): Long {
    return atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}

/** Converts epoch milliseconds to a [LocalDate] in UTC. @return The corresponding [LocalDate]. */
@OptIn(ExperimentalTime::class)
fun Long.toLocalDate(): LocalDate {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
}

/**
 * Returns true when the field still holds its default/unset value.
 * Used to show a subtle hint on the edit icon.
 */
fun InputField.isDefaultOrBlank(): Boolean = when (this) {
    is TextInput   -> value.isBlank()
    is DateInput   -> value == null
    is SexInput    -> value == Sex.Other
    is HeightInput -> value == null
    is WeightInput -> value == null
}

/**
 * Biological sex of the patient.
 */
@Serializable
enum class Sex { Male, Female, Other }

/**
 * Unit system used to express height.
 */
@Serializable
enum class HeightMeasurements { Metric, Imperial }


/**
 * Unit system used to express weight.
 */
@Serializable
enum class WeightMeasurements { Kilos, Pounds }

/*   ---------    */

/**
 * Interface for all onboarding input types.
 *
 * @property id - unique identifier
 * @property label - string resource id for the field label, resolved at render time via [stringResource].
 * @property value - default value of the field, type-safe, allows to read the value generically without knowing the subtype.
 * @property isValid - whether the value in the field is valid or not, based on specific criteria. Defaults to false.
 * @property isRequired - whether a value is required or not. Defaults to true.
 */
@Serializable
sealed interface InputField {
    val id: String
    @get:StringRes val label: Int
    val isRequired: Boolean get() = true
    val isValid: Boolean get() = false

    val value: Any? get() = null
}


/**
 * Text-based input, e.g. name, address, etc.
 *
 * @property value - default value of the field, type-safe. Here an empty string.
 *
 * @constructor Create empty Text input
 */
@Serializable
data class TextInput(
    override val id: String,
    @StringRes override val label: Int,
    override val value: String = "",
    override val isValid: Boolean = false
) : InputField

/**
 * Sex/gender input, e.g. biological sex of the patient.
 * Supports three values: Male, Female, Other.
 *
 * @property value - selected sex. Defaults to [Sex.Other].
 *
 * @constructor Create empty Sex input
 */
@Serializable
data class SexInput(
    override val id: String,
    @StringRes override val label: Int,
    override val isValid: Boolean = false,
    override val isRequired: Boolean = false,

    override val value: Sex? = Sex.Other

) : InputField

/**
 * Height input, e.g. user's height
 * Based on the metric for the tallest(272cm) and shortest(50cm) person ever lived.
 *
 * @property value - height stored in centimetres. Defaults to null.
 * @property isMetric - whether the user prefers metric units.
 *
 * @constructor Create empty Height input
 */
@Serializable
data class HeightInput(
    override val id: String,
    @StringRes override val label: Int,
    override val isRequired: Boolean = true,
    override val isValid: Boolean = false,

    override val value: Double? = null,
    val isMetric: Boolean = true

) : InputField {

    /**
     * Converts height cm to feet and inches.
     *
     * @param cm height in cm.
     * @return Height in imperial as a (feet, inches) [Pair].
     */
    fun cmToFeetInches(cm: Double = value ?: 0.0): Pair<Double, Double> {
        val totalInches = cm / 2.54
        val feet = floor(totalInches / 12.0)
        val inches = totalInches - feet * 12.0
        return feet to inches
    }

    /**
     * Converts feet and inches to centimetres.
     *
     * @param feet height in feet.
     * @param inches remaining height in inches.
     * @return Height in centimetres.
     */
    fun feetInchesToCm(feet: Int, inches: Int): Double =
        (feet * 12 + inches) * 2.54
}

/**
 * Weight input, e.g. user's weight.
 *
 * @property value - weight stored in kilograms internally; pounds input is converted on the way in. Defaults to null.
 * @property isKilos - whether the user prefers kilograms.
 *
 * @constructor Create empty Weight input
 */
@Serializable
data class WeightInput(
    override val id: String,
    @StringRes override val label: Int,
    override val isRequired: Boolean = true,
    override val isValid: Boolean = false,

    override val value: Double? = null,
    val isKilos: Boolean = true
) : InputField {

    /**
     * Converts kilograms to pounds.
     *
     * @param kilos weight in kg.
     * @return Weight in pounds.
     */
    fun kilosToPounds(kilos: Double = value ?: 0.0): Double = kilos * 2.2046

    /**
     * Converts pounds to kilograms.
     *
     * @param pounds weight in pounds.
     * @return Weight in kilograms.
     */
    fun poundsToKilos(pounds: Int): Double = pounds / 2.2046
}

/**
 * Date input, e.g. date of birth.
 *
 * @property value - selected date as [LocalDate], or null if no date has been picked yet.
 *
 * @constructor Create empty Date input
 */
@Serializable
data class DateInput(
    override val id: String,
    @StringRes override val label: Int,
    override val value: LocalDate? = null,
    override val isValid: Boolean = false
) : InputField