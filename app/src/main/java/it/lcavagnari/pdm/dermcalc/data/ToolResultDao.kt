package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolResultDao {
    @Upsert
    suspend fun upsert(result: ToolResultEntity): Long

    @Query("SELECT * FROM tool_results ORDER BY id DESC")
    fun getAll(): Flow<List<ToolResultEntity>>

    @Query("DELETE FROM tool_results WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM tool_results")
    suspend fun deleteAll()
}
