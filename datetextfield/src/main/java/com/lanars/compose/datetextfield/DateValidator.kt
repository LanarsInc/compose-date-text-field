package com.lanars.compose.datetextfield

import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Period
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

internal object DateValidator {
    fun validateDate(
        dateField: DateField,
        day: DateFieldValue,
        month: DateFieldValue,
        year: DateFieldValue,
        dateFormat: DateFormat
    ): Boolean {
        if (day.isComplete && month.isComplete && year.isComplete) {
            return try {
                isDateInAllowedRange(
                    LocalDateTime.of(
                        year.intValue,
                        month.intValue,
                        day.intValue,
                        0,
                        0
                    ), dateFormat
                )
            } catch (e: DateTimeException) {
                false
            }
        }
        return when (dateField) {
            DateField.Year -> validateYear(month, day, year, dateFormat)
            DateField.Month -> validateMonth(month, day, year, dateFormat)
            DateField.Day -> validateDay(month, day, year, dateFormat)
        }
    }

    private fun validateMonth(
        month: DateFieldValue,
        day: DateFieldValue,
        year: DateFieldValue,
        dateFormat: DateFormat
    ): Boolean {
        val monthIntValue = month.intValue

        if (!basicValidateMonth(month, day, year, dateFormat)) {
            return false
        }
        if (!isMonthInRange(month, day, dateFormat)) {
            return false
        }

        if (!year.isEmpty) {
            var yearValue: Int = year.intValue
            if (!year.isComplete) {
                if (isMaximumPossibleYear(year.intValue, dateFormat)) {
                    yearValue = completeYearValue(year.intValue, 0)
                } else if (isMinimumPossibleYear(year.intValue, dateFormat)) {
                    yearValue = completeYearValue(year.intValue, 9)
                } else {
                    return true
                }
            }

            if (month.isComplete) {
                if (month.intValue == 2 && day.values[0] > 2) {
                    return false
                }

                if (yearValue == dateFormat.minDate.year && monthIntValue < dateFormat.minDate.monthValue
                    || yearValue == dateFormat.maxDate.year && monthIntValue > dateFormat.maxDate.monthValue
                ) {
                    return false
                }
                if (day.isComplete) {
                    if (yearValue == dateFormat.minDate.year && monthIntValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth
                        || yearValue == dateFormat.maxDate.year && monthIntValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth
                    ) {
                        return false
                    }
                }
                if (day.count == 1) {
                    if (yearValue == dateFormat.minDate.year && monthIntValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth / 10
                        || yearValue == dateFormat.maxDate.year && monthIntValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth / 10
                    ) {
                        return false
                    }
                }
                return true
            } else {
                if (yearValue == dateFormat.minDate.year && monthIntValue < dateFormat.minDate.monthValue / 10
                    || yearValue == dateFormat.maxDate.year && monthIntValue > dateFormat.maxDate.monthValue / 10
                ) {
                    return false
                }
                if (day.isComplete) {
                    if ((yearValue == dateFormat.minDate.year && monthIntValue == dateFormat.minDate.monthValue / 10
                                && (dateFormat.minDate.monthValue == 9 || dateFormat.minDate.monthValue == 12) && day.intValue < dateFormat.minDate.dayOfMonth)
                        || (yearValue == dateFormat.maxDate.year && monthIntValue == dateFormat.maxDate.monthValue / 10
                                && (dateFormat.maxDate.monthValue == 1 || dateFormat.maxDate.monthValue == 10) && day.intValue > dateFormat.maxDate.dayOfMonth)
                    ) {
                        return false
                    }
                }
                if (day.count == 1) {
                    if ((yearValue == dateFormat.minDate.year && monthIntValue == dateFormat.minDate.monthValue / 10
                                && (dateFormat.minDate.monthValue == 9 || dateFormat.minDate.monthValue == 12) && day.intValue < dateFormat.minDate.dayOfMonth / 10)
                        || (yearValue == dateFormat.maxDate.year && monthIntValue == dateFormat.maxDate.monthValue / 10
                                && (dateFormat.maxDate.monthValue == 1 || dateFormat.maxDate.monthValue == 10) && day.intValue > dateFormat.maxDate.dayOfMonth / 10)
                    ) {
                        return false
                    }
                }
                return true
            }
        }
        return true
    }

