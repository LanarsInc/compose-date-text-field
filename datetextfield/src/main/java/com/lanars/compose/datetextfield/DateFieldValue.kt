package com.lanars.compose.datetextfield

import androidx.compose.runtime.mutableStateListOf


internal class DateFieldValue private constructor(
    val type: DateField,
    private val initialValues: List<Int>? = null
) {
    constructor(type: DateField) : this(type, null)

    val values = mutableStateListOf(
        *(initialValues ?: (0 until type.length).map { -1 }).toTypedArray()
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

    fun setValue(value: Int) {
        val position = values.indexOfFirst { it < 0 } ?: return
        if (position < 0) return
        setValue(position, value)
    }

    fun clearLast() {
        val position = values.indexOfLast { it >= 0 }
        if (position < 0) return
        setValue(position, -1)
    }

    fun copy(): DateFieldValue {
        return DateFieldValue(
            type,
            values
        )
    }
}