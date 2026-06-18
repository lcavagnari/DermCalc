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

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [UserProfileEntity].
 * 
 * Manages persistence of the single user profile record.
 */
@Dao
interface UserProfileDao {
    /**
     * Inserts or replaces the user profile.
     * 
     * @param profile the [UserProfileEntity] to persist
     */
    @Upsert
    suspend fun upsert(profile: UserProfileEntity)

    /**
     * Retrieves the user profile.
     * 
     * @return Flow emitting the [UserProfileEntity] or null if not set
     */
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>
}
