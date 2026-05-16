package it.lcavagnari.pdm.dermcalc.ui.landscape

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel

/**
 * Stub composable displayed when the device is in landscape orientation.
 *
 * Shows an unsupported-orientation message and prompts the user to rotate the device.
 * Landscape support is not yet implemented.
 *
 * @param onboardingModel - passed through for future use when landscape support is added.
 */
@Composable
fun MainLandscapeActivity(onboardingModel: OnboardingModel) {

    Column(
        modifier = Modifier.fillMaxSize()
        .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = R.drawable.ic_ecg),
            contentDescription = "App Logo",
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            "DermCalc does not support Landscape yet.\nFlip the device to vertical mode in order to continue using the app.\n\n No data was lost.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

}
