/*
 * Copyright (C) 2026 Luca Cavagnari
 *
 * This file is part of DermCalc, final project for the Mobile Device Programming course of Univerità Degli Studi Dell'Insubria.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 */
package it.lcavagnari.pdm.dermcalc.models

import it.lcavagnari.pdm.dermcalc.data.HeightInput
import it.lcavagnari.pdm.dermcalc.data.WeightInput
import it.lcavagnari.pdm.dermcalc.data.toEpochMillis
import it.lcavagnari.pdm.dermcalc.data.toLocalDate
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for [it.lcavagnari.pdm.dermcalc.data.HeightInput] and [it.lcavagnari.pdm.dermcalc.data.WeightInput] conversion helpers, and the [LocalDate] epoch roundtrip. */
class InputFieldConversionTest {

    private val height = HeightInput(id = "height", label = 0)
    private val weight = WeightInput(id = "weight", label = 0)

    // ── HeightInput.cmToFeetInches ──────────────────────────────────────────

    @Test
    fun `cmToFeetInches converts 0 cm to 0 feet 0 inches`() {
        val (feet, inches) = height.cmToFeetInches(0.0)
        assertEquals(0.0, feet, 0.001)
        assertEquals(0.0, inches, 0.001)
    }

    @Test
    fun `cmToFeetInches converts 180 cm to approximately 5 feet 10_86 inches`() {
        val (feet, inches) = height.cmToFeetInches(180.0)
        assertEquals(5.0, feet, 0.01)
        assertEquals(10.866, inches, 0.01)
    }

    @Test
    fun `cmToFeetInches converts 152_4 cm to exactly 5 feet 0 inches`() {
        val (feet, inches) = height.cmToFeetInches(152.4)
        assertEquals(5.0, feet, 0.001)
        assertEquals(0.0, inches, 0.001)
    }

    @Test
    fun `cmToFeetInches converts 272 cm to approximately 8 feet 11 inches`() {
        val (feet, inches) = height.cmToFeetInches(272.0)
        assertEquals(8.0, feet, 0.1)
        assertEquals(11.07, inches, 0.1)
    }

    @Test
    fun `cmToFeetInches uses field value when no argument is supplied`() {
        val h = height.copy(value = 170.0)
        val (feet, _) = h.cmToFeetInches()
        assertEquals(5.0, feet, 0.1)
    }

    // ── HeightInput.feetInchesToCm ──────────────────────────────────────────

    @Test
    fun `feetInchesToCm converts 5 feet 0 inches to 152_4 cm`() {
        assertEquals(152.4, height.feetInchesToCm(5, 0), 0.001)
    }

    @Test
    fun `feetInchesToCm converts 6 feet 0 inches to 182_88 cm`() {
        assertEquals(182.88, height.feetInchesToCm(6, 0), 0.001)
    }

    @Test
    fun `feetInchesToCm converts 0 feet 0 inches to 0 cm`() {
        assertEquals(0.0, height.feetInchesToCm(0, 0), 0.001)
    }

    @Test
    fun `feetInchesToCm converts 8 feet 11 inches to approximately 271_78 cm`() {
        assertEquals(271.78, height.feetInchesToCm(8, 11), 0.01)
    }

    @Test
    fun `feetInchesToCm and cmToFeetInches are inverse operations`() {
        val originalFeet = 5
        val originalInches = 9
        val cm = height.feetInchesToCm(originalFeet, originalInches)
        val (resultFeet, resultInches) = height.cmToFeetInches(cm)
        assertEquals(originalFeet.toDouble(), resultFeet, 0.001)
        assertEquals(originalInches.toDouble(), resultInches, 0.001)
    }

    // ── WeightInput.kilosToPounds ───────────────────────────────────────────

    @Test
    fun `kilosToPounds converts 0 kg to 0 lb`() {
        assertEquals(0.0, weight.kilosToPounds(0.0), 0.001)
    }

    @Test
    fun `kilosToPounds converts 70 kg to approximately 154_32 lb`() {
        assertEquals(154.322, weight.kilosToPounds(70.0), 0.01)
    }

    @Test
    fun `kilosToPounds converts 100 kg to approximately 220_46 lb`() {
        assertEquals(220.46, weight.kilosToPounds(100.0), 0.01)
    }

    @Test
    fun `kilosToPounds uses field value when no argument is supplied`() {
        val w = weight.copy(value = 80.0)
        assertEquals(176.368, w.kilosToPounds(), 0.01)
    }

    // ── LocalDate epoch roundtrip ───────────────────────────────────────────

    @Test
    fun `toEpochMillis then toLocalDate is identity for 2000-06-15`() {
        val original = LocalDate(2000, 6, 15)
        val roundtripped = original.toEpochMillis().toLocalDate()
        assertEquals(original, roundtripped)
    }

    @Test
    fun `toEpochMillis then toLocalDate is identity for 1970-01-01`() {
        val original = LocalDate(1970, 1, 1)
        val roundtripped = original.toEpochMillis().toLocalDate()
        assertEquals(original, roundtripped)
    }

    @Test
    fun `toEpochMillis then toLocalDate is identity for 1900-01-01`() {
        val original = LocalDate(1900, 1, 1)
        val roundtripped = original.toEpochMillis().toLocalDate()
        assertEquals(original, roundtripped)
    }

    @Test
    fun `toEpochMillis for 1970-01-01 is 0`() {
        val epoch = LocalDate(1970, 1, 1)
        assertEquals(0L, epoch.toEpochMillis())
    }

    @Test
    fun `toEpochMillis for 1970-01-02 is 86400000`() {
        val dayAfterEpoch = LocalDate(1970, 1, 2)
        assertEquals(86_400_000L, dayAfterEpoch.toEpochMillis())
    }
}
