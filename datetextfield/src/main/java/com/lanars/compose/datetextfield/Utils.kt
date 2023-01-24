package com.lanars.compose.datetextfield

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import java.time.LocalDate

internal object Utils {
    fun localDateToFieldMap(date: LocalDate?): SnapshotStateMap<DateField, DateFieldValue> {
        val map = mutableStateMapOf(
            DateField.Day to DateFieldValue(DateField.Day, 2),
            DateField.Month to DateFieldValue(DateField.Month, 2),
            DateField.Year to DateFieldValue(DateField.Year, 4)
        )
        if (date != null) {
            for (field in map.values) {
                val datePart = when (field.type) {
                    DateField.Day -> date.dayOfMonth
                    DateField.Month -> date.monthValue
                    DateField.Year -> date.year
                }
                val datePartToString = when {
                    getDigitsCount(datePart) < field.type.length ->
                        "0".repeat(field.type.length - getDigitsCount(datePart)) + datePart.toString()
                    else -> datePart.toString()
                }
                for (i in 0 until field.values.size) {
                    val digit = datePartToString[i].digitToInt()
                    field.setValue(i, digit)
                }
            }
        }
        return map
    }

    fun getDigitsCount(number: Int): Int {
        return when (number) {
            in -9..9 -> 1
            else -> 1 + getDigitsCount(number / 10)
        }
    }
}
