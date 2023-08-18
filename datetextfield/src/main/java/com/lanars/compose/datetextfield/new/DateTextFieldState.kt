package com.lanars.compose.datetextfield.new

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.lanars.compose.datetextfield.DateFormat
import com.lanars.compose.datetextfield.utils.empty

internal class DateTextFieldState(
    private val dateFormat: DateFormat
) {
    val fieldsState = mutableStateMapOf(
        *dateFormat.fields.map {
            it to DateFieldState(DateFieldValue(it), FocusRequester(), false)
        }.toTypedArray()
    )

    var focusedField by mutableStateOf<DateField?>(null)

    val hasFocus by derivedStateOf { focusedField != null }

    private val focusedFieldState: DateFieldValue?
        get() = fieldsState[focusedField]?.value

    fun onKeyEvent(event: KeyEvent) {
        if (event.type != KeyEventType.KeyDown || focusedFieldState == null) return

        val first = fieldsState[dateFormat.fields[0]]!!
        val second = fieldsState[dateFormat.fields[1]]!!
        val third = fieldsState[dateFormat.fields[2]]!!

        when {
            event.isDigit() -> {
                val char = event.utf16CodePoint.toChar()
                val intValue = char.digitToInt()
                when (focusedField) {
                    first.value.type -> {
                        if (first.isComplete) {
                            if (!second.isComplete) {
                                second.focusRequester.requestFocus()
                            } else if (!third.isComplete) {
                                third.focusRequester.requestFocus()
                            }
                        } else {
                            fieldsState[first.value.type] = first.apply {
                                value.setValue(intValue)
                            }
                            if (first.isComplete) {
                                if (!second.isComplete) {
                                    second.focusRequester.requestFocus()
                                } else if (!third.isComplete) {
                                    third.focusRequester.requestFocus()
                                }
                            }
                        }
                    }

                    second.value.type -> {
                        if (second.isComplete) {
                            if (!third.isComplete) {
                                third.focusRequester.requestFocus()
                            }
                        } else {
                            fieldsState[second.value.type] = second.apply {
                                value.setValue(intValue)
                            }
                            if (second.isComplete) {
                                if (!third.isComplete) {
                                    third.focusRequester.requestFocus()
                                }
                            }
                        }
                    }

                    third.value.type -> {
                        if (!third.isComplete) {
                            fieldsState[third.value.type] = third.apply {
                                value.setValue(intValue)
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
                    first.value.type -> {
                        if (!first.isEmpty) {
                            fieldsState[first.value.type] = first.apply {
                                value.clearLast()
                            }
                        }
                    }

                    second.value.type -> {
                        if (!second.isEmpty) {
                            fieldsState[second.value.type] = second.apply {
                                value.clearLast()
                            }
                        } else {
                            if (first.isComplete) {
                                fieldsState[first.value.type] = first.apply {
                                    value.clearLast()
                                }
                            }
                            first.focusRequester.requestFocus()
                        }
                    }

                    third.value.type -> {
                        if (!third.isEmpty) {
                            fieldsState[third.value.type] = third.apply {
                                value.clearLast()
                            }
                        } else {
                            if (second.isComplete) {
                                fieldsState[second.value.type] = second.apply {
                                    value.clearLast()
                                }
                            }
                            second.focusRequester.requestFocus()
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
        return fieldsState[field]!!.value.values.filter { it >= 0 }.joinToString(String.empty)
    }
}

internal data class DateFieldState(
    val value: DateFieldValue,
    val focusRequester: FocusRequester,
    val isFocused: Boolean = false
) {
    val isComplete: Boolean
        get() = value.isComplete

    val isEmpty: Boolean
        get() = value.isEmpty
}

internal fun KeyEvent.isDigit(): Boolean {
    return key.nativeKeyCode in android.view.KeyEvent.KEYCODE_0..android.view.KeyEvent.KEYCODE_9
}

@OptIn(ExperimentalComposeUiApi::class)
internal fun KeyEvent.isBackspace(): Boolean {
    return key == Key.Backspace
}
