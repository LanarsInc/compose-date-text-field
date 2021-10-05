package com.lanars.compose.datetextfield

import androidx.compose.runtime.mutableStateListOf


class DateFieldValue internal constructor(val type: DateField, i: Int) {
    val values = mutableStateListOf(
        *((0 until i).map { -1 }.toTypedArray())
    )

    val intValue: Int
        get() {
            var fieldValue = 0
            for (value in values) {
                if (value == -1) {
                    return fieldValue
                }
                fieldValue = fieldValue * 10 + value
            }
            return fieldValue
        }

    val isComplete: Boolean
        get() {
            for (i in values) {
                if (i == -1) {
                    return false
                }
            }
            return true
        }

    val isEmpty: Boolean
        get() {
            for (i in values) {
                if (i != -1) {
                    return false
                }
            }
            return true
        }

    val count: Int
        get() {
            var completedFieldsCount = 0
            for (i in values) {
                if (i == -1) {
                    break
                }
                completedFieldsCount++
            }
            return completedFieldsCount
        }

    fun setValue(position: Int, value: Int) {
        if (position >= 0) {
            val iArr = values
            if (position < iArr.size) {
                iArr[position] = value
            }
        }
    }
}