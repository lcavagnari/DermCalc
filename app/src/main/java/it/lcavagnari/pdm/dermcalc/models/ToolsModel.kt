package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.lcavagnari.pdm.dermcalc.utils.today
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Clinical severity tier used to color-code tool results throughout the app.
 */
enum class Severity { NONE, MILD, MODERATE, SEVERE }


@Serializable
sealed interface RegionScore {
    val erythema: Int
    val induration: Int
    val area: Int

    val areaToScore: Double
        get() = if (area == 0) 0.0
        else ((area + 10) / 20 + 1).coerceAtMost(6).toDouble()
}


@Serializable
data class EasiScore(
    override val erythema: Int = 0,
    override val induration: Int = 0,
    override val area: Int = 0,
    val excoriation: Int = 0,
    val lichenification: Int = 0
) : RegionScore


@Serializable
data class PasiScore(
    override val erythema: Int = 0,
    override val induration: Int = 0,
    override val area: Int = 0,
    val desquamation: Int = 0
) : RegionScore {
}



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
    val head: PasiScore,
    val upperLimbs: PasiScore,
    val trunk: PasiScore,
    val lowerLimbs: PasiScore,
    override val score: Double,
    override val timestamp: LocalDateTime = today()
) : ToolResult {
    override val name: String = "PASI"

    override fun isValid(): Boolean =
        listOf(head, upperLimbs, trunk, lowerLimbs)
            .all { region ->
                listOf(region.erythema, region.induration, region.area).all { it >= 0 }
            }
    companion object{
        fun compute(
            head: PasiScore,
            upperLimbs: PasiScore,
            trunk: PasiScore,
            lowerLimbs: PasiScore,
        ): PasiResult {
            val score =
                0.1 * head.areaToScore * (head.erythema + head.induration + head.desquamation) +
                        0.2 * upperLimbs.areaToScore * (upperLimbs.erythema + upperLimbs.induration + upperLimbs.desquamation) +
                        0.3 * trunk.areaToScore * (trunk.erythema + trunk.induration + trunk.desquamation) +
                        0.4 * lowerLimbs.areaToScore * (lowerLimbs.erythema + lowerLimbs.induration + lowerLimbs.desquamation)

            return PasiResult(
                head, upperLimbs, trunk, lowerLimbs,
                score = score
            )
        }
    }
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
    val head: EasiScore,
    val upperLimbs: EasiScore,
    val trunk: EasiScore,
    val lowerLimbs: EasiScore,
    override val score: Double,
    override val timestamp: LocalDateTime = today()
) : ToolResult {
    override val name: String = "EASI"

    override fun isValid(): Boolean =
        listOf(head, upperLimbs, trunk, lowerLimbs)
            .all { region ->
                listOf(region.erythema, region.induration, region.area).all { it >= 0 }
            }

    companion object {
        fun compute(
            head: EasiScore,
            upperLimbs: EasiScore,
            trunk: EasiScore,
            lowerLimbs: EasiScore,
        ): EasiResult {

            val score =
                0.1 * head.areaToScore * (head.erythema + head.induration + head.excoriation + head.lichenification) +
                        0.2 * upperLimbs.areaToScore * (upperLimbs.erythema + upperLimbs.induration + upperLimbs.excoriation + upperLimbs.lichenification) +
                        0.3 * trunk.areaToScore * (trunk.erythema + trunk.induration + trunk.excoriation + trunk.lichenification) +
                        0.4 * lowerLimbs.areaToScore * (lowerLimbs.erythema + lowerLimbs.induration + lowerLimbs.excoriation + lowerLimbs.lichenification)

            return EasiResult(
                head, upperLimbs, trunk, lowerLimbs,
                score = score
            )
        }
    }
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
        fun compute(regionValues: Map<BodyRegion, Int>): BsaResult {
            val total = BodyRegion.entries.sumOf { region ->
                (regionValues[region] ?: 0) * region.bodyWeight
            }
            return BsaResult(affectedPercentage = total, score = total)
        }
    }
}


data class IndexToolDraft<Tool : ToolResult>(
    var result: Tool? = null,
    var startPage: Int = 0,
    val values: MutableMap<Int, RegionScore> = mutableMapOf()
) {
    fun initPasi(pages: Int) {
        for (i in 0 until pages) values[i] = PasiScore()
    }

    fun initEasi(pages: Int) {
        for (i in 0 until pages) values[i] = EasiScore()
    }

    fun reset() {
        result = null
        startPage = 0
        values.clear()
    }
}