    private fun isMonthInRange(
        month: DateFieldValue,
        day: DateFieldValue,
        dateFormat: DateFormat
    ): Boolean {
        val period =
            Period.between(dateFormat.minDate.toLocalDate(), dateFormat.maxDate.toLocalDate())
        if (period.years >= 1) {
            return true
        }
        if (month.isComplete) {
            if (day.isComplete) {
                if (month.intValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth
                    || month.intValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth
                ) {
                    return false
                }
            } else if (day.count == 1) {
                if (month.intValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth / 10
                    || month.intValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth / 10
                ) {
                    return false
                }
            }
            if (dateFormat.minDate.year == dateFormat.maxDate.year) {
                return month.intValue in dateFormat.minDate.monthValue..dateFormat.maxDate.monthValue
            }
            return month.intValue !in dateFormat.maxDate.monthValue + 1 until dateFormat.minDate.monthValue
        } else {
            if (day.isComplete) {
                if ((month.intValue == dateFormat.minDate.monthValue / 10 && (dateFormat.minDate.monthValue == 9 || dateFormat.minDate.monthValue == 12)
                            && day.intValue < dateFormat.minDate.dayOfMonth) || (month.intValue == dateFormat.maxDate.monthValue / 10
                            && (dateFormat.maxDate.monthValue == 10 || dateFormat.maxDate.monthValue == 1) && day.intValue > dateFormat.maxDate.dayOfMonth)
                ) {
                    return false
                }
            } else if (day.count == 1) {
                if ((month.intValue == dateFormat.minDate.monthValue / 10 && (dateFormat.minDate.monthValue == 9 || dateFormat.minDate.monthValue == 12) && day.intValue < dateFormat.minDate.dayOfMonth / 10)
                    || (month.intValue == dateFormat.minDate.monthValue / 10 && (dateFormat.maxDate.monthValue == 1 || dateFormat.maxDate.monthValue == 10) && day.intValue > dateFormat.maxDate.dayOfMonth / 10)
                ) {
                    return false
                }
            }
            if (dateFormat.minDate.year == dateFormat.maxDate.year) {
                return month.intValue in (dateFormat.minDate.monthValue / 10)..(dateFormat.maxDate.monthValue / 10)
            }
        }
        return true
    }

    private fun basicValidateMonth(
        month: DateFieldValue,
        day: DateFieldValue,
        year: DateFieldValue,
        dateFormat: DateFormat
    ): Boolean {
        val monthIntValue = month.intValue
        return if (month.isComplete) {
            if (!dateFormat.getRange(DateField.Month).contains(monthIntValue)) {
                return false
            }
            if (!year.isComplete || !day.isComplete) {
                !day.isComplete || day.intValue <= getMaxMonthValue(
                    monthIntValue
                )
            } else isDateExists(
                day.intValue,
                monthIntValue,
                year.intValue
            )
        } else monthIntValue <= 1
    }

