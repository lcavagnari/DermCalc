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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import it.lcavagnari.pdm.dermcalc.R
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import kotlin.math.abs


/**
 * Configuration for a single scrollable column inside [SnapWheelPickerDialog].
 *
 * @property height - total height of the wheel composable. Defaults to 260.dp.
 * @property visibleItemCount - number of items visible at once. Must be odd; bumped up if even.
 * @property infinite - whether the list wraps around infinitely.
 * @property initialValue - item shown at the centre on first composition.
 * @property items - full list of selectable items.
 * @property onValueChanged - callback invoked with the centred item on every scroll settle.
 * @constructor Create empty Snap wheel
 */
data class SnapWheel<T>(
    val height: Dp = 260.dp,
    val visibleItemCount: Int = 5,
    val infinite: Boolean = false,
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
            fontSize = if (isFocused) 30.sp else 15.sp,
            softWrap = false
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
    @StringRes title: Int = R.string.picker_default_title,
    tonalElevation: Dp = DatePickerDefaults.TonalElevation,
    colors: DatePickerColors = DatePickerDefaults.colors(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    wheels: List<SnapWheel<*>>,
    @StringRes inputFieldLabels: List<Int> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (List<Any?>) -> Unit
) {
    val selectedValues = remember {
        mutableStateListOf(*wheels.map { it.initialValue }.toTypedArray())
    }

    val hasInputMode = inputFieldLabels.size == wheels.size && inputFieldLabels.isNotEmpty()
    var isInputMode by remember { mutableStateOf(false) }
    val inputTexts = remember {
        mutableStateListOf(*wheels.map { it.initialValue.toString() }.toTypedArray())
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(title),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        if (hasInputMode) {
                            IconButton(onClick = {
                                isInputMode = !isInputMode
                                if (isInputMode) {
                                    wheels.forEachIndexed { i, _ ->
                                        inputTexts[i] = selectedValues[i].toString()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = if (isInputMode) Icons.Default.ViewCarousel
                                        else Icons.Default.Keyboard,
                                    contentDescription = if (isInputMode) "Switch to wheel"
                                        else "Switch to text input",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                if (isInputMode && hasInputMode) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        wheels.forEachIndexed { index, _ ->
                            val items = wheels[index].items
                            val minVal = (items.firstOrNull() as? Int) ?: 0
                            val maxVal = (items.lastOrNull() as? Int) ?: 0
                            OutlinedTextField(
                                value = inputTexts[index],
                                onValueChange = { raw ->
                                    inputTexts[index] = raw
                                    raw.toIntOrNull()?.let { intVal ->
                                        if (intVal in minVal..maxVal) {
                                            selectedValues[index] = intVal
                                        }
                                    }
                                },
                                label = { Text(stringResource(inputFieldLabels[index])) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(17.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                } else {
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
                        ) { Text(stringResource(R.string.btn_cancel)) }

                        OutlinedButton(
                            onClick = { onConfirm(selectedValues.toList()) },
                            modifier = Modifier.semantics { testTag = "btn_confirm_picker" },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) { Text(stringResource(R.string.btn_ok)) }
                    }
                }
            }
        }
    }
}