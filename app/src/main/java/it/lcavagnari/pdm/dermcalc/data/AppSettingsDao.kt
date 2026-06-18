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
 * Data Access Object for [AppSettingsEntity].
 * 
 * Manages persistence of application-wide settings.
 */
@Dao
interface AppSettingsDao {
    /**
     * Inserts or replaces the application settings.
     * 
     * @param settings the [AppSettingsEntity] to persist
     */
    @Upsert
    suspend fun upsert(settings: AppSettingsEntity)

    /**
     * Retrieves the application settings.
     * 
     * @return Flow emitting the [AppSettingsEntity] or null if not set
     */
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>
}
