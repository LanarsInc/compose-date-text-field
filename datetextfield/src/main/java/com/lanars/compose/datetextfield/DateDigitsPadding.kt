package com.lanars.compose.datetextfield

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DateDigitsPadding(
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp
) {
    constructor(padding: Dp = 0.dp) : this(
        start = padding,
        top = padding,
        end = padding,
        bottom = padding
    )

    constructor(
        horizontal: Dp = 0.dp,
        vertical: Dp = 0.dp
    ) : this(start = horizontal, top = vertical, end = horizontal, bottom = vertical)
}