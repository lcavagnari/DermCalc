package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Upsert
    suspend fun upsert(settings: AppSettingsEntity)

    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>
}
