package com.lanars.composedateinput

internal class Range<T : Comparable<T>> internal constructor(val lower: T, val upper: T) {
    operator fun contains(t: T): Boolean = t in lower..upper

    override fun equals(other: Any?): Boolean {
        var result = false
        if (other == null) {
            return false
        }
        if (this === other) {
            return true
        }
        if (other is Range<*>) {
            if (lower == other.lower && upper == other.upper) {
                result = true
            }
        }
        return result
    }

    override fun hashCode(): Int {
        return hashCode(lower, upper)
    }

    override fun toString(): String {
        return String.format("[%s, %s]", lower, upper)
    }

    companion object {
        private fun <T> getRangeValue(value: T, str: String): T {
            if (value != null) {
                return value
            }
            throw NullPointerException(str)
        }

        @JvmStatic
        fun <T : Comparable<T>> create(lower: T, upper: T): Range<T> =
            Range(lower, upper)

        fun <T> hashCode(lower: T, upper: T): Int {
            val hashCodeLower = lower.hashCode()
            val hashCodeUpper = upper.hashCode()
            return (hashCodeLower shl 5) - hashCodeLower xor hashCodeUpper
        }
    }

    init {
        require(lower <= upper) { "lower must be less than or equal to upper" }
    }
}