/** ViewModel that holds the in-memory list of [ToolResult] entries for the current session. */
class ToolsModel(application: Application) : AndroidViewModel(application) {
    private val _results = MutableStateFlow<List<ToolResult>>(emptyList())

    /** Ordered list of all stored results; updated by [addResult] and [deleteResult]. */
    val toolsResult: StateFlow<List<ToolResult>> = _results.asStateFlow()

    // --- Draft State (Persistence across navigation) ---
    private val _pasiDraft = MutableStateFlow(IndexToolDraft<PasiResult>())
    val pasiDraftScore: Double
        get() = _pasiDraft.value.result?.score ?: 0.0

    val pasiDraftPage: Int
        get() = _pasiDraft.value.startPage

    private val _easiDraft = MutableStateFlow(IndexToolDraft<EasiResult>())
    val easiDraftScore: Double
        get() = _easiDraft.value.result?.score ?: 0.0

    val easiDraftStartPage: Int
        get() = _easiDraft.value.startPage

    fun easiRegionScore(region: Int): EasiScore {
        return (_easiDraft.value.values[region] as? EasiScore) ?: EasiScore()
    }

    fun pasiRegionScore(region: Int): PasiScore {
        return (_pasiDraft.value.values[region] as? PasiScore) ?: PasiScore()
    }

    /** Reactive stream of the computed PASI score — bridges StateFlow → Compose reactivity. */
    val pasiScore: StateFlow<Double> = _pasiDraft
        .map { it.result?.score ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    /** Reactive boolean: true if any region in the PASI draft has a non-zero field value. */
    val pasiHasData: StateFlow<Boolean> = _pasiDraft
        .map { draft ->
            draft.values.any { (_, v) ->
                val p = v as? PasiScore ?: return@any false
                p.erythema > 0 || p.induration > 0 || p.desquamation > 0 || p.area > 0
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    /** Reactive stream of the computed EASI score — bridges StateFlow → Compose reactivity. */
    val easiScore: StateFlow<Double> = _easiDraft
        .map { it.result?.score ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    /** Reactive boolean: true if any region in the EASI draft has a non-zero field value. */
    val easiHasData: StateFlow<Boolean> = _easiDraft
        .map { draft ->
            draft.values.any { (_, v) ->
                val e = v as? EasiScore ?: return@any false
                e.erythema > 0 || e.induration > 0 || e.excoriation > 0 || e.lichenification > 0 || e.area > 0
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    // TODO: Have it reviewed
    fun initPasiDraft(pages: Int) { _pasiDraft.value.initPasi(pages) }
    fun initEasiDraft(pages: Int) { _easiDraft.value.initEasi(pages) }

    fun updatePasiDraft(region: Int, score: PasiScore, page: Int) {
        val old = _pasiDraft.value
        val newValues = old.values.toMutableMap().apply { this[region] = score }

        val result = PasiResult.compute(
            head = newValues[0] as? PasiScore ?: PasiScore(),
            upperLimbs = newValues[1] as? PasiScore ?: PasiScore(),
            trunk = newValues[2] as? PasiScore ?: PasiScore(),
            lowerLimbs = newValues[3] as? PasiScore ?: PasiScore()
        )

        _pasiDraft.value = old.copy(
            values = newValues,
            startPage = page,
            result = result
        )
    }

    fun updateEasiDraft(region: Int, score: EasiScore, page: Int) {
        val old = _easiDraft.value
        val newValues = old.values.toMutableMap().apply { this[region] = score }

        val result = EasiResult.compute(
            head = newValues[0] as? EasiScore ?: EasiScore(),
            upperLimbs = newValues[1] as? EasiScore ?: EasiScore(),
            trunk = newValues[2] as? EasiScore ?: EasiScore(),
            lowerLimbs = newValues[3] as? EasiScore ?: EasiScore()
        )

        _easiDraft.value = old.copy(
            values = newValues,
            startPage = page,
            result = result
        )
    }

    fun resetPasiDraft() {
        _pasiDraft.value = IndexToolDraft()
    }

    fun resetEasiDraft() {
        _easiDraft.value = IndexToolDraft()
    }

    fun savePasiDraft(): Boolean {
        return addResult(_pasiDraft.value.result ?: return false)
    }

    fun saveEasiDraft(): Boolean {
        return addResult(_easiDraft.value.result ?: return false)
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
    fun clearResult() {
        _results.update { emptyList() }
    }
}