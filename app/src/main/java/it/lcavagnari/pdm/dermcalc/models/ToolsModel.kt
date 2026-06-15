@file:OptIn(kotlin.time.ExperimentalTime::class)

package it.lcavagnari.pdm.dermcalc.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.lcavagnari.pdm.dermcalc.data.ToolResultDao
import it.lcavagnari.pdm.dermcalc.data.ToolResultEntity
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Clinical severity tier used to color-code tool results throughout the app.
 */
enum class Severity { NONE, MILD, MODERATE, SEVERE }


/**
 * Base type for region scoring in index tools.
 * 
 * Represents clinical assessment of a body region with erythema, induration, and area measurements.
 * 
 * @property erythema erythema severity score (0-4 for PASI, 0-3 for EASI)
 * @property induration induration severity score (0-4 for PASI, 0-3 for EASI)
 * @property area area percentage (0-100)
 * @property areaToScore computed area weighting factor for score calculation
 */
@Serializable
sealed interface RegionScore {
    val erythema: Int
    val induration: Int
    val area: Int

    val areaToScore: Double
        get() = if (area == 0) 0.0
        else ((area + 10) / 20 + 1).coerceAtMost(6).toDouble()
}


/**
 * EASI (Eczema Area and Severity Index) score for a single body region.
 * 
 * @property erythema erythema severity (0-3)
 * @property induration induration severity (0-3)
 * @property area area percentage (0-100)
 * @property excoriation excoriation severity (0-3)
 * @property lichenification lichenification severity (0-3)
 */
@Serializable
data class EasiScore(
    override val erythema: Int = 0,
    override val induration: Int = 0,
    override val area: Int = 0,
    val excoriation: Int = 0,
    val lichenification: Int = 0
) : RegionScore


