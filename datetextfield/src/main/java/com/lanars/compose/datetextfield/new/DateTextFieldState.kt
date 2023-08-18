package com.lanars.compose.datetextfield.new

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import com.lanars.compose.datetextfield.DateField
import com.lanars.compose.datetextfield.DateFieldValue
import com.lanars.compose.datetextfield.utils.empty

internal class DateTextFieldState {
    private var dayFieldState by mutableStateOf(DateFieldValue(DateField.Day))

    private var monthFieldState by mutableStateOf(DateFieldValue(DateField.Month))

    private var yearFieldState by mutableStateOf(DateFieldValue(DateField.Year))

    val dayValue by derivedStateOf {
        dayFieldState.values.filter { it >= 0 }.joinToString()
    }

    val monthValue by derivedStateOf {
        monthFieldState.values.filter { it >= 0 }.joinToString()
    }

    val yearValue by derivedStateOf {
        yearFieldState.values.filter { it >= 0 }.joinToString()
    }

    var focusedField by mutableStateOf<DateField?>(null)

    val hasFocus by derivedStateOf { focusedField != null }

    private val focusedFieldState: DateFieldValue?
        get() = when (focusedField) {
            is DateField.Day -> dayFieldState
            is DateField.Month -> monthFieldState
            is DateField.Year -> yearFieldState
            else -> null
        }

    fun onKeyEvent(
        event: KeyEvent,
        dayFocusRequester: FocusRequester,
        monthFocusRequester: FocusRequester,
        yearFocusRequester: FocusRequester
    ) {
        if (event.type != KeyEventType.KeyDown || focusedFieldState == null) return
        when {
            event.isDigit() -> {
                val char = event.utf16CodePoint.toChar()
                val intValue = char.digitToInt()
                when (focusedField) {
                    is DateField.Day -> {
                        if (dayFieldState.isComplete) {
                            if (!monthFieldState.isComplete) {
                                monthFocusRequester.requestFocus()
                            } else if (!yearFieldState.isComplete) {
                                yearFocusRequester.requestFocus()
                            }
                        } else {
                            dayFieldState = dayFieldState.apply {
                                setValue(intValue)
                            }
                            if (dayFieldState.isComplete) {
                                if (monthFieldState.isComplete) {
                                    yearFocusRequester.requestFocus()
                                } else {
                                    monthFocusRequester.requestFocus()
                                }
                            }
                        }
                    }

                    is DateField.Month -> {
                        if (monthFieldState.isComplete) {
                            if (!yearFieldState.isComplete) {
                                yearFocusRequester.requestFocus()
                            }
                        } else {
                            monthFieldState = monthFieldState.apply {
                                setValue(intValue)
                            }
                            if (monthFieldState.isComplete) {
                                if (!yearFieldState.isComplete) {
                                    yearFocusRequester.requestFocus()
                                }
                            }
                        }
                    }

                    is DateField.Year -> {
                        if (!yearFieldState.isComplete) {
                            yearFieldState = yearFieldState.apply {
                                setValue(intValue)
                            }
                        }
                    }

                    else -> {
                        /* do nothing */
                    }
                }
            }

            event.isBackspace() -> {
                when (focusedField) {
                    is DateField.Day -> {
                        if (!dayFieldState.isEmpty) {
                            dayFieldState = dayFieldState.apply {
                                clearLast()
                            }
                        }
                    }

                    DateField.Month -> {
                        if (!monthFieldState.isEmpty) {
                            monthFieldState = monthFieldState.apply {
                                clearLast()
                            }
                        } else {
                            if (dayFieldState.isComplete) {
                                dayFieldState = dayFieldState.apply {
                                    clearLast()
                                }
                            }
                            dayFocusRequester.requestFocus()
                        }
                    }

                    DateField.Year -> {
                        if (!yearFieldState.isEmpty) {
                            yearFieldState = yearFieldState.apply {
                                clearLast()
                            }
                        } else {
                            if (monthFieldState.isComplete) {
                                monthFieldState = monthFieldState.apply {
                                    clearLast()
                                }
                            }
                            monthFocusRequester.requestFocus()
                        }
                    }

                    else -> {
                        /* do nothing */
                    }
                }
            }
        }
    }

    fun valueForField(field: DateField): String {
        val fieldState = when (field) {
            is DateField.Day -> dayFieldState
            is DateField.Month -> monthFieldState
            is DateField.Year -> yearFieldState
        }
        return fieldState.values.filter { it >= 0 }.joinToString(String.empty)
    }
}

internal fun KeyEvent.isDigit(): Boolean {
    return key.nativeKeyCode in android.view.KeyEvent.KEYCODE_0..android.view.KeyEvent.KEYCODE_9
}

@OptIn(ExperimentalComposeUiApi::class)
internal fun KeyEvent.isBackspace(): Boolean {
    return key == Key.Backspace
}
