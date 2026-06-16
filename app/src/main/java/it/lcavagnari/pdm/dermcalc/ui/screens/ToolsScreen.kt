package it.lcavagnari.pdm.dermcalc.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.navigation.BMIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.BSAToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.component.BorderSide
import it.lcavagnari.pdm.dermcalc.ui.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.preview.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalNavigate
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulBravery
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulIntegrity
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPatience
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPerseverance

@Preview(showBackground = true) @Composable private fun ToolsScreenFullPreview() {
    DermCalcPreview(screen = ToolsRoute, setupOm = { it.finishOnboarding() })
}
@Preview(showBackground = true) @Composable private fun ToolsScreenFullDarkPreview() {
    DermCalcPreview(screen = ToolsRoute, darkTheme = true, setupOm = { it.finishOnboarding() })
}

/**
 * Metadata for a single tool entry rendered in [ToolsScreen].
 */
data class ToolCard(
    @StringRes val description: Int,
    val color: Color,
    val route: AppRoute,
    val imageSize: Dp? = 40.dp,
    val districtNum: Int? = null,
    val valueRange: Pair<Double, Double>? = null,
    val borderSide: BorderSide = BorderSide.Top
)

/**
 * Tools tab. Two vertically stacked sections, each containing clickable [ToolCard] entries.
 */
@Composable
fun ToolsScreen(toolsModel: ToolsModel) {
    val navigate = LocalNavigate.current
    val quick = listOf<ToolCard>(
        ToolCard(
            route = BMIToolRoute,
            description = R.string.tools_bmi_description,
            borderSide = BorderSide.Top,
            color = SoulPatience
        ),
        ToolCard(
            route = BSAToolRoute,
            description = R.string.tools_bsa_description,
            borderSide = BorderSide.Top,
            color = SoulBravery
        )
    )

    val index = listOf<ToolCard>(
        ToolCard(
            route = PASIToolRoute,
            description = R.string.tools_pasi_description,
            borderSide = BorderSide.Left,
            color = SoulIntegrity,
            districtNum = 4,
            valueRange = Pair(0.0,72.0)
        ),
        ToolCard(
            route = EASIToolRoute,
            description = R.string.tools_easi_description,
            borderSide = BorderSide.Left,
            color = SoulPerseverance,
            districtNum = 4,
            valueRange = Pair(0.0,72.0)
        )
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
    ) {
        Text(
            text = stringResource(R.string.tools_quick).uppercase(),
            modifier = Modifier.padding(vertical = 15.dp).fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )

        QuickCalculators(toolsList = quick) { navigate(it) }

        Text(
            text = stringResource(R.string.tools_index).uppercase(),
            modifier = Modifier.padding(vertical = 15.dp).fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )

        IndexesCalculators(toolsList = index) { navigate(it) }
    }
}

/**
 * Row of quick-calculator [ToolCard] entries.
 * 
 * Displays a horizontal row of calculator cards for quick access to tools (BMI, BSA).
 * 
 * @param modifier optional modifier for layout customization
 * @param toolsList list of [ToolCard] entries to display
 * @param onClick callback invoked when a tool card is clicked
 */
@Composable
fun QuickCalculators(modifier: Modifier = Modifier, toolsList:List<ToolCard>, onClick:(route: AppRoute) -> Unit = {}) {
    Row(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Max),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
    ) {
        toolsList.forEach {
            val destination = it.route
            BorderedCard(
                modifier = Modifier.weight(1f).padding(5.dp).fillMaxHeight(),
                elevation = CardDefaults.cardElevation(6.dp),
                border = BorderStroke(1.dp, it.color),
                borderColor = it.color,
                borderSide = it.borderSide,
                borderStrokeWidth = 2.dp,
                cornerRadius = 10.dp,
                onClick = { onClick(destination) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth()
                ) {
                    if (destination.iconRes != null) {
                        Icon(
                            modifier = Modifier.size(it.imageSize!!),
                            painter = painterResource(destination.iconRes!!),
                            contentDescription = "Icon for " + destination.title + " tool",
                            tint = it.color
                        )
                    }

                    Column(
                        modifier = Modifier.padding(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = stringResource(destination.title!!),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = stringResource(it.description),
                            modifier = Modifier.wrapContentWidth(Alignment.Start),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Column of index-calculator [ToolCard] entries.
 * 
 * Displays a vertical column of calculator cards for index-based tools.
 * 
 * @param modifier optional modifier for layout customization
 * @param toolsList list of [ToolCard] entries to display
 * @param onClick callback invoked when a tool card is clicked
 */
@Composable
fun IndexesCalculators(modifier: Modifier = Modifier, toolsList:List<ToolCard>, onClick:(route: AppRoute) -> Unit = {}) {
    Column(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Max),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        val surface = MaterialTheme.colorScheme.surface
        toolsList.forEach {
            val destination = it.route
            val title = destination.title!!
            val icon = destination.iconRes
            val onColor: Color = lerp(surface, it.color, 0.22f)

            BorderedCard(
                modifier = Modifier.weight(1f).padding(5.dp).fillMaxHeight(),
                elevation = CardDefaults.cardElevation(6.dp),
                border = BorderStroke(1.dp, it.color),
                borderColor = it.color,
                borderSide = it.borderSide,
                borderStrokeWidth = 2.dp,
                cornerRadius = 10.dp,
                onClick = { onClick(destination) }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(6.dp),
                        border = BorderStroke(1.dp, it.color),
                        colors = CardDefaults.cardColors(containerColor = onColor)
                    ) {
                        if (icon != null) {
                            Icon(
                                modifier = Modifier.size(it.imageSize!!).padding(7.dp),
                                painter = painterResource(icon),
                                contentDescription = "Icon for $title tool",
                                tint = it.color
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.Top)
                    ) {
                        Text(
                            text = stringResource(title),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = stringResource(it.description),
                            modifier = Modifier.wrapContentWidth(Alignment.Start),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                    }
                }
                DistrictsCards(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp, end = 10.dp),
                    tool = it, onColor = onColor
                )
            }
        }
    }
}

@Composable
private fun DistrictsCards(
    modifier: Modifier = Modifier,
    tool: ToolCard,
    onColor: Color
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
        verticalAlignment = Alignment.Bottom
    ) {
        if(tool.districtNum != null) Card(
            elevation = CardDefaults.cardElevation(4.dp),
            border = BorderStroke(1.dp, onColor),
            colors = CardDefaults.cardColors(
                containerColor = onColor,
                contentColor = tool.color
            )
        ) { Text(
            "${tool.districtNum} ${stringResource(R.string.districts)}",
            modifier = Modifier.padding(horizontal = 5.dp),
            fontWeight = FontWeight.Thin,
            fontSize = 20.sp
        ) }

        if(tool.valueRange != null) Card(
            elevation = CardDefaults.cardElevation(6.dp),
            border = BorderStroke(1.dp, onColor),
            colors = CardDefaults.cardColors(
                containerColor = onColor,
                contentColor = tool.color
            )
        ) { Text(
            "${tool.valueRange.first.toInt()}-${tool.valueRange.second.toInt()}",
            modifier = Modifier.padding(horizontal = 5.dp),
            fontWeight = FontWeight.Thin,
            fontSize = 20.sp
        ) }
    }
}
