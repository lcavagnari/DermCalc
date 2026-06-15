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
