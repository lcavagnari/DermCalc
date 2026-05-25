package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/** Placeholder screen for the PASI calculator. Not yet implemented. */
@Composable
fun PASIScreen(modifier: Modifier = Modifier, onSaveResult: () -> Unit) {
    Text("1234")
}

/** Placeholder screen for the EASI calculator. Not yet implemented. */
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
