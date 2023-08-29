package com.lanars.compose.datetextfield

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Immutable
object DateTextFieldDefaults {
    val MainTextStyle = TextStyle.Default

    val HintTextStyle = MainTextStyle.copy(color = MainTextStyle.color.copy(alpha = 0.5f))

    val DelimiterSpacing = 4.dp
}
