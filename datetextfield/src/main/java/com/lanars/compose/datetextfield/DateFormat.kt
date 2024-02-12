package com.lanars.compose.datetextfield

import android.annotation.SuppressLint
import android.text.TextUtils
import com.lanars.compose.datetextfield.utils.Range
import java.time.LocalDateTime
import java.util.*

internal class DateFormat private constructor(builder: Builder) {
    private val dateFieldRangeMap: Map<DateField, Range<Int>> = builder.map
    val minDate: LocalDateTime = builder.minDate
    val maxDate: LocalDateTime = builder.maxDate

    private class Builder {
        val map: MutableMap<DateField, Range<Int>> = LinkedHashMap<DateField, Range<Int>>()
        var minDate: LocalDateTime = LocalDateTime.of(1900, 1, 1, 0, 0)
        var maxDate: LocalDateTime = LocalDateTime.of(2100, 12, 31, 0, 0)

        fun buildMonth(min: Int, max: Int): Builder {
            map[DateField.Month] = Range(min, max)
            return this
        }

        fun buildYear(min: Int, max: Int): Builder {
            map[DateField.Year] = Range(min, max)
            return this
        }

        fun buildDayOfMonth(min: Int, max: Int): Builder {
            map[DateField.Day] = Range(min, max)
            return this
        }

        fun buildMinMax(
            minDate: LocalDateTime,
            maxDate: LocalDateTime
        ) {
            this.minDate = minDate
            this.maxDate = maxDate
        }

        @SuppressLint("DefaultLocale")
        fun build(): Optional<DateFormat> {
            if (map.size == 3) {
                return Optional.of(DateFormat(this))
            }
            return Optional.empty()
        }
    }

    internal class Factory {
        lateinit var minDate: LocalDateTime
        lateinit var maxDate: LocalDateTime
        private fun addDayBounds(builder: Builder): Builder {
            builder.buildDayOfMonth(1, 31)
            return builder
        }

        private fun addMonthBounds(builder: Builder): Builder {
            builder.buildMonth(1, 12)
            return builder
        }

        private fun addYearBounds(
            builder: Builder,
            minDate: LocalDateTime,
            maxDate: LocalDateTime
        ): Builder {
            builder.buildYear(minDate.year, maxDate.year)
            return builder
        }

        fun createDefaultFormat(): DateFormat {
            require(maxDate >= minDate) { "The maximum date cannot be less than the minimum date" }
            val builder = Builder()
            addMonthBounds(builder)
            addDayBounds(builder)
            addYearBounds(builder, minDate, maxDate)
            builder.buildMinMax(minDate, maxDate)
            return builder.build().get()
        }

        fun createSpecificFormat(format: Format): Optional<DateFormat> {
            require(maxDate >= minDate) { "The maximum date cannot be less than the minimum date" }
            val split = format.format.split("/").toTypedArray()
            if (split.size != 3) {
                return Optional.empty()
            }
            val builder =
                Builder()
            for (str in split) {
                if (TextUtils.isEmpty(str)) {
                    return Optional.empty()
                }
                when (str[0]) {
                    'M' -> addMonthBounds(builder)
                    'd' -> addDayBounds(builder)
                    'y' -> addYearBounds(builder, minDate, maxDate)
                }
            }
            builder.buildMinMax(minDate, maxDate)
            return builder.build()
        }
    }

    val fields: List<DateField>
        get() = ArrayList(dateFieldRangeMap.keys)

    fun getRange(dateField: DateField): Range<Int> {
        return dateFieldRangeMap[dateField] ?: Range.create(0, 0)
    }
}
