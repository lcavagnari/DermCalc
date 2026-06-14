package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolResultDao {
    @Insert
    suspend fun insert(result: ToolResultEntity)

    @Query("SELECT * FROM tool_results ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ToolResultEntity>>

    @Query("DELETE FROM tool_results WHERE id = :id")
    suspend fun deleteById(id: Long)
}
