package it.lcavagnari.pdm.dermcalc.ui.shared.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource

/**
 * Which edge of the card the border is drawn on.
 */
enum class BorderSide { Left, Right, Top, Bottom }

/**
 * Draw a one-sided border inside [DrawScope], respecting rounded corners.
 * Intended for use inside [androidx.compose.ui.draw.drawBehind].
 */
private fun DrawScope.drawOneSidedBorder(
    cornerRadiusPx: Float,
    borderSide: BorderSide,
    borderColor: Color,
    strokeWidthPx: Float,
) {
    val r = cornerRadiusPx
    when (borderSide) {
        BorderSide.Right -> {
            drawLine(
                color = borderColor,
                start = Offset(x = size.width, y = r),
                end = Offset(x = size.width, y = size.height - r),
                strokeWidth = strokeWidthPx
            )
            drawArc(
                color = borderColor,
                startAngle = 270f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(x = size.width - r * 2, y = 0f),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidthPx)
            )
            drawArc(
                color = borderColor,
                startAngle = 0f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(x = size.width - r * 2, y = size.height - r * 2),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidthPx)
            )
        }

        BorderSide.Left -> {
            drawLine(
                color = borderColor,
                start = Offset(x = 0f, y = r),
                end = Offset(x = 0f, y = size.height - r),
                strokeWidth = strokeWidthPx
            )
            drawArc(
                color = borderColor,
                startAngle = 180f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(x = 0f, y = 0f),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidthPx)
            )
            drawArc(
                color = borderColor,
                startAngle = 90f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(x = 0f, y = size.height - r * 2),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidthPx)
            )
        }

        BorderSide.Top -> {
            drawLine(
                color = borderColor,
                start = Offset(x = r, y = 0f),
                end = Offset(x = size.width - r, y = 0f),
                strokeWidth = strokeWidthPx
            )
            drawArc(
                color = borderColor,
                startAngle = 180f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(x = 0f, y = 0f),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidthPx)
            )
            drawArc(
                color = borderColor,
                startAngle = 270f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(x = size.width - r * 2, y = 0f),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidthPx)
            )
        }

        BorderSide.Bottom -> {
            drawLine(
                color = borderColor,
                start = Offset(x = r, y = size.height),
                end = Offset(x = size.width - r, y = size.height),
                strokeWidth = strokeWidthPx
            )
            drawArc(
                color = borderColor,
                startAngle = 90f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(x = 0f, y = size.height - r * 2),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidthPx)
            )
            drawArc(
                color = borderColor,
                startAngle = 0f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(x = size.width - r * 2, y = size.height - r * 2),
                size = Size(r * 2, r * 2),
                style = Stroke(width = strokeWidthPx)
            )
        }
    }
}

/**
 * A [Card] with a colored border drawn on one side only, respecting rounded corners.
 *
 * This is a drop-in subset of [Card] that matches its full API — every standard
 * parameter is preserved — and adds [borderSide], [borderColor], [borderStrokeWidth],
 * and [cornerRadius] to control the one-sided border.
 *
 * The shape is forced to [RoundedCornerShape] using [cornerRadius] so that the
 * drawn border arcs align with the card corners.
 *
 * @param borderSide   Which edge gets the colored line (default [BorderSide.Right]).
 * @param borderColor  Color of the one-sided border (default [MaterialTheme.colorScheme.primary]).
 * @param borderStrokeWidth Width of the one-sided border line (default 1.dp).
 * @param cornerRadius Radius of card corners — also used for the drawn arcs (default 16.dp).
 */
@Composable
fun BorderedCard(
    modifier: Modifier = Modifier,
    borderSide: BorderSide = BorderSide.Right,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderStrokeWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content,
        modifier = modifier.drawBehind {
            drawOneSidedBorder(
                cornerRadiusPx = cornerRadius.toPx(),
                borderSide = borderSide,
                borderColor = borderColor,
                strokeWidthPx = borderStrokeWidth.toPx(),
            )
        }
    )
}

/**
 * Clickable variant of [BorderedCard].
 *
 * Mirrors the clickable [Card] overload — same parameters, same behavior — with
 * the one-sided border added.
 */
@Composable
fun BorderedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderSide: BorderSide = BorderSide.Right,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderStrokeWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        modifier = modifier.drawBehind {
            drawOneSidedBorder(
                cornerRadiusPx = cornerRadius.toPx(),
                borderSide = borderSide,
                borderColor = borderColor,
                strokeWidthPx = borderStrokeWidth.toPx(),
            )
        }
    ) {
        content()
    }
}