    private fun validateYear(
        month: DateFieldValue,
        day: DateFieldValue,
        year: DateFieldValue,
        dateFormat: DateFormat
    ): Boolean {
        if (
            isYearExists(
                year.intValue,
                year.values.toIntArray(),
                dateFormat
            )
        ) {
            if (month.isComplete && day.isComplete) {
                var min: Int = dateFormat.minDate.year
                var max: Int = dateFormat.maxDate.year
                if (month.intValue > dateFormat.minDate.monthValue && month.intValue < dateFormat.maxDate.monthValue) {
                    return true
                }
                if (month.intValue < dateFormat.minDate.monthValue) {
                    min = dateFormat.minDate.year + 1
                }
                if (month.intValue > dateFormat.maxDate.monthValue) {
                    max = dateFormat.maxDate.year - 1
                }
                if (month.intValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth) {
                    min = dateFormat.minDate.year + 1
                }
                if (month.intValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth) {
                    max = dateFormat.maxDate.year - 1
                }
                return isYearExists(
                    year.intValue,
                    year.values.toIntArray(),
                    dateFormat,
                    min = min,
                    max = max
                )
            } else if (month.isComplete) {
                var min: Int = dateFormat.minDate.year
                var max: Int = dateFormat.maxDate.year
                if (month.intValue < dateFormat.minDate.monthValue) {
                    min = dateFormat.minDate.year + 1
                }
                if (month.intValue > dateFormat.maxDate.monthValue) {
                    max = dateFormat.maxDate.year - 1
                }
                if (!day.isEmpty) {
                    if (month.intValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth) {
                        min = dateFormat.minDate.year + 1
                    }
                    if (month.intValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth) {
                        max = dateFormat.maxDate.year - 1
                    }
                }
                return isYearExists(
                    year.intValue,
                    year.values.toIntArray(),
                    dateFormat,
                    min = min,
                    max = max
                )
            } else if (month.count in 1 until DateField.Month.length) {
                var min: Int = dateFormat.minDate.year
                var max: Int = dateFormat.maxDate.year
                if (month.intValue < dateFormat.minDate.monthValue / 10) {
                    min = dateFormat.minDate.year + 1
                }
                if (month.intValue > dateFormat.maxDate.monthValue / 10) {
                    max = dateFormat.maxDate.year - 1
                }
                return isYearExists(
                    year.intValue,
                    year.values.toIntArray(),
                    dateFormat,
                    min = min,
                    max = max
                )
            } else {
                return isYearExists(
                    year.intValue,
                    year.values.toIntArray(),
                    dateFormat
                )
            }
        }
        return false
    }

    private fun validateDay(
        month: DateFieldValue,
        day: DateFieldValue,
        year: DateFieldValue,
        dateFormat: DateFormat
    ): Boolean {
        if (!basicValidateDay(month, day, year)) {
            return false
        }
        if (!isDayInRange(month, day, dateFormat)) {
            return false
        }

        if (!year.isEmpty) {
            var yearValue: Int = year.intValue
            if (!year.isComplete) {
                if (isMaximumPossibleYear(year.intValue, dateFormat)) {
                    yearValue = completeYearValue(year.intValue, 0)
                } else if (isMinimumPossibleYear(year.intValue, dateFormat)) {
                    yearValue = completeYearValue(year.intValue, 9)
                } else {
                    return true
                }
            }

            if (day.isComplete && !month.isEmpty) {
                if (month.isComplete) {
                    return isDateInAllowedRange(
                        LocalDateTime.of(
                            yearValue,
                            month.intValue,
                            day.intValue,
                            0,
                            0
                        ), dateFormat
                    )
                }
                if (yearValue == dateFormat.minDate.year) {
                    if (month.intValue == dateFormat.minDate.monthValue / 10 && (dateFormat.minDate.monthValue == 9 || dateFormat.minDate.monthValue == 12)
                        && day.intValue < dateFormat.minDate.dayOfMonth
                    ) {
                        return false
                    }
                }
                if (yearValue == dateFormat.maxDate.year) {
                    if (month.intValue == dateFormat.maxDate.monthValue / 10 && (dateFormat.maxDate.monthValue == 1 || dateFormat.maxDate.monthValue == 10)
                        && day.intValue > dateFormat.maxDate.dayOfMonth
                    ) {
                        return false
                    }
                }
            }

            if (!day.isComplete && month.isComplete) {
                if (yearValue == dateFormat.minDate.year) {
                    if (month.intValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth / 10) {
                        return false
                    }
                }
                if (yearValue == dateFormat.maxDate.year) {
                    if (month.intValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth / 10) {
                        return false
                    }
                }
            }
            if (!day.isComplete && month.count in 1 until DateField.Month.length) {
                if (yearValue == dateFormat.minDate.year) {
                    if (month.intValue == dateFormat.minDate.monthValue / 10 && day.intValue < dateFormat.minDate.dayOfMonth / 10
                        && (dateFormat.minDate.monthValue == 9 || dateFormat.minDate.monthValue == 12)
                    ) {
                        return false
                    }
                }
                if (yearValue == dateFormat.maxDate.year) {
                    if (month.intValue == dateFormat.maxDate.monthValue / 10 && day.intValue > dateFormat.maxDate.dayOfMonth / 10
                        && (dateFormat.maxDate.monthValue == 1 || dateFormat.maxDate.monthValue == 10)
                    ) {
                        return false
                    }
                }
            }
            if (month.isEmpty) {
                if (day.isComplete) {
                    if (yearValue == dateFormat.minDate.year && dateFormat.minDate.monthValue == 12) {
                        return day.intValue >= dateFormat.minDate.dayOfMonth
                    }
                    if (yearValue == dateFormat.maxDate.year && dateFormat.maxDate.monthValue == 1) {
                        return day.intValue <= dateFormat.maxDate.dayOfMonth
                    }
                } else if (day.count == 1) {
                    if (yearValue == dateFormat.minDate.year && dateFormat.minDate.monthValue == 12) {
                        return day.intValue >= dateFormat.minDate.dayOfMonth / 10
                    }
                    if (yearValue == dateFormat.maxDate.year && dateFormat.maxDate.monthValue == 1) {
                        return day.intValue <= dateFormat.maxDate.dayOfMonth / 10
                    }
                }
            }
        }
        return true
    }

