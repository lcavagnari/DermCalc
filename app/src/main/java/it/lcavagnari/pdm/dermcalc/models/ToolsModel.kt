package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ToolResult {
    val name: String
    val score: Double
    val timestamp: LocalDateTime
        get() = today()

    fun isValid(): Boolean
}



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

class ToolsModel(application: Application) : AndroidViewModel(application) {
    private val _results = MutableStateFlow<List<ToolResult>>(emptyList())
    val toolsResult: StateFlow<List<ToolResult>> = _results.asStateFlow()

    fun addResult(result: ToolResult): Boolean {
        if (!result.isValid()) return false
        _results.update { it + result }
        return true
    }

    fun deleteResult(result: ToolResult) {
        _results.update { current ->
            current.filter { it != result }
        }
    }
}