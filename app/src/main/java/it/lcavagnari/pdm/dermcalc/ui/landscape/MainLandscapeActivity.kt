package it.lcavagnari.pdm.dermcalc.ui.landscape

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel

@Composable
fun MainLandscapeActivity(onboardingModel: OnboardingModel) {

    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize()) {
        Text(
            "DermCalc does not support Landscape yet, flip the device to vertical mode in order to continue using the app. No data was lost.",
            textAlign = TextAlign.Center
        )
    }

}