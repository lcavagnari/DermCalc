package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tool calculation result stored in Room database.
 * 
 * @property id unique identifier, auto-generated
 * @property toolName name of the tool that generated this result
 * @property score numeric score from the calculation
 * @property detailsJson JSON string containing detailed result data
 */
@Entity(tableName = "tool_results")
data class ToolResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolName: String,
    val score: Double,
    val detailsJson: String
)
