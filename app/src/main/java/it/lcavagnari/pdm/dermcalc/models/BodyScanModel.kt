package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import it.lcavagnari.pdm.dermcalc.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BsaRegion(@StringRes val labelRes: Int, val bodyWeight: Double) {
    NONE(-1, 0.00),
    HEAD(R.string.bsa_region_head, 0.09),
    RIGHT_ARM(R.string.bsa_region_right_arm, 0.09),
    LEFT_ARM(R.string.bsa_region_left_arm, 0.09),
    ANTERIOR_TRUNK(R.string.bsa_region_anterior_trunk, 0.18),
    POSTERIOR_TRUNK(R.string.bsa_region_posterior_trunk, 0.18),
    RIGHT_LEG(R.string.bsa_region_right_leg, 0.18),
    LEFT_LEG(R.string.bsa_region_left_leg, 0.18),
}

data class BsaState(
    val regionValues: Map<BsaRegion, Int> = BsaRegion.entries.associateWith { 0 },
    val selectedRegion: BsaRegion = BsaRegion.NONE,
) {
    val result: BsaResult
        get() = BsaResult.compute(regionValues)
}

class BodyScanModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(BsaState())
    val state: StateFlow<BsaState> = _state.asStateFlow()


    fun selectRegion(region: BsaRegion) {
        _state.value = _state.value.copy(selectedRegion = region)
    }

    fun updateRegion(region: BsaRegion, pct: Int) {
        val s = _state.value
        _state.value = s.copy(regionValues = s.regionValues + (region to pct))

    }

    fun reset() {
        _state.value = _state.value.copy(selectedRegion = BsaRegion.NONE)
        _state.value = _state.value.copy(regionValues = BsaRegion.entries.associateWith { 0 })
    }
}
