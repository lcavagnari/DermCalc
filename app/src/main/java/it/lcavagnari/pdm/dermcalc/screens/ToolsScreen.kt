package it.lcavagnari.pdm.dermcalc.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
/**
 * Displays calculator tools placeholder while feature modules are added.
 *
 * @param navController Controller available for future tools deep links.
 * @return Unit.
 */
fun ToolsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Calculators",
            fontSize = 18.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
