package it.lcavagnari.pdm.dermcalc.ui.portrait.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlin.math.abs


data class SnapWheel<T>(
    val height: Dp = 260.dp,
    val visibleItemCount: Int = 5,
    val infinite: Boolean = true,
    val initialValue: T,
    val items: List<T>,
    val onValueChanged: (T) -> Unit = {},
)


/**
 * Generic infinitely-scrolling (or finite) wheel picker.
 * Renders items via [itemContent] and reports the centred selection via [SnapWheel.onValueChanged].
 * Source: https://stackoverflow.com/a/73968517 (CC BY-SA 4.0)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T> DrawSnapWheel(
    modifier: Modifier = Modifier,
    wheel: SnapWheel<T>,
    itemContent: @Composable (item: T, isFocused: Boolean) -> Unit = { item, isFocused ->
        Text(
            text = String.format("%s", item),
            fontWeight = FontWeight.Bold,
            style = if (isFocused) MaterialTheme.typography.labelLarge
            else MaterialTheme.typography.labelMedium,
            color = if (isFocused) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.4f),
            fontSize = if (isFocused) 30.sp else 15.sp
        )
    }
) {
    val visibleItemCount = wheel.visibleItemCount
    val infinite = wheel.infinite
    val items = wheel.items

    // Even visibleItemCount would put the viewport centre exactly on a cell boundary,
    // making two items equidistant — bump to the next odd number to guarantee a single centred cell.
    val cellSize =
        wheel.height / if (wheel.visibleItemCount % 2 == 0) wheel.visibleItemCount + 1 else visibleItemCount
    val paddingCount = (visibleItemCount / 2)

    val cycleSize = items.size
    // 10,000× is enough for infinite feel; 10,000,000× risks Int overflow on large lists.
    val expandedSize = if (infinite) cycleSize * 10_000 else cycleSize + paddingCount * 2

    val initialListPoint = if (infinite) expandedSize / 2 else paddingCount
    val targetIndex = initialListPoint + (items.indexOf(wheel.initialValue).coerceAtLeast(0))

    val visibleCell = wheel.height.value / cellSize.value
    // Start scroll so initialValue lands at the visual centre, not the top.
    val scrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = (targetIndex - (visibleCell / 2).toInt()).coerceAtLeast(0)
    )

    Box(
        modifier = modifier
            .height(wheel.height)
            .wrapContentWidth()
    ) {
        // Pixel-level centering: compare each item's midpoint to the viewport midpoint
        // rather than using firstVisibleItemIndex, which only gives row-level granularity.
        val centeredIndex by remember {
            derivedStateOf {
                val layoutInfo = scrollState.layoutInfo
                val viewportCenter =
                    (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                layoutInfo.visibleItemsInfo.minByOrNull { info ->
                    abs((info.offset + info.size / 2) - viewportCenter)
                }?.index ?: 0
            }
        }

        // Map the virtual expanded index back to a real item index.
        val selectedIndex by remember {
            derivedStateOf {
                if (infinite) centeredIndex % cycleSize
                else (centeredIndex - paddingCount).coerceIn(0, cycleSize - 1)
            }
        }

        // Also fires on first composition, which seeds the dialog's selectedValues
        // with the correct initial item rather than waiting for the first scroll.
        LaunchedEffect(selectedIndex) {
            wheel.onValueChanged(items[selectedIndex])
        }

        LazyColumn(
            modifier = Modifier.wrapContentWidth(),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(scrollState)
        ) {
            items(expandedSize, itemContent = {
                val isDataItem = infinite || (it >= paddingCount && it < paddingCount + cycleSize)
                val dataIndex = if (infinite) it % cycleSize else it - paddingCount

                Box(
                    modifier = Modifier.size(cellSize),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDataItem) {
                        val isFocused = it == centeredIndex
                        itemContent(items[dataIndex], isFocused)
                    }
                }
            })
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun SnapWheelPickerDialog(
    modifier: Modifier = Modifier,
    title: String = "Scroll to pick a value",
    tonalElevation: Dp = DatePickerDefaults.TonalElevation,
    colors: DatePickerColors = DatePickerDefaults.colors(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    wheels: List<SnapWheel<*>>,
    onDismiss: () -> Unit,
    onConfirm: (List<Any?>) -> Unit
) {
    // Dialog owns the selection state. Each wheel's onValueChanged is overridden below
    // to write into this list, so onConfirm can return all current values at once.
    val selectedValues = remember {
        mutableStateListOf(*wheels.map { it.initialValue }.toTypedArray())
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.wrapContentHeight(),
        properties = properties
    ) {
        Surface(
            modifier = Modifier
                .requiredWidth(360.dp)
                .heightIn(max = 568.dp),
            shape = MaterialTheme.shapes.large,
            color = colors.containerColor,
            tonalElevation = tonalElevation,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        wheels.forEachIndexed { index, config ->
                            // Safe: T is erased at runtime; values stored in selectedValues
                            // came from the same items list, so the cast holds.
                            @Suppress("UNCHECKED_CAST")
                            DrawSnapWheel(
                                wheel = (config as SnapWheel<Any?>).copy(
                                    onValueChanged = { selectedValues[index] = it }
                                ),
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    }
                }

                Box(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.padding(end = 5.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f),
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Cancel") }

                        OutlinedButton(
                            onClick = { onConfirm(selectedValues.toList()) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) { Text("OK") }
                    }
                }
            }
        }
    }
}