    private fun isDayInRange(
        month: DateFieldValue,
        day: DateFieldValue,
        dateFormat: DateFormat
    ): Boolean {
        if (dateFormat.maxDate.year - dateFormat.minDate.year > 1) {
            return true
        }
        if (day.isComplete) {
            if (day.intValue > getMaxMonthValue(dateFormat.minDate.monthValue) && day.intValue > getMaxMonthValue(
                    dateFormat.maxDate.monthValue
                )
            ) {
                return false
            }
            if (month.isComplete) {
                if (month.intValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth
                    || month.intValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth
                ) {
                    return false
                }
            } else if (month.count == 1) {
                if ((month.intValue == dateFormat.minDate.monthValue / 10 && (dateFormat.minDate.monthValue == 9 || dateFormat.minDate.monthValue == 12)
                            && day.intValue < dateFormat.minDate.dayOfMonth) || (month.intValue == dateFormat.maxDate.monthValue / 10
                            && (dateFormat.maxDate.monthValue == 1 || dateFormat.maxDate.monthValue == 1) && day.intValue > dateFormat.maxDate.dayOfMonth)
                ) {
                    return false
                }
            }
            if (dateFormat.minDate.monthValue == dateFormat.maxDate.monthValue) {
                return day.intValue in dateFormat.minDate.dayOfMonth..dateFormat.maxDate.dayOfMonth
            }
            return day.intValue !in dateFormat.maxDate.dayOfMonth + 1 until dateFormat.minDate.dayOfMonth
        } else {
            if (day.intValue > getMaxMonthValue(dateFormat.minDate.monthValue) / 10 && day.intValue > getMaxMonthValue(
                    dateFormat.maxDate.monthValue / 10
                )
            ) {
                return false
            }
            if (month.isComplete) {
                if (month.intValue == dateFormat.minDate.monthValue && day.intValue < dateFormat.minDate.dayOfMonth / 10
                    || month.intValue == dateFormat.maxDate.monthValue && day.intValue > dateFormat.maxDate.dayOfMonth / 10
                ) {
                    return false
                }
            } else if (month.count == 1) {
                if ((month.intValue == dateFormat.minDate.monthValue / 10 && (dateFormat.minDate.monthValue == 9 || dateFormat.minDate.monthValue == 12)
                            && day.intValue < dateFormat.minDate.dayOfMonth / 10) || (month.intValue == dateFormat.maxDate.monthValue / 10
                            && (dateFormat.maxDate.monthValue == 1 || dateFormat.maxDate.monthValue == 1) && day.intValue > dateFormat.maxDate.dayOfMonth / 10)
                ) {
                    return false
                }
            }
            if (dateFormat.minDate.monthValue == dateFormat.maxDate.monthValue) {
                return day.intValue in (dateFormat.minDate.dayOfMonth / 10)..(dateFormat.maxDate.dayOfMonth / 10)
            }
            if (dateFormat.minDate.monthValue != 12 && dateFormat.maxDate.monthValue != 1) {
                return true
            }
            return day.intValue !in (dateFormat.maxDate.dayOfMonth / 10) + 1 until (dateFormat.minDate.dayOfMonth / 10)
        }
    }

