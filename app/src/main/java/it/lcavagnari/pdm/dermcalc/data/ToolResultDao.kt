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
 * Data Access Object for [ToolResultEntity].
 * 
 * Manages persistence and retrieval of tool calculation results.
 */
@Dao
interface ToolResultDao {
    /**
     * Inserts or replaces a tool result.
     * 
     * @param result the [ToolResultEntity] to persist
     */
    @Upsert
    suspend fun upsert(result: ToolResultEntity)

    /**
     * Retrieves all tool results ordered by ID descending.
     * 
     * @return Flow emitting list of all [ToolResultEntity] records
     */
    @Query("SELECT * FROM tool_results ORDER BY id DESC")
    fun getAll(): Flow<List<ToolResultEntity>>

    /**
     * Deletes a specific tool result by ID.
     * 
     * @param id the ID of the [ToolResultEntity] to delete
     */
    @Query("DELETE FROM tool_results WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Deletes all tool results.
     */
    @Query("DELETE FROM tool_results")
    suspend fun deleteAll()
}
