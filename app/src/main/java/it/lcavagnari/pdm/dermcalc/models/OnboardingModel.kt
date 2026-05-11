package it.lcavagnari.pdm.dermcalc.models

import androidx.lifecycle.ViewModel
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class OnboardingModel : ViewModel() {
    // In-memory only — not persisted. Process death (force-kill) resets this to false
    // and the user will see onboarding again on next launch.
    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    // Single source of truth for every onboarding field across all pages.
    // Pages reference fields by id; adding a new field here makes it available to any page.
    private val _fields = MutableStateFlow(
        listOf<InputField>(
            TextInput(id = "full-name", label = "Full name"),
            DateInput(id = "date-of-birth", label = "Date of birth"),
            SexInput(id = "sex", label = "Sex"),
            HeightInput(id = "height", label = "Height", isMetric = true),
            WeightInput(id = "weight", label = "Weight", isKilos = true)
        )
    )
    val fields: StateFlow<List<InputField>> = _fields.asStateFlow()

    fun finishOnboarding() {
        _hasSeenOnboarding.value = true
    }

    private fun getFieldById(id: String): InputField? = _fields.value.firstOrNull { it.id == id }

    fun isPageInputValid(
        fieldIds: List<String>,
        fields: List<InputField> = _fields.value
    ): Boolean {
        if (fieldIds.isEmpty()) return true
        return fieldIds.all { id ->
            val field = fields.firstOrNull { it.id == id } ?: return false
            !field.isRequired || field.isValid
        }
    }

    // Valid when the value contains at least two whitespace-separated words.
    fun updateName(value: String) {
        val isValid = value.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size >= 2
        _fields.value = _fields.value.map { field ->
            if (field is TextInput && field.id == "full-name")
                field.copy(value = value, isValid = if (field.isRequired) isValid else true)
            else field
        }
    }

    // Valid when the date is after 1900-01-01 and not in the future.
    fun updateDateOfBirth(value: LocalDate) {
        val today = today().date
        val epoch = LocalDate(1900, 1, 1)
        val isValid = value > epoch && value <= today
        _fields.value = _fields.value.map { field ->
            if (field is DateInput && field.id == "date-of-birth")
                field.copy(value = value, isValid = if (field.isRequired) isValid else true)
            else field
        }
    }

    fun updateSex(value: Sex) {
        _fields.value = _fields.value.map { field ->
            if (field is SexInput && field.id == "sex")
                field.copy(value = value, isValid = true)
            else field
        }
    }

    fun updateMeasurements(value: HeightMeasurements) {
        _fields.value = _fields.value.map { field ->
            if (field is HeightInput && field.id == "height")
                field.copy(isMetric = value == HeightMeasurements.Metric)
            else field
        }
    }

    fun updateMeasurements(value: WeightMeasurements) {
        _fields.value = _fields.value.map { field ->
            if (field is WeightInput && field.id == "weight")
                field.copy(isKilos = value == WeightMeasurements.Kilos)
            else field
        }
    }


    fun updateHeightMetric(cm: Int) {
        // Valid range: 50–272 cm (shortest/tallest recorded human heights).
        val isValid = cm in 50..272

        _fields.value = _fields.value.map { field ->
            if (field is HeightInput && field.id == "height")
                field.copy(value = cm.toDouble(), isValid = if (field.isRequired) isValid else true)
            else field
        }
    }

    fun updateHeightImperial(feet: Int, inches: Int) {
        _fields.value = _fields.value.map { field ->
            if (field is HeightInput && field.id == "height") {
                val cm = field.feetInchesToCm(feet, inches)
                field.copy(
                    value = cm,
                    isValid = if (field.isRequired) cm in 19.68..1207.08 else true
                )
            } else field
        }
    }

    fun updateWeightKilos(kilos: Int) {
        // Reasonable human weight range: 20-300 kg.
        val isValid = kilos in 20..300

        _fields.value = _fields.value.map { field ->
            if (field is WeightInput && field.id == "weight") {
                field.copy(
                    value = kilos.toDouble(),
                    isValid = !field.isRequired || (field.isRequired && isValid)
                )
            } else field
        }
    }

    fun updateWeightPounds(pounds: Int) {
        // 20 kg = 44.092 lb, 300 kg = 661.38 lb → rounded range 44..661
        val isValid = pounds in 44..661

        _fields.value = _fields.value.map { field ->
            if (field is WeightInput && field.id == "weight") {
                field.copy(
                    value = pounds / 2.2046,
                    isValid = !field.isRequired || (field.isRequired && isValid)
                )
            } else field
        }
    }
}