package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Maps this [ToolResult] to its clinical [Severity] tier using per-tool thresholds:
 * - PASI: < 10 Mild, < 20 Moderate, else Severe
 * - EASI: < 7 Mild, < 21 Moderate, else Severe
 * - BMI: < 18.5 Severe (underweight), < 25 Mild, < 30 Moderate, else Severe
 * - BSA: < 10 Mild, < 30 Moderate, else Severe
 */
fun ToolResult.severity(): Severity = when (this) {
    is PasiResult -> when {
        score < 10.0 -> Severity.Mild
        score < 20.0 -> Severity.Moderate
        else -> Severity.Severe
    }

    is EasiResult -> when {
        score < 7.0 -> Severity.Mild
        score < 21.0 -> Severity.Moderate
        else -> Severity.Severe
    }

    is BmiResult -> when {
        score < 18.5 -> Severity.Severe
        score < 25.0 -> Severity.Mild
        score < 30.0 -> Severity.Moderate
        else -> Severity.Severe
    }

    is BsaResult -> when {
        score < 10.0 -> Severity.Mild
        score < 30.0 -> Severity.Moderate
        else -> Severity.Severe
    }
}

/** Formats this result's score: zero decimals for whole numbers, one decimal otherwise. */
fun ToolResult.formattedScore(): String =
    if (score % 1.0 == 0.0) "%.0f".format(score) else "%.1f".format(score)

/**
 * Base type for all calculator results stored in [ToolsModel].
 *
 * Every subtype must pass [isValid] before [ToolsModel.addResult] will accept it.
 *
 * @property name - human-readable tool identifier (e.g. "PASI", "BMI").
 * @property score - computed clinical score for this result.
 * @property timestamp - date/time the result was recorded; defaults to the current instant via [today].
 */
@Serializable
sealed interface ToolResult {
    val name: String
    val score: Double
    val timestamp: LocalDateTime
        get() = today()

    fun isValid(): Boolean
}



/**
 * PASI (Psoriasis Area and Severity Index) result.
 *
 * Symptom scores (erythema, induration, scaling) must each be in **0–4**.
 * Area percentages (headArea, trunkArea, upperLimbsArea, lowerLimbsArea) must each be in **0–100**.
 */
@Serializable
@SerialName("pasi")
data class PasiResult(
    val headErythema: Double,
    val headInduration: Double,
    val headScaling: Double,
    val headArea: Double,
    val trunkErythema: Double,
    val trunkInduration: Double,
    val trunkScaling: Double,
    val trunkArea: Double,
    val upperLimbsErythema: Double,
    val upperLimbsInduration: Double,
    val upperLimbsScaling: Double,
    val upperLimbsArea: Double,
    val lowerLimbsErythema: Double,
    val lowerLimbsInduration: Double,
    val lowerLimbsScaling: Double,
    val lowerLimbsArea: Double,
    override val score: Double,
    override val timestamp: LocalDateTime = today(),
) : ToolResult {
    override val name: String = "PASI"
    override fun isValid(): Boolean =
        listOf(
            headErythema, headInduration, headScaling,
            trunkErythema, trunkInduration, trunkScaling,
            upperLimbsErythema, upperLimbsInduration, upperLimbsScaling,
            lowerLimbsErythema, lowerLimbsInduration, lowerLimbsScaling,
        ).all { it in 0.0..4.0 } &&
                listOf(
                    headArea, trunkArea, upperLimbsArea, lowerLimbsArea,
                ).all { it in 0.0..100.0 }
}

/**
 * EASI (Eczema Area and Severity Index) result.
 *
 * Symptom scores (erythema, induration, excoriation, lichenification) must each be in **0–3**.
 * Area percentages (headArea, trunkArea, upperLimbsArea, lowerLimbsArea) must each be in **0–100**.
 */
@Serializable
@SerialName("easi")
data class EasiResult(
    val headErythema: Double,
    val headInduration: Double,
    val headExcoriation: Double,
    val headLichenification: Double,
    val headArea: Double,
    val trunkErythema: Double,
    val trunkInduration: Double,
    val trunkExcoriation: Double,
    val trunkLichenification: Double,
    val trunkArea: Double,
    val upperLimbsErythema: Double,
    val upperLimbsInduration: Double,
    val upperLimbsExcoriation: Double,
    val upperLimbsLichenification: Double,
    val upperLimbsArea: Double,
    val lowerLimbsErythema: Double,
    val lowerLimbsInduration: Double,
    val lowerLimbsExcoriation: Double,
    val lowerLimbsLichenification: Double,
    val lowerLimbsArea: Double,
    override val score: Double,
    override val timestamp: LocalDateTime = today(),
) : ToolResult {
    override val name: String = "EASI"
    override fun isValid(): Boolean =
        listOf(
            headErythema, headInduration, headExcoriation, headLichenification,
            trunkErythema, trunkInduration, trunkExcoriation, trunkLichenification,
            upperLimbsErythema, upperLimbsInduration, upperLimbsExcoriation, upperLimbsLichenification,
            lowerLimbsErythema, lowerLimbsInduration, lowerLimbsExcoriation, lowerLimbsLichenification,
        ).all { it in 0.0..3.0 } &&
                listOf(
                    headArea, trunkArea, upperLimbsArea, lowerLimbsArea,
                ).all { it in 0.0..100.0 }
}

