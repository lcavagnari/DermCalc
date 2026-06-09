package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel

enum class BsaRegion(val label: String, val bodyWeight: Float) {
    HEAD("Head & neck", 0.09f),
    RIGHT_ARM("Right arm", 0.09f),
    LEFT_ARM("Left arm", 0.09f),
    ANTERIOR_TRUNK("Anterior trunk", 0.18f),
    POSTERIOR_TRUNK("Posterior trunk", 0.18f),
    RIGHT_LEG("Right leg", 0.18f),
    LEFT_LEG("Left leg", 0.18f),
}

data class BsaState(
    val regionValues: Map<BsaRegion, Int> = BsaRegion.entries.associateWith { 0 },
    val selectedRegion: BsaRegion? = null,
) {
    val totalBsaPct: Float get() = BsaRegion.entries.sumOf { region ->
        (regionValues[region] ?: 0) * region.bodyWeight.toDouble()
    }.toFloat()

    val severity: Severity get() = when {
        totalBsaPct == 0f  -> Severity.NONE
        totalBsaPct < 10f  -> Severity.MILD
        totalBsaPct < 30f  -> Severity.MODERATE
        else               -> Severity.SEVERE
    }
}

class BsaViewModel(application: Application) : AndroidViewModel(application) {
    var state: BsaState by mutableStateOf(BsaState())
        private set

    fun selectRegion(region: BsaRegion) { state = state.copy(selectedRegion = region) }

    fun setRegionValue(region: BsaRegion, pct: Int) {
        state = state.copy(regionValues = state.regionValues + (region to pct))
    }
}
