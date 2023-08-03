package com.lanars.compose.datetextfield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DateTextField2(
    delimiter: Char = '/'
) {
    val delimiterText = @Composable {
        Text(
            delimiter.toString(),
            style = MaterialTheme.typography.h3.copy(Color.Gray),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }

    val dayFocusRequester = remember { FocusRequester() }
    val monthFocusRequester = remember { FocusRequester() }
    val yearFocusRequester = remember { FocusRequester() }

    var dayValue by remember { mutableStateOf("") }
    var monthValue by remember { mutableStateOf("") }
    var yearValue by remember { mutableStateOf("") }

    Row {
        DateInputSection(
            value = dayValue,
            onValueChange = {
                dayValue = it
                if (it.length == 2) {
                    monthFocusRequester.requestFocus()
                }
            },
            length = 2,
            placeholder = 'D',
            modifier = Modifier.focusRequester(dayFocusRequester),
        )

        delimiterText()

        DateInputSection(
            value = monthValue,
            onValueChange = {
                monthValue = it
                if (it.length == 2) {
                    yearFocusRequester.requestFocus()
                }
            },
            length = 2,
            placeholder = 'M',
            modifier = Modifier.focusRequester(monthFocusRequester),
        )

        delimiterText()

        DateInputSection(
            value = yearValue,
            onValueChange = {
                yearValue = it
            },
            length = 4,
            placeholder = 'Y',
            modifier = Modifier.focusRequester(yearFocusRequester),
        )
    }
}

@Composable
internal fun DateInputSection(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    placeholder: Char,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = DateTextFieldDefaults.CharSpacing,
    textStyle: TextStyle = LocalTextStyle.current
) {
    BasicTextField(
        value = value,
        onValueChange = {
            if (it.length <= length) {
                onValueChange(it)
            }
        },
        modifier = modifier,
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
                    val char = value.getOrNull(index)?.toString()
                    Box {
                        char?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.h3
                            )
                        }
                        Text(
                            placeholder.toString(),
                            style = MaterialTheme.typography.h3.copy(color = Color.Gray),
                            modifier = Modifier.alpha(if (char == null) 1f else 0f)
                        )
                    }
                }
            }
        }
    )
}

/*
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
}*/

/*modifier = Modifier.drawWithContent {
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
}*/

/*
fun Modifier.startInputSession() = composed {
    val textInputService = LocalTextInputService.current

    textInputService?.startInput(
        value = TextFieldValue(""),
        imeOptions = ImeOptions(keyboardType = KeyboardType.NumberPassword),
        onEditCommand = {},
        onImeActionPerformed = {}
    )

    this
}*/
