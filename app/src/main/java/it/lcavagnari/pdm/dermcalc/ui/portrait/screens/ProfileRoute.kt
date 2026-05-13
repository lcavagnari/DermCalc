package it.lcavagnari.pdm.dermcalc.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.DateInput
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Sex
import it.lcavagnari.pdm.dermcalc.models.SexInput
import it.lcavagnari.pdm.dermcalc.models.TextInput
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity

import java.util.Locale.getDefault

/**
 * Displays basic profile placeholder content centered on current screen.
 *
 * @param navController - controller available for future account flow routing.
 */
@Composable
fun ProfileRoute(navController: NavHostController, onboardingModel: OnboardingModel) {
    val inputFields by onboardingModel.fields.collectAsState()

    Column(
        modifier = Modifier.fillMaxHeight().padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Personal Details".uppercase(getDefault()),
            //stringResource(R.string.nav_profile_subtitle),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Italic
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            inputFields.forEach { field ->
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = stringResource(field.label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    )

                    Text(
                        text = when (field) {
                            is TextInput -> field.value
                            is DateInput -> field.value?.toString() ?: stringResource(R.string.placeholder_date)
                            is SexInput -> {
                                stringResource(when (field.value) {
                                    Sex.Male -> R.string.sex_male
                                    Sex.Female -> R.string.sex_female
                                    Sex.Other -> R.string.sex_other
                                    else -> R.string.sex_other
                                })
                            }

                            is HeightInput -> ""
                            is WeightInput -> ""
                        },
                        modifier = Modifier.padding(5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    )
                }
                HorizontalDivider(
                    thickness = 4.dp
                )
            }

        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Icon(
            modifier = Modifier.size(40.dp).padding(end = 5.dp),
            painter = painterResource(id = R.drawable.ic_annoying_dog),
            contentDescription = "annoying dog",
            tint = Color.Unspecified
        )
    }
}


// DO NOT EDIT THIS

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val vm = remember { OnboardingModel().also { it.finishOnboarding() } }
    MainPortraitActivity(onboardingModel = vm, startingDestination = ProfileRoute)
}
