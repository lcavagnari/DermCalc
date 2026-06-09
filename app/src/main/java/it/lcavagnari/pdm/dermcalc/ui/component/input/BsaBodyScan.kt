package it.lcavagnari.pdm.dermcalc.ui.component.input

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BsaRegion
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulBravery

private data class RegionDef(
    val region: BsaRegion,
    val l: Dp, val t: Dp, val r: Dp, val b: Dp,
    val isEllipse: Boolean = false,
    val corner: Dp = 10.dp,
)

private val REGION_DEFS = listOf(
    RegionDef(BsaRegion.HEAD,           l=58.dp,  t=2.dp,   r=102.dp, b=54.dp,  isEllipse=true),
    RegionDef(BsaRegion.RIGHT_ARM,      l=10.dp,  t=57.dp,  r=38.dp,  b=139.dp),
    RegionDef(BsaRegion.ANTERIOR_TRUNK, l=42.dp,  t=57.dp,  r=118.dp, b=155.dp, corner=6.dp),
    RegionDef(BsaRegion.LEFT_ARM,       l=122.dp, t=57.dp,  r=150.dp, b=139.dp),
    RegionDef(BsaRegion.RIGHT_LEG,      l=42.dp,  t=158.dp, r=77.dp,  b=280.dp),
    RegionDef(BsaRegion.LEFT_LEG,       l=83.dp,  t=158.dp, r=118.dp, b=280.dp),
)

private data class ResolvedRegion(
    val region: BsaRegion,
    val rect: Rect,
    val isEllipse: Boolean,
    val corner: Float,
) {
    fun contains(offset: Offset): Boolean {
        if (!rect.contains(offset)) return false
        else if (!isEllipse) return true
        val dx = (offset.x - rect.center.x) / (rect.width / 2f)
        val dy = (offset.y - rect.center.y) / (rect.height / 2f)

        return dx * dx + dy * dy <= 1f
    }
}

@Composable
fun BsaBodyDiagram(
    regionValues: Map<BsaRegion, Int>,
    selectedRegion: BsaRegion?,
    onRegionSelected: (BsaRegion) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val resolved = remember(density) {
        REGION_DEFS.map { def ->
            with(density) {
                ResolvedRegion(
                    region = def.region,
                    rect = Rect(def.l.toPx(), def.t.toPx(), def.r.toPx(), def.b.toPx()),
                    isEllipse = def.isEllipse,
                    corner = def.corner.toPx(),
                )
            }
        }
    }

    val defaultFill    = MaterialTheme.colorScheme.surfaceVariant
    val selectedFill   = MaterialTheme.colorScheme.primary
    val filledFill     = MaterialTheme.colorScheme.primaryContainer
    val defaultStroke  = MaterialTheme.colorScheme.outline
    val selectedStroke = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .size(160.dp, 290.dp)
            .pointerInput(Unit) {
                detectTapGestures { tap ->
                    resolved.firstOrNull { it.contains(tap) }
                        ?.region
                        ?.let(onRegionSelected)
                }
            }
    ) {
        resolved.forEach { r ->
            val isSelected = r.region == selectedRegion
            val hasValue   = (regionValues[r.region] ?: 0) > 0
            val fill   = if (isSelected) selectedFill else if (hasValue) filledFill else defaultFill
            val stroke = if (isSelected) selectedStroke else defaultStroke
            val sw     = if (isSelected) 2.dp.toPx() else 1.5f

            if (r.isEllipse) {
                drawOval(fill,   topLeft = r.rect.topLeft, size = r.rect.size)
                drawOval(stroke, topLeft = r.rect.topLeft, size = r.rect.size, style = Stroke(sw))
            } else {
                val cr = CornerRadius(r.corner)
                drawRoundRect(fill,   topLeft = r.rect.topLeft, size = r.rect.size, cornerRadius = cr)
                drawRoundRect(stroke, topLeft = r.rect.topLeft, size = r.rect.size, cornerRadius = cr, style = Stroke(sw))
            }
        }
    }
}

@Composable
fun BsaRegionSlider(
    selectedRegion: BsaRegion?,
    regionValues: Map<BsaRegion, Int>,
    onValueChange: (BsaRegion, Int) -> Unit,
) {
    val region = selectedRegion ?: return
    val current = (regionValues[region] ?: 0).toFloat()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = region.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${current.toInt()} %",
                style = MaterialTheme.typography.labelMedium
            )
        }
        Slider(
            value = current,
            onValueChange = { onValueChange(region, it.toInt()) },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = SoulBravery,
                activeTrackColor = SoulBravery
            )
        )
    }
}

@Composable
fun BsaTotalRow(totalBsaPct: Float) {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.bsa_total_label),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "%.1f %%".format(totalBsaPct),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SoulBravery
        )
    }
}

@Composable
fun PosteriorTrunkRow(
    regionValues: Map<BsaRegion, Int>,
    onValueChange: (BsaRegion, Int) -> Unit,
) {
    val region = BsaRegion.POSTERIOR_TRUNK
    val current = (regionValues[region] ?: 0).toFloat()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = region.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${current.toInt()} %",
                style = MaterialTheme.typography.labelMedium
            )
        }
        Slider(
            value = current,
            onValueChange = { onValueChange(region, it.toInt()) },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = SoulBravery,
                activeTrackColor = SoulBravery
            )
        )
    }
}