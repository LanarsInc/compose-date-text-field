package com.lanars.compose.datetextfield

import android.view.KeyEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate

@Composable
fun DateTextField2(
    modifier: Modifier = Modifier,
    format: Format = Format.MMDDYYYY,
    minDate: LocalDate = LocalDate.of(1900, 1, 1),
    maxDate: LocalDate = LocalDate.of(2100, 12, 31),
    delimiter: Char = '/',
    value: LocalDate? = null
) {
    val dateFormat by remember {
        val factory = DateFormat.Factory()
        factory.minDate = minDate.atTime(0, 0)
        factory.maxDate = maxDate.atTime(0, 0)
        mutableStateOf(factory.createSpecificFormat(format).orElseGet {
            factory.createDefaultFormat()
        })
    }

    val delimiterText = @Composable {
        Text(
            delimiter.toString(),
            style = MaterialTheme.typography.h3.copy(Color.Gray),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }

    // TODO: improve
    val requesters = remember {
        mutableStateListOf(
            FocusRequester(),
            FocusRequester(),
            FocusRequester()
        )
    }

    val values = remember {
        mutableStateListOf(
            "",
            "",
            ""
        )
    }

    val fieldValues = remember {
        Utils.localDateToFieldMap(value)
    }

    val dayFocusRequester = remember { FocusRequester() }
    val monthFocusRequester = remember { FocusRequester() }
    val yearFocusRequester = remember { FocusRequester() }

    var dayValue by remember { mutableStateOf("") }
    var monthValue by remember { mutableStateOf("") }
    var yearValue by remember { mutableStateOf("") }

    Row {
        dateFormat.fields.forEachIndexed { index, field ->
            val nextRequester = requesters.getOrNull(index + 1)
            val previousRequester = requesters.getOrNull(index - 1)

            DateInputSection(
                value = values[index],
                onValueChange = {
                    if (it.length == field.length) {
                        nextRequester?.requestFocus()
                    }
                    if (it.length <= field.length) {
                        values[index] = it
                    }
                },
                length = field.length,
                placeholder = stringResource(field.placeholderRes),
                modifier = Modifier
                    .focusRequester(requesters[index])
                    .onPreviewKeyEvent { event ->
                        if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL &&
                            event.type == KeyEventType.KeyDown && values[index].isEmpty()
                        ) {
                            previousRequester?.requestFocus()
                            if (index != 0) {
                                if (values[index - 1].length == dateFormat.fields[index - 1].length) {
                                    values[index - 1] =
                                        values[index - 1].substring(0 until dateFormat.fields[index - 1].length - 1)
                                }
                            }
                        }
                        if (values[index].length >= field.length && event.type == KeyEventType.KeyDown && event.key.nativeKeyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9) {
                            nextRequester?.requestFocus()
                        }
                        false
                    }
            )

            if (index < dateFormat.fields.size - 1) {
                delimiterText()
            }
        }
    }
}

@Composable
fun DateInputSection(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    placeholder: String,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = DateTextFieldDefaults.CharSpacing
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged {
                isFocused = it.isFocused
            },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    horizontalSpacing,
                    Alignment.CenterHorizontally
                )
            ) {
                repeat(length) { index ->
                    val cursorAlpha = remember { Animatable(1f) }
                    LaunchedEffect((isFocused && value.length == index) || (isFocused && value.length == length && index == length - 1)) {
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

                    val digit = value.getOrNull(index)?.toString()
                    Box(
                        modifier = Modifier.drawWithContent {
                            drawContent()
                            if (isFocused && value.length == index) {
                                drawLine(
                                    brush = SolidColor(Color.Magenta),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, size.height),
                                    strokeWidth = 2.dp.toPx(),
                                    alpha = cursorAlpha.value
                                )
                            } else if (isFocused && value.length == length && index == length - 1) {
                                drawLine(
                                    brush = SolidColor(Color.Magenta),
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 2.dp.toPx(),
                                    alpha = cursorAlpha.value
                                )
                            }
                        }
                    ) {
                        if (digit != null) {
                            Text(
                                digit,
                                style = MaterialTheme.typography.h3
                            )
                        }
                        Text(
                            placeholder,
                            style = MaterialTheme.typography.h3.copy(color = Color.Gray),
                            modifier = Modifier.alpha(if (digit == null) 1f else 0f)
                        )
                    }
                }
            }
        }
    )
}
