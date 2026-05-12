package it.lcavagnari.pdm.dermcalc.models

import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [OnboardingModel] — all update methods and [OnboardingModel.isPageInputValid].
 *
 * OnboardingModel is a plain class here; no Android framework is needed for these tests
 * because none of the logic under test touches Android APIs.
 */
class OnboardingModelTest {

    private lateinit var model: OnboardingModel

    @Before
    fun setUp() {
        model = OnboardingModel()
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private fun field(id: String) = model.fields.value.first { it.id == id }

    // ── updateName ───────────────────────────────────────────────────────────

    @Test
    fun `updateName with two words is valid`() {
        model.updateName("Mario Rossi")
        assertTrue((field("full-name") as TextInput).isValid)
    }

    @Test
    fun `updateName with three words is valid`() {
        model.updateName("Mario Giovanni Rossi")
        assertTrue((field("full-name") as TextInput).isValid)
    }

    @Test
    fun `updateName with single word is invalid`() {
        model.updateName("Mario")
        assertFalse((field("full-name") as TextInput).isValid)
    }

    @Test
    fun `updateName with blank string is invalid`() {
        model.updateName("")
        assertFalse((field("full-name") as TextInput).isValid)
    }

    @Test
    fun `updateName with only spaces is invalid`() {
        model.updateName("   ")
        assertFalse((field("full-name") as TextInput).isValid)
    }

    @Test
    fun `updateName stores the value verbatim`() {
        model.updateName("  Luke  Skywalker  ")
        assertEquals("  Luke  Skywalker  ", (field("full-name") as TextInput).value)
    }

    // ── updateDateOfBirth ────────────────────────────────────────────────────

    @Test
    fun `updateDateOfBirth with a date in the past is valid`() {
        model.updateDateOfBirth(LocalDate(1990, 6, 15))
        assertTrue((field("date-of-birth") as DateInput).isValid)
    }

    @Test
    fun `updateDateOfBirth with epoch boundary 1900-01-01 is invalid (not strictly after)`() {
        model.updateDateOfBirth(LocalDate(1900, 1, 1))
        assertFalse((field("date-of-birth") as DateInput).isValid)
    }

    @Test
    fun `updateDateOfBirth with 1900-01-02 is valid`() {
        model.updateDateOfBirth(LocalDate(1900, 1, 2))
        assertTrue((field("date-of-birth") as DateInput).isValid)
    }

    @Test
    fun `updateDateOfBirth with a date far in the future is invalid`() {
        model.updateDateOfBirth(LocalDate(2100, 1, 1))
        assertFalse((field("date-of-birth") as DateInput).isValid)
    }

    // ── updateSex ────────────────────────────────────────────────────────────

    @Test
    fun `updateSex with Male sets isValid to true`() {
        model.updateSex(Sex.Male)
        assertTrue((field("sex") as SexInput).isValid)
    }

    @Test
    fun `updateSex with Female sets isValid to true`() {
        model.updateSex(Sex.Female)
        assertTrue((field("sex") as SexInput).isValid)
    }

    @Test
    fun `updateSex with Other sets isValid to true`() {
        model.updateSex(Sex.Other)
        assertTrue((field("sex") as SexInput).isValid)
    }

    @Test
    fun `updateSex stores the selected value`() {
        model.updateSex(Sex.Female)
        assertEquals(Sex.Female, (field("sex") as SexInput).value)
    }

    // ── updateHeightMetric ───────────────────────────────────────────────────

    @Test
    fun `updateHeightMetric with 170 cm is valid`() {
        model.updateHeightMetric(170)
        assertTrue((field("height") as HeightInput).isValid)
    }

    @Test
    fun `updateHeightMetric with lower bound 50 is valid`() {
        model.updateHeightMetric(50)
        assertTrue((field("height") as HeightInput).isValid)
    }

    @Test
    fun `updateHeightMetric with upper bound 272 is valid`() {
        model.updateHeightMetric(272)
        assertTrue((field("height") as HeightInput).isValid)
    }

    @Test
    fun `updateHeightMetric with 49 is invalid`() {
        model.updateHeightMetric(49)
        assertFalse((field("height") as HeightInput).isValid)
    }

    @Test
    fun `updateHeightMetric with 273 is invalid`() {
        model.updateHeightMetric(273)
        assertFalse((field("height") as HeightInput).isValid)
    }

    @Test
    fun `updateHeightMetric stores value in cm`() {
        model.updateHeightMetric(175)
        assertEquals(175.0, (field("height") as HeightInput).value!!, 0.001)
    }

    // ── updateHeightImperial ─────────────────────────────────────────────────

    @Test
    fun `updateHeightImperial with 5 feet 10 inches is valid`() {
        model.updateHeightImperial(5, 10)
        assertTrue((field("height") as HeightInput).isValid)
    }

    @Test
    fun `updateHeightImperial with 0 feet 0 inches is invalid (too short)`() {
        model.updateHeightImperial(0, 0)
        assertFalse((field("height") as HeightInput).isValid)
    }

    @Test
    fun `updateHeightImperial converts to cm before storing`() {
        model.updateHeightImperial(5, 0)
        assertEquals(152.4, (field("height") as HeightInput).value!!, 0.001)
    }

    // ── updateWeightKilos ────────────────────────────────────────────────────

    @Test
    fun `updateWeightKilos with 70 kg is valid`() {
        model.updateWeightKilos(70)
        assertTrue((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightKilos with lower bound 20 is valid`() {
        model.updateWeightKilos(20)
        assertTrue((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightKilos with upper bound 300 is valid`() {
        model.updateWeightKilos(300)
        assertTrue((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightKilos with 19 is invalid`() {
        model.updateWeightKilos(19)
        assertFalse((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightKilos with 301 is invalid`() {
        model.updateWeightKilos(301)
        assertFalse((field("weight") as WeightInput).isValid)
    }

    // ── updateWeightPounds ───────────────────────────────────────────────────

    @Test
    fun `updateWeightPounds with 154 lb is valid`() {
        model.updateWeightPounds(154)
        assertTrue((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightPounds with lower bound 44 is valid`() {
        model.updateWeightPounds(44)
        assertTrue((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightPounds with upper bound 661 is valid`() {
        model.updateWeightPounds(661)
        assertTrue((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightPounds with 43 is invalid`() {
        model.updateWeightPounds(43)
        assertFalse((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightPounds with 662 is invalid`() {
        model.updateWeightPounds(662)
        assertFalse((field("weight") as WeightInput).isValid)
    }

    @Test
    fun `updateWeightPounds converts to kg before storing`() {
        model.updateWeightPounds(220)
        // 220 lb ÷ 2.2046 ≈ 99.79 kg
        assertEquals(99.79, (field("weight") as WeightInput).value!!, 0.1)
    }

    // ── updateMeasurements (height) ───────────────────────────────────────────

    @Test
    fun `updateMeasurements HeightMeasurements_Metric sets isMetric true`() {
        model.updateMeasurements(HeightMeasurements.Imperial) // change first
        model.updateMeasurements(HeightMeasurements.Metric)
        assertTrue((field("height") as HeightInput).isMetric)
    }

    @Test
    fun `updateMeasurements HeightMeasurements_Imperial sets isMetric false`() {
        model.updateMeasurements(HeightMeasurements.Imperial)
        assertFalse((field("height") as HeightInput).isMetric)
    }

    // ── updateMeasurements (weight) ───────────────────────────────────────────

    @Test
    fun `updateMeasurements WeightMeasurements_Kilos sets isKilos true`() {
        model.updateMeasurements(WeightMeasurements.Pounds)
        model.updateMeasurements(WeightMeasurements.Kilos)
        assertTrue((field("weight") as WeightInput).isKilos)
    }

    @Test
    fun `updateMeasurements WeightMeasurements_Pounds sets isKilos false`() {
        model.updateMeasurements(WeightMeasurements.Pounds)
        assertFalse((field("weight") as WeightInput).isKilos)
    }

    // ── isPageInputValid ──────────────────────────────────────────────────────

    @Test
    fun `isPageInputValid returns true for empty fieldIds`() {
        assertTrue(model.isPageInputValid(emptyList()))
    }

    @Test
    fun `isPageInputValid returns false when required field is not valid`() {
        // full-name is required and starts invalid
        assertFalse(model.isPageInputValid(listOf("full-name")))
    }

    @Test
    fun `isPageInputValid returns true after valid name is entered`() {
        model.updateName("Mario Rossi")
        assertTrue(model.isPageInputValid(listOf("full-name")))
    }

    @Test
    fun `isPageInputValid returns false when only one of two required fields is valid`() {
        model.updateName("Mario Rossi") // valid
        // height still invalid
        assertFalse(model.isPageInputValid(listOf("full-name", "height")))
    }

    @Test
    fun `isPageInputValid returns true when all required fields in list are valid`() {
        model.updateName("Mario Rossi")
        model.updateDateOfBirth(LocalDate(1990, 6, 15))
        // sex is not required, so no update needed
        assertTrue(model.isPageInputValid(listOf("full-name", "date-of-birth", "sex")))
    }

    @Test
    fun `isPageInputValid returns false for unknown field id`() {
        assertFalse(model.isPageInputValid(listOf("non-existent-id")))
    }

    @Test
    fun `isPageInputValid uses custom fields snapshot when provided`() {
        val customFields = listOf(
            TextInput(id = "full-name", label = "Full name", value = "Test", isValid = true)
        )
        assertTrue(model.isPageInputValid(listOf("full-name"), customFields))
    }

    @Test
    fun `finishOnboarding flips hasSeenOnboarding to true`() {
        assertFalse(model.hasSeenOnboarding.value)
        model.finishOnboarding()
        assertTrue(model.hasSeenOnboarding.value)
    }
}