/**
 * BMI (Body Mass Index) result.
 *
 * Both [weightKg] and [heightCm] must be strictly positive for [isValid] to return true.
 */
@Serializable
@SerialName("bmi")
data class BmiResult(
    val weightKg: Double,
    val heightCm: Double,
    override val score: Double,
    override val timestamp: LocalDateTime = today(),
) : ToolResult {
    override val name: String = "BMI"
    override fun isValid(): Boolean = weightKg > 0.0 && heightCm > 0.0
}

/**
 * BSA (Body Surface Area) result.
 *
 * [affectedPercentage] must be in the range **0–100** for [isValid] to return true.
 */
@Serializable
@SerialName("bsa")
data class BsaResult(
    val affectedPercentage: Double,
    override val score: Double,
    override val timestamp: LocalDateTime = today(),
) : ToolResult {
    override val name: String = "BSA"
    override fun isValid(): Boolean = affectedPercentage in 0.0..100.0
}

/** ViewModel that holds the in-memory list of [ToolResult] entries for the current session. */
class ToolsModel(application: Application) : AndroidViewModel(application) {
    private val _results = MutableStateFlow<List<ToolResult>>(emptyList())
    /** Ordered list of all stored results; updated by [addResult] and [deleteResult]. */
    val toolsResult: StateFlow<List<ToolResult>> = _results.asStateFlow()

    // --- BMI calculator ephemeral state ---

    private val _bmiHeight =
        MutableStateFlow(HeightInput(id = "height", label = R.string.label_height))
    private val _bmiWeight =
        MutableStateFlow(WeightInput(id = "weight", label = R.string.label_weight))

    /** Current height input for the BMI calculator. */
    val bmiHeight: StateFlow<HeightInput> = _bmiHeight.asStateFlow()

    /** Current weight input for the BMI calculator. */
    val bmiWeight: StateFlow<WeightInput> = _bmiWeight.asStateFlow()

    /** Live BMI result derived from the current inputs; null when either input value is absent. */
    private var lastValidBmi: BmiResult? = null

    val bmiResult: StateFlow<BmiResult?> = combine(_bmiHeight, _bmiWeight) { h, w ->
        val heightCm = h.value
        val weightKg = w.value
        if (heightCm != null && weightKg != null) {
            val hm = heightCm / 100.0
            lastValidBmi = BmiResult(weightKg, heightCm, weightKg / (hm * hm))
        }
        lastValidBmi
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Formatted BMI score string; "--" when no valid score has been computed yet. */
    val bmiFormattedScore: StateFlow<String> = bmiResult
        .map { it?.formattedScore() ?: "--" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "--")

    /** Clinical severity for the current BMI result; null when no valid score has been computed yet. */
    val bmiSeverity: StateFlow<Severity?> = bmiResult
        .map { it?.severity() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Seeds the BMI calculator with the user's stored profile values. */
    fun initBmi(height: HeightInput, weight: WeightInput) {
        _bmiHeight.value = height
        _bmiWeight.value = weight
    }

    fun updateBmiHeightMetric(cm: Int) {
        _bmiHeight.update { it.copy(value = cm.toDouble(), isValid = cm in 50..272) }
    }

    fun updateBmiHeightImperial(feet: Int, inches: Int) {
        _bmiHeight.update {
            val cm = it.feetInchesToCm(feet, inches)
            it.copy(value = cm, isValid = cm in 19.68..1207.08)
        }
    }

    fun updateBmiWeightKilos(kg: Int) {
        _bmiWeight.update { it.copy(value = kg.toDouble(), isValid = kg in 20..300) }
    }

    fun updateBmiWeightPounds(lb: Int) {
        _bmiWeight.update { it.copy(value = it.poundsToKilos(lb), isValid = lb in 44..661) }
    }

    // --- Result storage ---

    /**
     * Validates and appends [result] to the stored list.
     *
     * @param result the [ToolResult] to add; must pass [ToolResult.isValid].
     * @return true if the result was added, false if validation failed.
     */
    fun addResult(result: ToolResult): Boolean {
        if (!result.isValid()) return false
        _results.update { it + result }
        return true
    }

    /**
     * Removes [result] from the stored list by equality.
     *
     * @param result the [ToolResult] instance to remove.
     */
    fun deleteResult(result: ToolResult) {
        _results.update { current ->
            current.filter { it != result }
        }
    }

    /** Removes all stored results. */
    fun clearResult() {
        _results.update { emptyList() }
    }
}
