package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Placeholder screen for the PASI calculator.
 *
 * Renders temporary text until the PASI input flow is implemented.
 *
 * @param modifier modifier reserved for the future screen layout.
 * @param onSaveResult callback reserved for saving a PASI result.
 */
@Composable
fun PASIScreen(modifier: Modifier = Modifier, onSaveResult: () -> Unit) {
    Text("1234")
}

/**
 * Placeholder screen for the EASI calculator.
 *
 * Renders temporary text until the EASI input flow is implemented.
 *
 * @param modifier modifier reserved for the future screen layout.
 * @param onSaveResult callback reserved for saving an EASI result.
 */
@Composable
fun EASIScreen(modifier: Modifier = Modifier, onSaveResult: () -> Unit) {
    Text("1234")
}

@Preview(showBackground = true)
@Composable
fun PASIScreenPreview() {
    PASIScreen() {}
}

@Preview(showBackground = true)
@Composable
fun EASIScreenPreview() {
    EASIScreen() {}
}

