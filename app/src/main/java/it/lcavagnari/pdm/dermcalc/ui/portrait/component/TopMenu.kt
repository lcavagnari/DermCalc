package it.lcavagnari.pdm.dermcalc.ui.portrait.component

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TopMenu(navController: NavController, onToggleTheme: () -> Unit = {}) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    Log.d("TopMenu", "-".repeat(50))
    Log.d("TopMenu", "currentDestination: ${currentDestination?.route}")
    Log.d("TopMenu", "currentDestination: $currentDestination")
    Log.d("TopMenu", "navController: ${navController.currentBackStackEntryAsState().value?.destination?.route}")
    Log.d("TopMenu", "HomeRoute: ${HomeRoute.route}")
    Log.d("TopMenu", "-".repeat(50))


    val title: Int = when(currentDestination?.route) {
        ToolsRoute.route -> R.string.nav_tools
        ProfileRoute.route -> R.string.nav_profile
        else -> R.string.app_name
    }

    val subtitle: Int? = when(currentDestination?.route) {
        HomeRoute.route -> R.string.nav_home_subtitle
        ToolsRoute.route -> R.string.nav_tools_subtitle
        ProfileRoute.route -> R.string.nav_profile_subtitle
        else -> null
    }

    val icon = when(currentDestination?.route) {
        ToolsRoute.route -> R.drawable.ic_tools_calculator
        ProfileRoute.route -> R.drawable.ic_profile_button
        else -> R.drawable.ic_ecg
    }

    Card(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(1/7f),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp, top = 22.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(icon),
                    contentDescription = "Back button",
                    tint = MaterialTheme.colorScheme.onSecondary
                )

                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 45.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(title),
                        modifier = Modifier,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        softWrap = false,
                    )

                    subtitle?.let {
                        Text(
                            text = stringResource(it),
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(end = 12.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) { TopTrayButtons(iconTint = MaterialTheme.colorScheme.onSecondary, onToggleTheme = onToggleTheme ) {} }
        }
    }
}

private val vm = OnboardingModel()

@SuppressLint("NewApi", "RestrictedApi")
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun MenuPreview() {
    vm.finishOnboarding()
    MainPortraitActivity(onboardingModel = vm)
}