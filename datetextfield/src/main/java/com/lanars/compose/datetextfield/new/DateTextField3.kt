package com.lanars.compose.datetextfield.new

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputSession
import androidx.compose.ui.unit.dp
import com.lanars.compose.datetextfield.DateField
import com.lanars.compose.datetextfield.DateFormat
import com.lanars.compose.datetextfield.DateTextFieldDefaults
import com.lanars.compose.datetextfield.Format
import org.threeten.bp.LocalDate
import kotlin.jvm.optionals.getOrElse

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateTextField3(
    modifier: Modifier = Modifier,
    format: Format = Format.DDMMYYYY,
    minDate: LocalDate = LocalDate.of(1900, 1, 1),
    maxDate: LocalDate = LocalDate.of(2100, 12, 31),
    delimiter: String = "/",
    cursorBrush: Brush = SolidColor(MaterialTheme.colors.primary),
    keyboardOptions: KeyboardOptions = DateTextFieldDefaults.KeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions(),
    textStyle: TextStyle = DateTextFieldDefaults.MainTextStyle,
    hintTextStyle: TextStyle = DateTextFieldDefaults.HintTextStyle
) {
    val dateFormat by remember {
        val factory = DateFormat.Factory()
        factory.minDate = minDate.atTime(0, 0)
        factory.maxDate = maxDate.atTime(0, 0)
        mutableStateOf(
            factory.createSpecificFormat(format).getOrElse {
                factory.createDefaultFormat()
            }
        )
    }

    val textInputService = LocalTextInputService.current
    val focusManager = LocalFocusManager.current

    var inputSession by remember { mutableStateOf<TextInputSession?>(null) }

    val state = remember { DateTextFieldState(dateFormat) }

    val keyboardActionRunner = remember(keyboardActions) {
        KeyboardActionRunner(keyboardActions, focusManager)
    }

    LaunchedEffect(state.hasFocus) {
        if (state.hasFocus) {
            inputSession = textInputService?.startInput(
                value = TextFieldValue(),
                imeOptions = keyboardOptions.toImeOptions(),
                onEditCommand = { commands ->
                    // TODO: implement
                },
                onImeActionPerformed = keyboardActionRunner::runAction
            )
            keyboardActionRunner.inputSession = inputSession
        }
    }

    val delimiterText = @Composable {
        Text(
            delimiter,
            style = hintTextStyle,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }

    Row(
        modifier = modifier
            .focusGroup()
            .onKeyEvent { event ->
                state.onKeyEvent(event)
                false
            }
    ) {
        val cursorAlpha = remember { Animatable(1f) }
        LaunchedEffect(
            state.valueForField(DateField.Day),
            state.valueForField(DateField.Month),
            state.valueForField(DateField.Year),
            state.focusedField
        ) {
            // Animate the cursor even when animations are disabled by the system.
            cursorAlpha.snapTo(1f)
            // then start the cursor blinking on animation clock (500ms on to start)
            cursorAlpha.animateTo(0f,
                infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1000
                        1f at 0
                        1f at 499
                        0f at 500
                        0f at 999
                    }
                )
            )
        }

        dateFormat.fields.forEachIndexed { index, field ->
            val fieldState = state.fieldsState[field]!!
            Box(
                modifier = Modifier
                    .focusRequester(fieldState.focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            state.focusedField = field
                        } else if (state.focusedField == field) {
                            state.focusedField = null
                        }
                    }
                    .focusable()
                    .noRippleClickable {
                        fieldState.focusRequester.requestFocus()
                        inputSession?.showSoftwareKeyboard()
                    }
            ) {
                Row {
                    val fieldText = state.valueForField(field)
                    val isFocused = state.focusedField == field
                    for (i in 0 until field.length) {
                        val char = fieldText.getOrNull(i)
                        Box(
                            modifier = Modifier.drawWithContent {
                                drawContent()
                                if (isFocused && fieldText.length == i) {
                                    drawLine(
                                        brush = cursorBrush,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = 2.dp.toPx(),
                                        alpha = cursorAlpha.value
                                    )
                                } else if (isFocused && fieldText.length == field.length && i == field.length - 1) {
                                    drawLine(
                                        brush = cursorBrush,
                                        start = Offset(size.width, 0f),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = 2.dp.toPx(),
                                        alpha = cursorAlpha.value
                                    )
                                }
                            }
                        ) {
                            Text(
                                stringResource(field.placeholderRes),
                                style = hintTextStyle,
                                modifier = Modifier.alpha(if (char == null) 1f else 0f)
                            )
                            if (char != null) {
                                Text(char.toString(), style = textStyle)
                            }
                        }
                    }
                }
            }

            if (index < dateFormat.fields.size - 1) {
                delimiterText()
            }
        }
    }
}

internal fun Modifier.noRippleClickable(onClick: () -> Unit) =
    clickable(MutableInteractionSource(), indication = null, onClick = onClick)

internal fun KeyboardOptions.toImeOptions() =
    ImeOptions(
        singleLine = true,
        capitalization = capitalization,
        autoCorrect = autoCorrect,
        keyboardType = keyboardType,
        imeAction = imeAction
    )