    private fun basicValidateDay(
        month: DateFieldValue,
        day: DateFieldValue,
        year: DateFieldValue
    ): Boolean {
        if (!day.isComplete && day.values[0] > 3) {
            return false
        }
        if (month.isComplete && month.intValue == 2 && day.values[0] > 2) {
            return false
        }
        if (day.intValue == 0 && day.isComplete) {
            return false
        }
        if (day.intValue in 0..28) {
            return true
        }
        if (day.intValue == 29 && (isLeapYear(year.intValue) || !year.isComplete)) {
            return true
        }
        if ((day.intValue == 29 || day.intValue == 30) && (month.intValue != 2 || !month.isComplete)) {
            return true
        }
        val monthsWith31Day = listOf(1, 3, 5, 7, 8, 10, 12)
        return day.intValue == 31 && (monthsWith31Day.contains(month.intValue) || !month.isComplete)
    }

    private fun completeYearValue(value: Int, digit: Int): Int {
        var valueString = value.toString()
        var digitsToAdd = DateField.Year.length - valueString.length
        while (digitsToAdd > 0) {
            valueString += "$digit"
            digitsToAdd--
        }
        return valueString.toInt()
    }

    private fun isMaximumPossibleYear(yearValue: Int, dateFormat: DateFormat): Boolean {
        return completeYearValue(yearValue, 0) == dateFormat.maxDate.year
    }

    private fun isMinimumPossibleYear(yearValue: Int, dateFormat: DateFormat): Boolean {
        return completeYearValue(yearValue, 9) == dateFormat.minDate.year
    }

    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }

    private fun isYearExists(
        year: Int,
        yearValues: IntArray,
        dateFormat: DateFormat,
        min: Int = dateFormat.minDate.year,
        max: Int = dateFormat.maxDate.year
    ): Boolean {
        val a =
            getEmptyFieldIndex(
                yearValues
            )
        if (a <= 0) {
            return false
        }
        val pow = 10.0.pow(yearValues.size - a.toDouble()).toInt()
        val range: Range<*> = Range.create(min, max)
        for (intValue in range.upper as Int downTo range.lower as Int) {
            if (intValue / pow == year) {
                return true
            }
        }
        return false
    }

    private fun getEmptyFieldIndex(iArr: IntArray): Int {
        for (i in iArr.indices) {
            if (iArr[i] == -1) {
                return i
            }
        }
        return iArr.size
    }

    private fun getMaxMonthValue(monthOrderNumber: Int): Int {
        val isFebruary = monthOrderNumber == 2
        return if (isFebruary) {
            29
        } else LocalDate.now().withMonth(monthOrderNumber).lengthOfMonth()
    }

    private fun isDateExists(day: Int, month: Int, year: Int): Boolean {
        val format =
            String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
        val simpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
        simpleDateFormat.isLenient = false
        return try {
            simpleDateFormat.parse(format)
            true
        } catch (unused: ParseException) {
            false
        }
    }

    private fun isDateInAllowedRange(
        date: LocalDateTime,
        dateFormat: DateFormat
    ): Boolean {
        return date in dateFormat.minDate..dateFormat.maxDate
    }
}