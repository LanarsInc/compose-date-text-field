package com.lanars.compose.datetextfield

internal sealed class DateField {
    abstract val length: Int
    abstract val placeholderRes: Int

    object Day: DateField() {
        override val length = 2
        override val placeholderRes = R.string.D
    }
    object Month: DateField() {
        override val length = 2
        override val placeholderRes = R.string.M
    }
    object Year: DateField() {
        override val length = 4
        override val placeholderRes = R.string.Y
    }
}
