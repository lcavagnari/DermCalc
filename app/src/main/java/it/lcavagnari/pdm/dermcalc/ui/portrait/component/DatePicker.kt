package it.lcavagnari.pdm.dermcalc.ui.portrait.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.DateInput
import it.lcavagnari.pdm.dermcalc.models.toEpochMillis
import it.lcavagnari.pdm.dermcalc.models.toLocalDate
import kotlinx.datetime.LocalDate

/**
 * Read-only date field that opens a Material3 [DatePickerDialog] on tap, bridging [kotlinx.datetime.LocalDate] to the epoch-millisecond API.
 *
 * @param field - the [DateInput] field supplying the current value and label.
 * @param onDateSelected - callback invoked with the chosen [kotlinx.datetime.LocalDate] on confirm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputPicker(field: DateInput, onDateSelected: (LocalDate) -> Unit) {
    var openPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) openPicker = true
        }
    }

    OutlinedTextField(
        value = field.value?.toString() ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(field.label), style = MaterialTheme.typography.labelMedium) },
        placeholder = { Text(stringResource(R.string.placeholder_date)) },
        trailingIcon = {
            IconButton(
                onClick = { openPicker = true },
                modifier = Modifier.semantics { testTag = "btn_open_date_picker" }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.cd_pick_date)
                )
            }
        },
        interactionSource = interactionSource,
        modifier = Modifier
            .padding(top = 20.dp)
            .semantics { testTag = "input_date_of_birth" },
        shape = RoundedCornerShape(17.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary
        ),
    )

    if (openPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = field.value?.toEpochMillis()
        )
        DatePickerDialog(
            onDismissRequest = { openPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onDateSelected(it.toLocalDate()) }
                        openPicker = false
                    },
                    modifier = Modifier.semantics { testTag = "btn_confirm_date" },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) { Text(stringResource(R.string.btn_ok)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { openPicker = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f),
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.btn_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