/**
 * PASI (Psoriasis Area and Severity Index) score for a single body region.
 * 
 * @property erythema erythema severity (0-4)
 * @property induration induration severity (0-4)
 * @property area area percentage (0-100)
 * @property desquamation scaling severity (0-4)
 */
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
    val id: Long   // NEW — default 0 for new results
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
    override val timestamp: LocalDateTime = today(),
    override val id: Long = 0   // NEW
) : ToolResult {
    override val name: String = "PASI"

    override fun isValid(): Boolean =
        listOf(head, upperLimbs, trunk, lowerLimbs)
            .all { region ->
                listOf(region.erythema, region.induration, region.area).all { it >= 0 }
            }
    companion object {
        /**
         * Computes a PASI result from four region scores.
         * 
         * @param head head region score
         * @param upperLimbs upper limbs region score
         * @param trunk trunk region score
         * @param lowerLimbs lower limbs region score
         * @return computed [PasiResult]
         */
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
    override val timestamp: LocalDateTime = today(),
    override val id: Long = 0   // NEW
) : ToolResult {
    override val name: String = "EASI"

    override fun isValid(): Boolean =
        listOf(head, upperLimbs, trunk, lowerLimbs)
            .all { region ->
                listOf(region.erythema, region.induration, region.area).all { it >= 0 }
            }

    companion object {
        /**
         * Computes an EASI result from four region scores.
         * 
         * @param head head region score
         * @param upperLimbs upper limbs region score
         * @param trunk trunk region score
         * @param lowerLimbs lower limbs region score
         * @return computed [EasiResult]
         */
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
    override val id: Long = 0   // NEW
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
    override val id: Long = 0   // NEW
) : ToolResult {
    override val name: String = "BSA"
    override fun isValid(): Boolean = affectedPercentage in 0.1..100.0

    companion object {
        /**
         * Computes a BSA result from region severity values.
         * 
         * @param regionValues mapping of body regions to severity scores (0-3)
         * @return computed [BsaResult]
         */
        fun compute(regionValues: Map<BodyRegion, Int>): BsaResult {
            val total = BodyRegion.entries.sumOf { region ->
                (regionValues[region] ?: 0) * region.bodyWeight
            }
            return BsaResult(affectedPercentage = total, score = total)
        }
    }
}


/**
 * Draft state for index tool calculations (PASI or EASI).
 * 
 * @property result the computed result for the current session
 * @property startPage starting page in the multi-page index tool
 * @property values mapping of region indices to their scores
 */
data class IndexToolDraft<Tool : ToolResult>(
    var result: Tool? = null,
    var startPage: Int = 0,
    val values: MutableMap<Int, RegionScore> = mutableMapOf()
) {
    /**
     * Initializes PASI scores for the specified number of pages.
     * 
     * @param pages number of pages to initialize
     */
    fun initPasi(pages: Int) {
        for (i in 0 until pages) values[i] = PasiScore()
    }

    /**
     * Initializes EASI scores for the specified number of pages.
     * 
     * @param pages number of pages to initialize
     */
    fun initEasi(pages: Int) {
        for (i in 0 until pages) values[i] = EasiScore()
    }

    /**
     * Resets the draft state to initial values.
     */
    fun reset() {
        result = null
        startPage = 0
        values.clear()
    }
}

/**
 * ViewModel that holds the in-memory list of [ToolResult] entries for the current session.
 * 
 * @param toolResultDao the [ToolResultDao] for database operations
 */
class ToolsModel(private val toolResultDao: ToolResultDao) : ViewModel() {
    private val _results = MutableStateFlow<List<ToolResult>>(emptyList())

    /**
     * Ordered list of all stored results; updated by [addResult] and [deleteResult].
     */
    val toolsResult: StateFlow<List<ToolResult>> = _results.asStateFlow()

    init {
        // Load persisted results on creation
        viewModelScope.launch {
            toolResultDao.getAll().collect { entities ->
                _results.value = entities.mapNotNull { entity ->
                    try {
                        val decoded = Json.decodeFromString<ToolResult>(entity.detailsJson)
                        when (decoded) {
                            is PasiResult -> decoded.copy(id = entity.id)
                            is EasiResult -> decoded.copy(id = entity.id)
                            is BmiResult  -> decoded.copy(id = entity.id)
                            is BsaResult  -> decoded.copy(id = entity.id)
                        }
                    } catch (e: Exception) {
                        null // Skip deserialization failures
                    }
                }
            }
        }
    }

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

    /**
     * Retrieves the EASI score for the specified region index.
     * 
     * @param region region index (0=head, 1=upperLimbs, 2=trunk, 3=lowerLimbs)
     * @return the [EasiScore] for the region, or a default empty score if not found
     */
    fun easiRegionScore(region: Int): EasiScore {
        return (_easiDraft.value.values[region] as? EasiScore) ?: EasiScore()
    }

    /**
     * Retrieves the PASI score for the specified region index.
     * 
     * @param region region index (0=head, 1=upperLimbs, 2=trunk, 3=lowerLimbs)
     * @return the [PasiScore] for the region, or a default empty score if not found
     */
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
    /**
     * Initializes PASI draft state with the specified number of pages.
     * 
     * @param pages number of pages to initialize
     */
    fun initPasiDraft(pages: Int) { _pasiDraft.value.initPasi(pages) }

    /**
     * Initializes EASI draft state with the specified number of pages.
     * 
     * @param pages number of pages to initialize
     */
    fun initEasiDraft(pages: Int) { _easiDraft.value.initEasi(pages) }

    /**
     * Updates a PASI region score and recomputes the total.
     * 
     * @param region region index (0=head, 1=upperLimbs, 2=trunk, 3=lowerLimbs)
     * @param score the new [PasiScore] for the region
     * @param page the page number where this score was entered
     */
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

    /**
     * Updates an EASI region score and recomputes the total.
     * 
     * @param region region index (0=head, 1=upperLimbs, 2=trunk, 3=lowerLimbs)
     * @param score the new [EasiScore] for the region
     * @param page the page number where this score was entered
     */
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

    /**
     * Resets the PASI draft state to initial values.
     */
    fun resetPasiDraft() {
        _pasiDraft.value = IndexToolDraft()
    }

    fun resetEasiDraft() {
        _easiDraft.value = IndexToolDraft()
    }

    /**
     * Saves the current PASI draft result to storage.
     * 
     * @return true if the result was saved, false if no result exists
     */
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
        viewModelScope.launch {
            try {
                val json = Json.encodeToString(ToolResult.serializer(), result)
                val entity = ToolResultEntity(
                    toolName = result.name,
                    score = result.score,
                    detailsJson = json
                )
                toolResultDao.upsert(entity)
            } catch (e: Exception) {
                // Insert failed silently
            }
        }
        return true
    }

    /**
     * Removes [result] from the stored list by equality.
     *
     * @param result the [ToolResult] instance to remove.
     */
    fun deleteResult(result: ToolResult) {
        viewModelScope.launch {
            toolResultDao.deleteById(result.id)
        }
    }

    /**
     * Removes all stored results.
     */
    fun clearAllResults() {
        viewModelScope.launch {
            toolResultDao.deleteAll()
        }
    }
}




