package com.lanars.compose.datetextfield.new

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputSession
import androidx.compose.ui.unit.dp
import com.lanars.compose.datetextfield.DateField

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateTextField3(
    modifier: Modifier = Modifier,
    delimiter: String = "/"
) {
    val textInputService = LocalTextInputService.current

    var inputSession by remember { mutableStateOf<TextInputSession?>(null) }

    val fieldsToFocusRequesters = remember {
        mapOf(
            DateField.Day to FocusRequester(),
            DateField.Month to FocusRequester(),
            DateField.Year to FocusRequester()
        )
    }

    val state = remember { DateTextFieldState() }

    LaunchedEffect(state.focusedField != null) {
        if (state.hasFocus) {
            inputSession = textInputService?.startInput(
                value = TextFieldValue(),
                imeOptions = ImeOptions(
                    singleLine = true,
                    autoCorrect = false,
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                onEditCommand = { commands ->
                    // TODO: implement
                },
                onImeActionPerformed = { action ->
                    // TODO: implement
                }
            )
        }
    }

    val delimiterText = @Composable {
        Text(
            delimiter,
            style = MaterialTheme.typography.h3.copy(Color.Gray),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }

    Row(
        modifier = modifier
            .focusGroup()
            .onKeyEvent { event ->
                state.onKeyEvent(
                    event,
                    fieldsToFocusRequesters[DateField.Day]!!,
                    fieldsToFocusRequesters[DateField.Month]!!,
                    fieldsToFocusRequesters[DateField.Year]!!
                )
                false
            }
    ) {
        fieldsToFocusRequesters.forEach { (field, focusRequester) ->
            Box(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            state.focusedField = field
                        } else if (state.focusedField == field) {
                            state.focusedField = null
                        }
                    }
                    .focusable()
                    .noRippleClickable {
                        focusRequester.requestFocus()
                    }
            ) {
                Row {
                    val fieldText = state.valueForField(field)
                    for (i in 0 until field.length) {
                        val char = fieldText.getOrNull(i)
                        Box {
                            Text(
                                stringResource(field.placeholderRes),
                                style = MaterialTheme.typography.h3.copy(Color.Gray),
                                modifier = Modifier.alpha(if (char == null) 1f else 0f)
                            )
                            if (char != null) {
                                Text(char.toString(), style = MaterialTheme.typography.h3)
                            }
                        }
                    }
                }
            }

            delimiterText()
        }
    }
}

internal fun Modifier.noRippleClickable(onClick: () -> Unit) =
    clickable(MutableInteractionSource(), indication = null, onClick = onClick)
