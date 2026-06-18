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
package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

/**
 * User profile data stored in Room database.
 * 
 * @property id unique identifier, defaults to 1 for single profile
 * @property fullName user's full name, nullable
 * @property dateOfBirth user's date of birth, nullable
 * @property sex user's biological sex
 * @property heightCm user's height in centimeters
 * @property weightKg user's weight in kilograms
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String? = null,
    val dateOfBirth: LocalDate? = null,
    val sex: Sex? = null,
    val heightCm: Double = 0.0,
    val weightKg: Double = 0.0
)
