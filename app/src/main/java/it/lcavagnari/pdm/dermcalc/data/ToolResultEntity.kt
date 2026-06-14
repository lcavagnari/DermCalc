package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tool_results")
data class ToolResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolName: String,
    val score: Double,
    val timestamp: Long,
    val detailsJson: String
)
