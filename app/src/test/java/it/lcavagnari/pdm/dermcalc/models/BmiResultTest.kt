package it.lcavagnari.pdm.dermcalc.models

import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for [BmiResult.compute] and [BmiResult.severity]. */
class BmiResultTest {

    // --- BmiResult.compute() with boundary cases ---

    @Test
    fun `compute with min valid values (20 kg, 50 cm) returns valid result`() {
        val result = BmiResult.compute(weightKg = 20.0, heightCm = 50.0)
        assertEquals(20.0, result.weightKg, 0.001)
        assertEquals(50.0, result.heightCm, 0.001)
        // BMI = 20 / (0.5^2) = 20 / 0.25 = 80
        assertEquals(80.0, result.score, 0.001)
        assert(result.isValid())
    }

    @Test
    fun `compute with max valid values (300 kg, 272 cm) returns valid result`() {
        val result = BmiResult.compute(weightKg = 300.0, heightCm = 272.0)
        assertEquals(300.0, result.weightKg, 0.001)
        assertEquals(272.0, result.heightCm, 0.001)
        // BMI = 300 / (2.72^2) = 300 / 7.3984 ≈ 40.549
        assertEquals(40.549, result.score, 0.001)
        assert(result.isValid())
    }

    @Test
    fun `compute with normal case (70 kg, 172 cm) returns correct BMI`() {
        val result = BmiResult.compute(weightKg = 70.0, heightCm = 172.0)
        assertEquals(70.0, result.weightKg, 0.001)
        assertEquals(172.0, result.heightCm, 0.001)
        // BMI = 70 / (1.72^2) = 70 / 2.9584 ≈ 23.661
        assertEquals(23.661, result.score, 0.001)
        assert(result.isValid())
    }

    // --- Severity thresholds ---

    @Test
    fun `severity returns Severe for BMI less than 18_5 (underweight)`() {
        val result = BmiResult.compute(50.0, 170.0)
        // BMI ≈ 17.301
        assertEquals(Severity.SEVERE, result.severity())
    }

    @Test
    fun `severity returns Mild for BMI in range 18_5 to 25`() {
        val result = BmiResult.compute(65.0, 175.0)
        // BMI ≈ 21.224
        assertEquals(Severity.MILD, result.severity())
    }

    @Test
    fun `severity returns Moderate for BMI in range 25 to 30`() {
        val result = BmiResult.compute(80.0, 175.0)
        // BMI ≈ 26.122
        assertEquals(Severity.MODERATE, result.severity())
    }

    @Test
    fun `severity returns Severe for BMI 30 or greater (obese)`() {
        val result = BmiResult.compute(100.0, 175.0)
        // BMI ≈ 32.653
        assertEquals(Severity.SEVERE, result.severity())
    }

    @Test
    fun `severity at boundary 18_5 returns Mild`() {
        // BMI = weight / (height^2), solve for weight where BMI = 18.5 and height = 170
        // weight = 18.5 * (1.70^2) = 18.5 * 2.89 = 53.465
        val result = BmiResult.compute(53.465, 170.0)
        assertEquals(Severity.MILD, result.severity())
    }

    @Test
    fun `severity at boundary 25_0 returns Moderate`() {
        // BMI = 25.0, height = 170; weight = 25.0 * 2.89 = 72.25
        val result = BmiResult.compute(72.25, 170.0)
        assertEquals(Severity.MODERATE, result.severity())
    }

    @Test
    fun `severity just above boundary 25_0 returns Moderate`() {
        // BMI = 25.01, height = 170; weight = 25.01 * 2.89 ≈ 72.279
        val result = BmiResult.compute(72.279, 170.0)
        assertEquals(Severity.MODERATE, result.severity())
    }

    @Test
    fun `severity at boundary 30_0 returns Severe`() {
        // BMI = 30.0, height = 170; weight = 30.0 * 2.89 = 86.7
        val result = BmiResult.compute(86.7, 170.0)
        assertEquals(Severity.SEVERE, result.severity())
    }
}
