package com.lanars.compose.datetextfield

import android.content.Context
import android.view.KeyEvent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.lanars.compose.datetextfield.DateValidator.validateDate
import org.threeten.bp.LocalDate

/**
 * Date text field with on the fly validation
 *
 * @param modifier optional modifier
 * @param format date format (enum)
 * @param minDate minimum allowed date
 * @param maxDate maximum allowed date
 * @param onValueChange this callback is triggered when field value has changed. An updated object comes as a parameter of the callback
 * @param onEditingComplete this callback is triggered when date is fully entered. A completed LocalDate object comes as a parameter of the callback
 * @param textStyle optional text style configuration
 */
@ExperimentalComposeUiApi
@Composable
fun DateTextField(
    modifier: Modifier = Modifier,
    format: Format = Format.MMDDYYYY,
    minDate: LocalDate = LocalDate.of(1900, 1, 1),
    maxDate: LocalDate = LocalDate.of(2100, 12, 31),
    onValueChange: (FieldsData) -> Unit = {},
    onEditingComplete: (LocalDate) -> Unit,
    textStyle: TextStyle = TextStyle.Default
) {
    val dateFormat by remember {
        val factory = DateFormat.Factory()
        factory.minDate = minDate.atTime(0, 0)
        factory.maxDate = maxDate.atTime(0, 0)
        mutableStateOf(
            factory.createSpecificFormat(format).orElseGet { factory.createDefaultFormat() })
    }

    val fieldValues = remember {
        mutableMapOf(
            DateField.Day to DateFieldValue(DateField.Day, 2),
            DateField.Month to DateFieldValue(DateField.Month, 2),
            DateField.Year to DateFieldValue(DateField.Year, 4)
        )
    }

    val focusRequestersMap = mapOf(
        DateField.Day to (0 until DateField.Day.length).map { FocusRequester() },
        DateField.Month to (0 until DateField.Month.length).map { FocusRequester() },
        DateField.Year to (0 until DateField.Year.length).map { FocusRequester() }
    )

    val maxWidthMap = remember {
        mutableStateMapOf(
            DateField.Day to 0.0f,
            DateField.Month to 0.0f,
            DateField.Year to 0.0f
        )
    }

    var isEditingComplete by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.offset((-4).dp)
    ) {
        val it: Iterator<*> = dateFormat.fields.iterator()
        while (it.hasNext()) {
            val dateField = it.next() as DateField
            DateInputField(
                dateField = dateField,
                length = dateField.length,
                textStyle = textStyle,
                onDelete = {
                    onValueChange(
                        FieldsData(
                            day =
                            fieldValues[DateField.Day]!!.values.map { if (it == -1) null else it }
                                .toTypedArray(),
                            month =
                            fieldValues[DateField.Month]!!.values.map { if (it == -1) null else it }
                                .toTypedArray(),
                            year =
                            fieldValues[DateField.Year]!!.values.map { if (it == -1) null else it }
                                .toTypedArray()
                        )
                    )
                },
                onChange = { newValue, fieldType, index ->
                    if (newValue.isNotEmpty()) {
                        val isCorrect = validateDate(
                            dateField,
                            fieldValues[DateField.Day]!!,
                            fieldValues[DateField.Month]!!,
                            fieldValues[DateField.Year]!!,
                            dateFormat
                        )
                        if (!isCorrect) {
                            fieldValues[dateField]!!.setValue(index, -1)
                        } else {
                            if (dateFormat.fields.indexOf(fieldType) * 2 + index < 7) {
                                for (i in dateFormat.fields.indexOf(fieldType) until dateFormat.fields.size) {
                                    if (!fieldValues[dateFormat.fields[i]]!!.isComplete) {
                                        val emptyPosition =
                                            fieldValues[dateFormat.fields[i]]!!.values.indexOfFirst { it == -1 }
                                        if (emptyPosition != -1) {
                                            focusRequestersMap[dateFormat.fields.elementAt(i)]!![emptyPosition].requestFocus()
                                            break
                                        }
                                    }
                                }
                            }
                            onValueChange(
                                FieldsData(
                                    day =
                                    fieldValues[DateField.Day]!!.values.map { if (it == -1) null else it }
                                        .toTypedArray(),
                                    month =
                                    fieldValues[DateField.Month]!!.values.map { if (it == -1) null else it }
                                        .toTypedArray(),
                                    year =
                                    fieldValues[DateField.Year]!!.values.map { if (it == -1) null else it }
                                        .toTypedArray()
                                )
                            )
                        }
                    }

                    isEditingComplete = true
                    fieldValues.values.forEach {
                        if (!it.isComplete) {
                            isEditingComplete = false
                        }
                    }
                    if (isEditingComplete) {
                        onEditingComplete(
                            LocalDate.of(
                                fieldValues[DateField.Year]!!.intValue,
                                fieldValues[DateField.Month]!!.intValue,
                                fieldValues[DateField.Day]!!.intValue
                            )
                        )
                    }
                },
                focusRequesters = focusRequestersMap,
                maxWidthMap = maxWidthMap,
                values = fieldValues,
                dateFormat = dateFormat,
            )
            if (it.hasNext()) {
                Text(
                    text = stringResource(id = R.string.slash),
                    style = textStyle.copy(color = Color.Gray)
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
internal fun DateInputField(
    dateField: DateField,
    length: Int,
    onDelete: () -> Unit,
    onChange: (newValue: String, fieldType: DateField, index: Int) -> Unit,
    focusRequesters: Map<DateField, List<FocusRequester>>,
    maxWidthMap: MutableMap<DateField, Float>,
    values: MutableMap<DateField, DateFieldValue>,
    dateFormat: DateFormat,
    textStyle: TextStyle
) {
    val context = LocalContext.current
    CustomRow(
        onMeasured = {
            if (maxWidthMap[dateField] == 0.0f) {
                maxWidthMap[dateField] = pxToDp(context, it.toFloat())
            }
        },
        modifier = Modifier
            .padding(start = 4.dp)
            .pointerInteropFilter {
                when {
                    values[dateField]!!.isComplete -> {
                        focusRequesters[dateField]!![length - 1].requestFocus()
                    }
                    values[dateField]!!.isEmpty -> {
                        focusRequesters[dateField]!![0].requestFocus()
                    }
                    else -> {
                        val emptyPosition =
                            values[dateField]!!.values.indexOfFirst { it == -1 }
                        focusRequesters[dateField]!![emptyPosition].requestFocus()
                    }
                }
                true
            }
    ) {
        for (i in 0 until length) {

            var previousValue by remember(dateFormat.fields.indexOf(dateField) * 2 + i) {
                mutableStateOf(-1)
            }

            SingleInputField(
                modifier = Modifier
                    .focusRequester(focusRequesters[dateField]!![i])
                    .onFocusChanged {
                        previousValue = values[dateField]!!.values[i]
                        if (it.isFocused) {
                            when {
                                values[dateField]!!.isComplete -> {
                                    focusRequesters[dateField]!![length - 1].requestFocus()
                                }
                                values[dateField]!!.isEmpty -> {
                                    focusRequesters[dateField]!![0].requestFocus()
                                }
                                else -> {
                                    val emptyPosition =
                                        values[dateField]!!.values.indexOfFirst { it == -1 }
                                    focusRequesters[dateField]!![emptyPosition].requestFocus()
                                }
                            }
                        }
                    }
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyUp) {
                            if (event.key.nativeKeyCode == KeyEvent.KEYCODE_DEL) {
                                onDelete()
                                if (i == length - 1 && previousValue != values[dateField]!!.values[i]) {
                                    previousValue = values[dateField]!!.values[i]
                                } else if (dateFormat.fields.indexOf(dateField) * 2 + i > 0) {
                                    if (i == 0) {
                                        val indexOfDateField = dateFormat.fields.indexOf(dateField)
                                        if (indexOfDateField != 0) {
                                            values[dateFormat.fields[indexOfDateField - 1]]!!.setValue(
                                                dateFormat.fields[indexOfDateField - 1].length - 1,
                                                -1
                                            )
                                        }
                                    } else {
                                        values[dateField]!!.setValue(i - 1, -1)
                                    }
                                    if (i == 0) {
                                        val previousItem = dateFormat.fields.elementAt(
                                            dateFormat.fields.indexOf(dateField) - 1
                                        )
                                        focusRequesters[previousItem]!![previousItem.length - 1].requestFocus()
                                    } else {
                                        focusRequesters[dateField]!![i - 1].requestFocus()
                                    }
                                }
                            }
                        }
                        false
                    },
                dateField = dateField,
                textStyle = textStyle,
                value = if (values[dateField]!!.values[i] == -1) "" else values[dateField]!!.values[i].toString(),
                onChange = { newValue, fieldType ->

                    previousValue = values[dateField]!!.values[i]
                    if (newValue.isEmpty()) {
                        values[dateField]!!.setValue(i, -1)
                    } else {
                        values[dateField]!!.setValue(i, newValue.toInt())
                    }

                    onChange(newValue, fieldType, i)
                },
                maxWidthMap = maxWidthMap
            )
        }
    }
}

@Composable
internal fun SingleInputField(
    modifier: Modifier,
    dateField: DateField,
    value: String,
    onChange: (newValue: String, fieldType: DateField) -> Unit,
    textStyle: TextStyle,
    maxWidthMap: Map<DateField, Float>
) {
    val inputTextModifier = when (maxWidthMap[dateField]) {
        0.0f -> modifier
            .padding(end = 4.dp)
            .width(IntrinsicSize.Min)
        else -> modifier.width(maxWidthMap[dateField]!!.dp)
    }
    InputEditText(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            autoCorrect = false,
            imeAction = ImeAction.Next
        ),
        value = value,
        onValueChange = {
            if (it.isDigitsOnly()) {
                if (it.length <= 1) {
                    onChange(it, dateField)
                }
            }
        },
        placeHolderString = stringResource(id = dateField.placeholderRes),
        modifier = inputTextModifier,
        singleLine = true,
        contentTextStyle = textStyle,
        hintTextStyle = textStyle.copy(color = Color.Gray),
    )
}

@Composable
internal fun InputEditText(
    value: String,
    modifier: Modifier,
    onValueChange: (String) -> Unit,
    contentTextStyle: TextStyle,
    hintTextStyle: TextStyle,
    placeHolderString: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    cursorColor: Color = Color.Black,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = contentTextStyle,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        if (contentTextStyle.textAlign == TextAlign.Start)
                            IntOffset(x = 10, y = 0)
                        else
                            IntOffset(x = 0, y = 0)
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeHolderString,
                        style = hintTextStyle
                    )
                }

                innerTextField()

            }
        },
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        cursorBrush = SolidColor(cursorColor),
    )
}

@Composable
internal fun CustomRow(
    modifier: Modifier = Modifier,
    onMeasured: (Int) -> Unit,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        var width = 0
        val height = placeables[0].height

        placeables.forEach { placeable ->
            width += placeable.width
        }

        layout(width, height) {

            var xPosition = 0

            var max = 0

            placeables.forEach { placeable ->
                placeable.placeRelative(x = xPosition, y = 0)
                xPosition += placeable.width
                if (placeable.width > max) {
                    max = placeable.width
                }
            }
            onMeasured(max)
        }
    }
}

internal fun pxToDp(context: Context, px: Float): Float {
    return px / context.resources.displayMetrics.density
}