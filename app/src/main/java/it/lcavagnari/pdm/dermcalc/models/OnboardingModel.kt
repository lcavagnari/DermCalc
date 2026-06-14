package it.lcavagnari.pdm.dermcalc.models

import androidx.lifecycle.ViewModel
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.data.AppSettingsDao
import it.lcavagnari.pdm.dermcalc.data.UserProfileDao
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.datetime.LocalDate
import it.lcavagnari.pdm.dermcalc.data.UserProfileEntity
import it.lcavagnari.pdm.dermcalc.data.AppSettingsEntity

/** ViewModel holding onboarding state and all user-input update operations. */
class OnboardingModel(
    private val userProfileDao: UserProfileDao,
    private val appSettingsDao: AppSettingsDao
) : ViewModel() {

    private val _inputFields = MutableStateFlow<List<InputField>>(
        listOf<InputField>(
            TextInput(   id = "full-name",     label = R.string.label_full_name),
            DateInput(   id = "date-of-birth", label = R.string.label_date_of_birth),
            SexInput(    id = "sex",           label = R.string.label_sex,    value = Sex.Other),
            HeightInput( id = "height",        label = R.string.label_height, isMetric = true),
            WeightInput( id = "weight",        label = R.string.label_weight, isKilos = true)
        )
    )
    /** Ordered list of all onboarding [InputField] instances, updated in-place by the update methods. */
    val fields: StateFlow<List<InputField>> = _inputFields.asStateFlow()

    /** O(1) accessor for the [HeightInput] field; position 3 in the declared list is stable. */
    val heightInput: HeightInput get() = _inputFields.value[3] as HeightInput

    /** O(1) accessor for the [WeightInput] field; position 4 in the declared list is stable. */
    val weightInput: WeightInput get() = _inputFields.value[4] as WeightInput

    private val _hasSeenOnboarding = MutableStateFlow(false)

    /** Whether the user has completed the onboarding flow. In-memory only; resets on process death. */
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    init {
        // Load persisted profile once on creation.
        // Use first() instead of collect() to avoid a re-trigger loop:
        // Room → collect → updateName() → saveProfile() → Room → collect → ...
        viewModelScope.launch {
            val profile = userProfileDao.getProfile().first()
            if (profile != null) {
                if (!profile.fullName.isNullOrBlank()) {
                    _inputFields.value = _inputFields.value.map { field ->
                        if (field is TextInput && field.id == "full-name")
                            field.copy(value = profile.fullName, isValid = true)
                        else field
                    }
                }
                profile.dateOfBirth?.let { dob ->
                    _inputFields.value = _inputFields.value.map { field ->
                        if (field is DateInput && field.id == "date-of-birth")
                            field.copy(value = dob, isValid = true)
                        else field
                    }
                }
                profile.sex?.let { sex ->
                    _inputFields.value = _inputFields.value.map { field ->
                        if (field is SexInput && field.id == "sex")
                            field.copy(value = sex, isValid = true)
                        else field
                    }
                }
                if (profile.heightCm > 0) {
                    _inputFields.value = _inputFields.value.map { field ->
                        if (field is HeightInput && field.id == "height")
                            field.copy(value = profile.heightCm, isValid = true)
                        else field
                    }
                }
                if (profile.weightKg > 0) {
                    _inputFields.value = _inputFields.value.map { field ->
                        if (field is WeightInput && field.id == "weight")
                            field.copy(value = profile.weightKg, isValid = true)
                        else field
                    }
                }
            }
        }

        viewModelScope.launch {
            val settings = appSettingsDao.getSettings().first()
            if (settings != null) {
                _hasSeenOnboarding.value = settings.hasSeenOnboarding
            }
        }
    }

    // Methods

    /** Marks onboarding as complete; persists fields and settings to Room. */
    fun finishOnboarding() {
        _hasSeenOnboarding.value = true
        persistFields()
        viewModelScope.launch {
            val current = appSettingsDao.getSettings().firstOrNull()
            appSettingsDao.upsert(
                AppSettingsEntity(
                    id = 1,
                    isDarkTheme = current?.isDarkTheme ?: false,
                    hasSeenOnboarding = true
                )
            )
        }
    }

    /**
     * @param id id of the [InputField] to retrieve.
     * @return [InputField] instance with matching [id] or null.
     */
    private fun getFieldById(id: String): InputField? = _inputFields.value.firstOrNull { it.id == id }


    /**
     * Returns true when every required field in [fieldIds] has a valid value.
     *
     * @param fieldIds list of field ids for the current onboarding page.
     * @param fields snapshot of fields to validate against. Defaults to the current [fields] state.
     * @return true if all required fields are valid, or [fieldIds] is empty.
     */
    fun isFieldsInputValid(
        fieldIds: List<String>,
        fields: List<InputField> = _inputFields.value
    ): Boolean {
        if (fieldIds.isEmpty()) return true
        return fieldIds.all { id ->
            val field = fields.firstOrNull { it.id == id } ?: return false
            !field.isRequired || field.isValid
        }
    }

    /**
     * Updates the full name field.
     * Valid when the value contains at least two whitespace-separated words.
     *
     * In-memory only. Call [persistFields] to write to Room.
     *
     * @param value full name string entered by the user.
     */
    fun updateName(value: String) {
        val isValid = value.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size >= 2
        _inputFields.value = _inputFields.value.map { field ->
            if (field is TextInput && field.id == "full-name")
                field.copy(value = value, isValid = if (field.isRequired) isValid else true)
            else field
        }
    }

    /**
     * Updates the date of birth field.
     * Valid when the date is after 1900-01-01 and not in the future.
     *
     * In-memory only. Call [persistFields] to write to Room.
     *
     * @param value date of birth selected by the user.
     */
    fun updateDateOfBirth(value: LocalDate) {
        val today = today().date
        val epoch = LocalDate(1900, 1, 1)
        val isValid = value > epoch && value <= today
        _inputFields.value = _inputFields.value.map { field ->
            if (field is DateInput && field.id == "date-of-birth")
                field.copy(value = value, isValid = if (field.isRequired) isValid else true)
            else field
        }
    }

    /**
     * Updates the sex field.
     *
     * In-memory only. Call [persistFields] to write to Room.
     *
     * @param value selected [Sex] enum value.
     */
    fun updateSex(value: Sex) {
        _inputFields.value = _inputFields.value.map { field ->
            if (field is SexInput && field.id == "sex")
                field.copy(value = value, isValid = true)
            else field
        }
    }

    /**
     * Updates the height unit system.
     *
     * @param value selected height unit system.
     */
    fun updateMeasurements(value: HeightMeasurements) {
        _inputFields.value = _inputFields.value.map { field ->
            if (field is HeightInput && field.id == "height")
                field.copy(isMetric = value == HeightMeasurements.Metric)
            else field
        }
    }

    /**
     * Updates the weight unit system.
     *
     * @param value selected weight unit system.
     */
    fun updateMeasurements(value: WeightMeasurements) {
        _inputFields.value = _inputFields.value.map { field ->
            if (field is WeightInput && field.id == "weight")
                field.copy(isKilos = value == WeightMeasurements.Kilos)
            else field
        }
    }


    /**
     * Updates the metric height field. Valid range 50–272 cm.
     *
     * In-memory only. Call [persistFields] to write to Room.
     *
     * @param cm height in centimetres.
     */
    fun updateHeightMetric(cm: Int) {
        val isValid = cm in 50..272

        _inputFields.value = _inputFields.value.map { field ->
            if (field is HeightInput && field.id == "height")
                field.copy(value = cm.toDouble(), isValid = if (field.isRequired) isValid else true)
            else field
        }
    }

    /**
     * Updates the imperial height field. Converts feet and inches to centimetres before storing.
     *
     * In-memory only. Call [persistFields] to write to Room.
     *
     * @param feet whole feet component.
     * @param inches remaining inches component.
     */
    fun updateHeightImperial(feet: Int, inches: Int) {
        _inputFields.value = _inputFields.value.map { field ->
            if (field is HeightInput && field.id == "height") {
                val cm = field.feetInchesToCm(feet, inches)
                field.copy(
                    value = cm,
                    isValid = if (field.isRequired) cm in 19.68..1207.08 else true
                )
            } else field
        }
    }

    /**
     * Updates the weight field in kilograms. Valid range 20–300 kg.
     *
     * In-memory only. Call [persistFields] to write to Room.
     *
     * @param kilos weight in kilograms.
     */
    fun updateWeightKilos(kilos: Int) {
        val isValid = kilos in 20..300

        _inputFields.value = _inputFields.value.map { field ->
            if (field is WeightInput && field.id == "weight") {
                field.copy(
                    value = kilos.toDouble(),
                    isValid = !field.isRequired || (field.isRequired && isValid)
                )
            } else field
        }
    }

    /**
     * Updates the weight field in pounds. Converts to kg before storing. Valid range 44–661 lb.
     *
     * In-memory only. Call [persistFields] to write to Room.
     *
     * @param pounds weight in pounds.
     */
    fun updateWeightPounds(pounds: Int) {
        val isValid = pounds in 44..661

        _inputFields.value = _inputFields.value.map { field ->
            if (field is WeightInput && field.id == "weight") {
                field.copy(
                    value = pounds / 2.2046,
                    isValid = !field.isRequired || (field.isRequired && isValid)
                )
            } else field
        }
    }

    /**
     * Persists current field values to Room. Call explicitly at meaningful boundaries:
     * onboarding completion, profile save, or before process death.
     */
    fun persistFields() {
        val fields = _inputFields.value
        val name = (fields.firstOrNull { it.id == "full-name" } as? TextInput)?.value
        val dobField = (fields.firstOrNull { it.id == "date-of-birth" } as? DateInput)?.value
        val sex = (fields.firstOrNull { it.id == "sex" } as? SexInput)?.value
        val height = (fields.firstOrNull { it.id == "height" } as? HeightInput)?.value
        val weight = (fields.firstOrNull { it.id == "weight" } as? WeightInput)?.value
        
        viewModelScope.launch {
            userProfileDao.upsert(
                UserProfileEntity(
                    id = 1,
                    fullName = name,
                    dateOfBirth = dobField,
                    sex = sex ?: Sex.Other,
                    heightCm = height ?: 0.0,
                    weightKg = weight ?: 0.0
                )
            )
        }
    }
}


