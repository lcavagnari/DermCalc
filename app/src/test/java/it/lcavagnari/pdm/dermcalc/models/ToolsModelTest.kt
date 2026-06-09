package it.lcavagnari.pdm.dermcalc.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class ToolsModelTest {

    // --- BmiResult.compute() ---

    @Test
    fun `compute returns correct BMI for standard values`() {
        val result = BmiResult.compute(weightKg = 70.0, heightCm = 175.0)
        assertEquals(70.0, result.weightKg, 0.001)
        assertEquals(175.0, result.heightCm, 0.001)
        // BMI = 70 / (1.75^2) = 70 / 3.0625 ≈ 22.857
        assertEquals(22.857, result.score, 0.001)
    }

    @Test
    fun `compute handles borderline underweight`() {
        // BMI = 50 / (1.70^2) = 50 / 2.89 ≈ 17.301
        val result = BmiResult.compute(50.0, 170.0)
        assertEquals(17.301, result.score, 0.001)
    }

    @Test
    fun `compute handles obese range`() {
        // BMI = 120 / (1.80^2) = 120 / 3.24 ≈ 37.037
        val result = BmiResult.compute(120.0, 180.0)
        assertEquals(37.037, result.score, 0.001)
    }

    @Test
    fun `compute isValid reflects inputs`() {
        assertTrue(BmiResult.compute(70.0, 175.0).isValid())
        assertFalse(BmiResult.compute(0.0, 175.0).isValid())
        assertFalse(BmiResult.compute(70.0, 0.0).isValid())
        assertFalse(BmiResult.compute(-1.0, 175.0).isValid())
    }

    // --- ToolResult.severity() ---

    @Test
    fun `BmiResult severity matches clinical thresholds`() {
        // Underweight: < 18.5
        assertEquals(Severity.SEVERE, BmiResult.compute(50.0, 170.0).severity())
        // Normal weight: 18.5 ≤ score < 25
        assertEquals(Severity.MILD, BmiResult.compute(65.0, 175.0).severity())
        // Overweight: 25 ≤ score < 30
        assertEquals(Severity.MODERATE, BmiResult.compute(80.0, 175.0).severity())
        // Obese: score ≥ 30
        assertEquals(Severity.SEVERE, BmiResult.compute(100.0, 175.0).severity())
    }

    @Test
    fun `BsaResult severity matches clinical thresholds`() {
        val mild = BsaResult(affectedPercentage = 5.0, score = 5.0)
        assertEquals(Severity.MILD, mild.severity())

        val moderate = BsaResult(affectedPercentage = 15.0, score = 15.0)
        assertEquals(Severity.MODERATE, moderate.severity())

        val severe = BsaResult(affectedPercentage = 35.0, score = 35.0)
        assertEquals(Severity.SEVERE, severe.severity())

        // Boundary: 10.0 → Moderate (not Mild)
        val boundary = BsaResult(affectedPercentage = 10.0, score = 10.0)
        assertEquals(Severity.MODERATE, boundary.severity())

        // Boundary: 30.0 → Severe
        val boundary2 = BsaResult(affectedPercentage = 30.0, score = 30.0)
        assertEquals(Severity.SEVERE, boundary2.severity())
    }

    @Test
    fun `formattedScore gives no decimals for whole numbers, one decimal otherwise`() {
        assertEquals("25", BmiResult.compute(70.0, 167.33).formattedScore())
        assertEquals("22.9", BmiResult.compute(70.0, 175.0).formattedScore())
    }
}
