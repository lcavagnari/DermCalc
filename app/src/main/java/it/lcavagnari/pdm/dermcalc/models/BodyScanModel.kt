package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BsaRegion.NONE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Body region used for BSA (Body Surface Area) calculation, based on the Rule of Nines.
 *
 * Each entry carries a [labelRes] string resource for display and a [bodyWeight] representing
 * its fractional contribution to total body surface (e.g. 0.09 = 9 %).
 * The affected percentage for a region is multiplied by [bodyWeight] inside [BsaResult.compute]
 * to produce the total BSA score.
 *
 * @property labelRes string resource id for the region label; -1 for [NONE].
 * @property bodyWeight fractional BSA contribution of this region (0.0–1.0).
 */
enum class BsaRegion(@StringRes val labelRes: Int, val bodyWeight: Double) {
    /** Sentinel value used when no region is selected; carries no label or body weight. */
    NONE(-1, 0.00),
    HEAD(R.string.bsa_region_head, 0.09),
    RIGHT_ARM(R.string.bsa_region_right_arm, 0.09),
    LEFT_ARM(R.string.bsa_region_left_arm, 0.09),
    ANTERIOR_TRUNK(R.string.bsa_region_anterior_trunk, 0.18),
    POSTERIOR_TRUNK(R.string.bsa_region_posterior_trunk, 0.18),
    RIGHT_LEG(R.string.bsa_region_right_leg, 0.18),
    LEFT_LEG(R.string.bsa_region_left_leg, 0.18),
}

/**
 * Snapshot of the BSA body scan UI state.
 *
 * @property regionValues map of each [BsaRegion] to the affected percentage (0–100) for that region.
 *   Initialised to 0 for all regions. Values are set in steps of 5 via the region slider.
 * @property selectedRegion the region currently active in the slider; [BsaRegion.NONE] when nothing is selected.
 */
data class BsaState(
    val regionValues: Map<BsaRegion, Int> = BsaRegion.entries.associateWith { 0 },
    val selectedRegion: BsaRegion = BsaRegion.NONE,
) {
    /** Computed BSA result derived from current [regionValues]; recalculated on every read. */
    val result: BsaResult
        get() = BsaResult.compute(regionValues)
}

/**
 * ViewModel holding the mutable state for the BSA body-scan screen.
 *
 * Exposes a single [state] flow of [BsaState] updated by [selectRegion], [updateRegion], and [reset].
 */
class BodyScanModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(BsaState())

    /** Current body scan state; collect this in the UI to react to region selection and value changes. */
    val state: StateFlow<BsaState> = _state.asStateFlow()

    /**
     * Sets [region] as the active region for slider editing.
     *
     * @param region the [BsaRegion] tapped on the body diagram; pass [BsaRegion.NONE] to deselect.
     */
    fun selectRegion(region: BsaRegion) {
        _state.value = _state.value.copy(selectedRegion = region)
    }

    /**
     * Records the affected percentage for [region].
     *
     * @param region the [BsaRegion] being updated.
     * @param pct affected percentage for this region, in the range **0–100** (slider steps of 5).
     */
    fun updateRegion(region: BsaRegion, pct: Int) {
        val s = _state.value
        _state.value = s.copy(regionValues = s.regionValues + (region to pct))

    }

    /**
     * Resets the scan to its initial state: clears [BsaState.selectedRegion] to [BsaRegion.NONE]
     * and zeroes all region values. Called automatically on screen stop via [LifecycleEventEffect].
     */
    fun reset() {
        _state.value = _state.value.copy(selectedRegion = BsaRegion.NONE)
        _state.value = _state.value.copy(regionValues = BsaRegion.entries.associateWith { 0 })
    }
}
