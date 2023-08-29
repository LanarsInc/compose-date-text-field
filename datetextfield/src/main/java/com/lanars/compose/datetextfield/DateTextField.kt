package com.lanars.compose.datetextfield

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.BackspaceCommand
import androidx.compose.ui.text.input.CommitTextCommand
import androidx.compose.ui.text.input.DeleteSurroundingTextCommand
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputSession
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lanars.compose.datetextfield.utils.DateUtils.localDateToFieldMap
import org.threeten.bp.LocalDate
import kotlin.jvm.optionals.getOrElse

/**
 * Date text field with on the fly validation
 *
 * @param modifier the [Modifier] to be applied to this field
 * @param initialValue pre-filled [LocalDate] value
 * @param onValueChanged triggered when the date is changed. A [LocalDate] object comes as a
 * parameter of the callback if the date is entered completely or `null` otherwise
 * @param format input date format specified with [Format] enum
 * @param minDate minimum allowed date
 * @param maxDate maximum allowed date
 * @param delimiter character that separates date parts
 * @param cursorBrush cursor brush style
 * @param textStyle the style to be applied to the input text
 * @param hintTextStyle the style to be applied to the hint text
 * @param delimiterSpacing horizontal margin of the delimiter
 * @param readOnly when `true`, the field can not be edited
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateTextField(
    modifier: Modifier = Modifier,
    initialValue: LocalDate? = null,
    onValueChanged: (LocalDate?) -> Unit = {},
    format: Format = Format.DDMMYYYY,
    minDate: LocalDate = LocalDate.of(1900, 1, 1),
    maxDate: LocalDate = LocalDate.of(2100, 12, 31),
    delimiter: Char = '/',
    cursorBrush: Brush = SolidColor(Color.Black),
    textStyle: TextStyle = DateTextFieldDefaults.MainTextStyle,
    hintTextStyle: TextStyle = DateTextFieldDefaults.HintTextStyle,
    delimiterSpacing: Dp = DateTextFieldDefaults.DelimiterSpacing,
    readOnly: Boolean = false,
) {
    require(maxDate >= minDate) { "The maximum date cannot be less than the minimum date" }
    require(initialValue == null || initialValue in minDate..maxDate) { "Value must be greater than or equal to minDate and less than or equal to maxDate" }

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

    val state = remember {
        DateTextFieldState(
            dateFormat,
            localDateToFieldMap(initialValue),
            onValueChanged
        )
    }

    val keyboardActionRunner = remember {
        KeyboardActionRunner(KeyboardActions(), focusManager)
    }

    LaunchedEffect(state.hasFocus, readOnly) {
        if (state.hasFocus && !readOnly) {
            inputSession = textInputService?.startInput(
                value = TextFieldValue(),
                imeOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ).toImeOptions(),
                onEditCommand = { operations ->
                    operations.forEach { operation ->
                        when (operation) {
                            is DeleteSurroundingTextCommand,
                            is BackspaceCommand -> state.onBackspace()

                            is CommitTextCommand -> operation.text.forEach(state::onEnterDigit)
                        }
                    }
                },
                onImeActionPerformed = keyboardActionRunner::runAction
            )
            keyboardActionRunner.inputSession = inputSession
        } else {
            inputSession?.let { textInputService?.stopInput(it) }
        }
    }

    Row(
        modifier = modifier
            .focusGroup()
            .onKeyEvent { event ->
                state.onKeyEvent(event)
                false
            }
    ) {
        val cursorAlpha = cursorAlphaAnimatable(state)

        dateFormat.fields.forEachIndexed { index, field ->
            val fieldState = state.fieldsState[field]!!
            Box(
                modifier = Modifier
                    .alignByBaseline()
                    .focusRequester(fieldState.focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            state.focusedField = field
                        } else if (state.focusedField == field) {
                            state.focusedField = null
                        }
                    }
                    .focusable()
                    .noRippleClickable(
                        enabled = !readOnly
                    ) {
                        fieldState.focusRequester.requestFocus()
                        inputSession?.showSoftwareKeyboard()
                    },
                contentAlignment = Alignment.BottomStart
            ) {
                Row {
                    val fieldText = state.valueForField(field)
                    for (position in 0 until field.length) {
                        val char = fieldText.getOrNull(position)
                        Box(
                            modifier = Modifier
                                .alignByBaseline()
                                .cursor(
                                    state,
                                    field,
                                    cursorBrush,
                                    { cursorAlpha.value },
                                    position
                                ),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            BasicText(
                                stringResource(field.placeholderRes),
                                style = hintTextStyle,
                                modifier = Modifier.alpha(if (char == null) 1f else 0f)
                            )
                            if (char != null) {
                                BasicText(char.toString(), style = textStyle)
                            }
                        }
                    }
                }
            }

            if (index < dateFormat.fields.size - 1) {
                BasicText(
                    delimiter.toString(),
                    style = hintTextStyle,
                    modifier = Modifier
                        .padding(horizontal = delimiterSpacing)
                        .alignByBaseline()
                )
            }
        }
    }
}

@Composable
internal fun cursorAlphaAnimatable(state: DateTextFieldState): Animatable<Float, AnimationVector1D> {
    val cursorAlpha = remember { Animatable(1f) }
    LaunchedEffect(
        state.valueForField(DateField.Day),
        state.valueForField(DateField.Month),
        state.valueForField(DateField.Year),
        state.focusedField
    ) {
        cursorAlpha.snapTo(1f)
        cursorAlpha.animateTo(
            0f,
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
    return cursorAlpha
}

internal fun Modifier.cursor(
    state: DateTextFieldState,
    field: DateField,
    brush: Brush,
    alphaProvider: () -> Float,
    position: Int
): Modifier {
    val fieldText = state.valueForField(field)
    val isFocused = state.focusedField == field
    return drawWithContent {
        drawContent()
        if (isFocused && fieldText.length == position) {
            drawLine(
                brush = brush,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = 2.dp.toPx(),
                alpha = alphaProvider()
            )
        } else if (isFocused && fieldText.length == field.length && position == field.length - 1) {
            drawLine(
                brush = brush,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 2.dp.toPx(),
                alpha = alphaProvider()
            )
        }
    }
}

internal fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
) = clickable(MutableInteractionSource(), indication = null, enabled = enabled, onClick = onClick)

internal fun KeyboardOptions.toImeOptions() =
    ImeOptions(
        singleLine = true,
        capitalization = capitalization,
        autoCorrect = autoCorrect,
        keyboardType = keyboardType,
        imeAction = imeAction
    )
