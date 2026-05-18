package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.component.BorderSide
import it.lcavagnari.pdm.dermcalc.ui.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulBravery
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPatience


/**
 * Represents a single page in the onboarding flow.
 *
 * @property title - text displayed as the page heading.
 * @property description - optional subtitle shown below the title.
 * @property imageRes - optional vector icon displayed on the page.
 * @property imageDrawable - optional drawable resource id for the page image.
 * @property imageSize - size applied to the image composable. Defaults to 280.dp.
 * @property inputFieldIds - ids of [it.lcavagnari.pdm.dermcalc.models.InputField] instances rendered on this page.
 * @property inputFieldId - deprecated. use [inputFieldIds] instead.
 * @constructor Create empty Onboarding screen
 */
data class ToolCard(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val color: Color,

    val imageRes: ImageVector? = null,
    val imageDrawable: Int? = null,
    val imageSize: Dp? = 40.dp,
    val onClick:() -> Unit = {},

    val districtNum: Int? = null,
    val valueRange: Pair<Double, Double>? = null,

    val borderSide: BorderSide = BorderSide.Top
)

/**
 * Displays calculator tools placeholder while feature modules are added.
 *
 * @param navController - controller available for future tools deep links.
 */
@Composable
fun ToolsScreen(navController: NavHostController, toolsModel: ToolsModel) {
    val quick = listOf<ToolCard>(
        ToolCard(
            title = R.string.tools_bmi,
            description = R.string.tools_bmi_description,
            imageDrawable = R.drawable.ic_body_mass_index,
            borderSide = BorderSide.Top,
            color = SoulPatience
        ),
        ToolCard(
            title = R.string.tools_bsa,
            description = R.string.tools_bsa_description,
            imageDrawable = R.drawable.ic_bsa_lungs,
            borderSide = BorderSide.Top,
            color = SoulBravery
        )
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
    ) {

            Text(
                text = stringResource(R.string.tools_quick).uppercase(),
                modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )

            QuickCalculators(toolsList = quick)


            Text(
                text = stringResource(R.string.tools_index).uppercase(),
                modifier = Modifier.weight(1f).padding(top = 10.dp).fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )
            IndexesCalculators {}
    }
}

@Composable
fun QuickCalculators(modifier: Modifier = Modifier, toolsList:List<ToolCard>) {
    Row(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Max),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
    ) {
        toolsList.forEach {
            BorderedCard(
                modifier = modifier.weight(1f).padding(5.dp).fillMaxHeight(),
                elevation = CardDefaults.cardElevation(6.dp),
                border = BorderStroke(1.dp, it.color),
                borderColor = it.color,
                borderSide = it.borderSide,
                borderStrokeWidth = 2.dp,
                cornerRadius = 10.dp,
                onClick = it.onClick
            ) {
                Column(
                    modifier = modifier.padding(10.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (it.imageRes != null) {
                        Icon(
                            modifier = Modifier.size(it.imageSize!!),
                            imageVector = it.imageRes,
                            contentDescription = "Icon for "+stringResource(it.title)+" tool",
                            tint = it.color
                        )
                    } else if (it.imageDrawable != null) {
                        Icon(
                            modifier = Modifier.size(it.imageSize!!),
                            painter = painterResource(it.imageDrawable),
                            contentDescription = "Icon for "+it.title+" tool",
                            tint = it.color
                        )
                    }

                    Text(text = stringResource(it.title), fontWeight = FontWeight.SemiBold, maxLines = 1)

                    Text(
                        text = stringResource(it.description),
                        modifier = Modifier.wrapContentWidth(Alignment.Start),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
fun IndexesCalculators(onToolSelect:() -> Unit) {

}

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun ToolsScreenPreview() {
    val app = LocalContext.current.applicationContext as? Application ?: return
    val vm = remember { OnboardingModel(app) }.also { it.finishOnboarding() }
    val qm = remember { QuoteModel(app) }
    val tm = remember { ToolsModel(app) }

    MainPortraitActivity(onboardingModel = vm, quoteModel = qm, toolsModel = tm, startingDestination = ToolsRoute)
}