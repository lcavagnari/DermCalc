package it.lcavagnari.pdm.dermcalc.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity

/**
 * Displays basic profile placeholder content centered on current screen.
 *
 * @param navController - controller available for future account flow routing.
 */
@Composable
fun ProfileRoute(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Text(
            text = "Profile",
            fontSize = 18.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// Preview
private val vm = OnboardingModel()

@SuppressLint("NewApi")
@Preview(showBackground = true)
@Composable
fun MainPortraitActivityPreview() {
    vm.finishOnboarding()
    MainPortraitActivity(onboardingModel = vm, startingDestination = ProfileRoute)
}