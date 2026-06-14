package it.lcavagnari.pdm.dermcalc.ui.preview

import it.lcavagnari.pdm.dermcalc.models.BmiResult
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus

/** Populates [tm] with sample BmiResult entries for preview composables. */
val previewBmiResults: (ToolsModel) -> Unit = { tm ->
    tm.addResult(BmiResult(weightKg = 70.0, heightCm = 175.0, score = 22.9))
    tm.addResult(
        BmiResult(
            weightKg = 85.0, heightCm = 175.0, score = 27.8,
            timestamp = today().date.minus(3, DateTimeUnit.DAY).atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 110.0, heightCm = 175.0, score = 35.9,
            timestamp = today().date.minus(10, DateTimeUnit.DAY)
                .atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 110.0, heightCm = 175.0, score = 35.9, timestamp = today().date.minus(
                10,
                DateTimeUnit.WEEK
            ).atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 110.0, heightCm = 175.0, score = 35.9, timestamp = today().date.minus(
                10,
                DateTimeUnit.MONTH
            ).atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 92.0, heightCm = 175.0, score = 30.1, timestamp = today().date.minus(
                1,
                DateTimeUnit.YEAR
            ).atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 78.0,
            heightCm = 175.0,
            score = 25.5,
            timestamp = today()
        )
    )
}
