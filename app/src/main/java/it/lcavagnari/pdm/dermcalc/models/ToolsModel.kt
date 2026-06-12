package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.sqrt

/**
 * Clinical severity tier used to color-code tool results throughout the app.
 */
enum class Severity { NONE ,MILD, MODERATE, SEVERE }

/**
 * Maps this [ToolResult] to its clinical [Severity] tier using per-tool thresholds:
 * - PASI: < 10 Mild, < 20 Moderate, else Severe
 * - EASI: < 7 Mild, < 21 Moderate, else Severe
 * - BMI: < 18.5 Severe (underweight), < 25 Mild, < 30 Moderate, else Severe
 * - BSA: < 10 Mild, < 30 Moderate, else Severe
 */
fun ToolResult.severity(): Severity = when (this) {
    is PasiResult -> when {
        score < 10.0 -> Severity.MILD
        score < 20.0 -> Severity.MODERATE
        else -> Severity.SEVERE
    }

    is EasiResult -> when {
        score < 7.0 -> Severity.MILD
        score < 21.0 -> Severity.MODERATE
        else -> Severity.SEVERE
    }

    is BmiResult -> when {
        score < 18.5 -> Severity.SEVERE
        score < 25.0 -> Severity.MILD
        score < 30.0 -> Severity.MODERATE
        else -> Severity.SEVERE
    }

    is BsaResult -> when {
        score < 10.0 -> Severity.MILD
        score < 30.0 -> Severity.MODERATE
        else -> Severity.SEVERE
    }
}
/** Formats this result's score: zero decimals for whole numbers, one decimal otherwise. */
fun ToolResult.formattedScore(): String {
    val rounded = Math.round(score * 10) / 10.0
    return if (rounded % 1.0 == 0.0) "%.0f".format(rounded) else "%.1f".format(rounded)
}

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

    companion object {
        /**
         * Computes BMI from raw measurements and returns a [BmiResult].
         *
         * Formula: weightKg / (heightMetres)²
         *
         * @param weightKg body weight in kilograms (must be > 0 for [isValid]).
         * @param heightCm body height in centimetres (must be > 0 for [isValid]).
         */
        fun compute(weightKg: Double, heightCm: Double): BmiResult {
            val heightM = heightCm / 100.0
            val bmi = weightKg / (heightM * heightM)
            return BmiResult(weightKg = weightKg, heightCm = heightCm, score = bmi)
        }
    }
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
    override fun isValid(): Boolean = affectedPercentage in 0.1..100.0

    companion object {
        fun compute(regionValues: Map<BsaRegion, Int>): BsaResult {
            val total = BsaRegion.entries.sumOf { region ->
                (regionValues[region] ?: 0) * region.bodyWeight
            }
            return BsaResult(affectedPercentage = total, score = total)
        }
    }
}




/** ViewModel that holds the in-memory list of [ToolResult] entries for the current session. */
class ToolsModel(application: Application) : AndroidViewModel(application) {
    private val _results = MutableStateFlow<List<ToolResult>>(emptyList())
    /** Ordered list of all stored results; updated by [addResult] and [deleteResult]. */
    val toolsResult: StateFlow<List<ToolResult>> = _results.asStateFlow()

    // --- Draft State (Persistence across navigation) ---

    private val _pasiDraftScore = MutableStateFlow(0.0)
    val pasiDraftScore: StateFlow<Double> = _pasiDraftScore.asStateFlow()
    private val _pasiDraftPage = MutableStateFlow(0)
    val pasiDraftPage: StateFlow<Int> = _pasiDraftPage.asStateFlow()

    private val _easiDraftScore = MutableStateFlow(0.0)
    val easiDraftScore: StateFlow<Double> = _easiDraftScore.asStateFlow()
    private val _easiDraftPage = MutableStateFlow(0)
    val easiDraftPage: StateFlow<Int> = _easiDraftPage.asStateFlow()

    fun updatePasiDraft(score: Double, page: Int) {
        _pasiDraftScore.value = score
        _pasiDraftPage.value = page
    }

    fun resetPasiDraft() {
        _pasiDraftScore.value = 0.0
        _pasiDraftPage.value = 0
    }

    fun updateEasiDraft(score: Double, page: Int) {
        _easiDraftScore.value = score
        _easiDraftPage.value = page
    }

    fun resetEasiDraft() {
        _easiDraftScore.value = 0.0
        _easiDraftPage.value = 0
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
        _results.update { current -> current.filter { it != result } }
    }

    /** Removes all stored results. */
    fun clearResult() { _results.update { emptyList() } }
}

