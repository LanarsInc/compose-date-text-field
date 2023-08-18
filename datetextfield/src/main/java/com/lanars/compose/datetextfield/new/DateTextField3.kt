package com.lanars.compose.datetextfield.new

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.input.BackspaceCommand
import androidx.compose.ui.text.input.CommitTextCommand
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputSession
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DateTextField3(
    modifier: Modifier = Modifier,
    delimiter: String = "/"
) {
    val textInputService = LocalTextInputService.current

    var inputSession by remember { mutableStateOf<TextInputSession?>(null) }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    var dayValue by remember { mutableStateOf("") }
    var monthValue by remember { mutableStateOf("") }
    var yearValue by remember { mutableStateOf("") }

    val dayFocusRequester = remember { FocusRequester() }
    val monthFocusRequester = remember { FocusRequester() }
    val yearFocusRequester = remember { FocusRequester() }

    val (day, month, year) = FocusRequester.createRefs()

    var anyChildHasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            inputSession = textInputService?.startInput(
                value = textFieldValue,
                imeOptions = ImeOptions(
                    singleLine = true,
                    autoCorrect = false,
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                onEditCommand = { commands ->
                    commands.forEach { command ->
                        when (command) {
                            is BackspaceCommand -> {}
                            is CommitTextCommand -> {
                                textFieldValue = textFieldValue.copy(command.text)
                            }
                        }
                    }
                },
                onImeActionPerformed = { action ->
                    // TODO: implement
                    action
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

    val onFocusChangedModifier = Modifier.onFocusChanged { focusState ->
        anyChildHasFocus = focusState.isFocused
    }

    Row(
        modifier = modifier
            .focusGroup()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    if (event.isDigit()) {
                        val newChar = event.utf16CodePoint.toChar()

                        if (dayValue.length < 2) {
                            dayValue += newChar
                        } else if (monthValue.length < 2) {
                            monthValue += newChar
                        } else if (yearValue.length < 4) {
                            yearValue += newChar
                        }
                    } else if (event.key == Key.Backspace) {
                        if (yearValue.isNotEmpty()) {
                            yearValue = yearValue.dropLast(1)
                        } else if (monthValue.isNotEmpty()) {
                            monthValue = monthValue.dropLast(1)
                        } else if (dayValue.isNotEmpty()) {
                            dayValue = dayValue.dropLast(1)
                        }
                    }
                    /*if (event.isDigit()) {
                        textFieldValue =
                            textFieldValue.copy(textFieldValue.text + event.utf16CodePoint.toChar())
                    } else if (event.key == Key.Backspace) {
                        if (textFieldValue.text.isNotEmpty()) {
                            textFieldValue =
                                textFieldValue.copy(textFieldValue.text.dropLast(1))
                        }
                    }*/
                }
                false
            }
    ) {
        Box(
            modifier = Modifier
                .focusRequester(day)
                .focusable(interactionSource = interactionSource)
                .noRippleClickable {
                    day.requestFocus()
                }
        ) {
            Text("DD", style = MaterialTheme.typography.h3.copy(Color.Gray))
            Text(dayValue, style = MaterialTheme.typography.h3)
        }

        delimiterText()

        Box(
            modifier = Modifier
                .focusRequester(month)
                .focusable(interactionSource = interactionSource)
                .noRippleClickable {
                    month.requestFocus()
                }
        ) {
            Text("MM", style = MaterialTheme.typography.h3.copy(Color.Gray))
            Text(monthValue, style = MaterialTheme.typography.h3)
        }

        delimiterText()

        Box(
            modifier = Modifier
                .focusRequester(year)
                .focusable(interactionSource = interactionSource)
                .noRippleClickable {
                    year.requestFocus()
                }
        ) {
            Text("YYYY", style = MaterialTheme.typography.h3.copy(Color.Gray))
            Text(yearValue, style = MaterialTheme.typography.h3)
        }
    }
}

internal fun KeyEvent.isDigit(): Boolean {
    return key.nativeKeyCode in android.view.KeyEvent.KEYCODE_0..android.view.KeyEvent.KEYCODE_9
}

fun String.takeAvailable(n: Int): String {
    if (this.length < n) {
        return this
    }
    return take(n)
}

fun Modifier.noRippleClickable(onClick: () -> Unit) =
    clickable(MutableInteractionSource(), indication = null, onClick = onClick)
