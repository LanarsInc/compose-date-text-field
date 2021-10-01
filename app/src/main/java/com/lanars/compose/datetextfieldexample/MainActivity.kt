package com.lanars.compose.datetextfieldexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.lanars.compose.datetextfield.DateTextField
import com.lanars.compose.datetextfield.Format
import com.lanars.compose.datetextfieldexample.ui.theme.DateTextFieldExampleTheme
import org.threeten.bp.LocalDate

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DateTextFieldExampleTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        DateTextField(
                            modifier = Modifier.onFocusChanged { Log.d("DateInput", it.toString()) },
                            format = Format.DDMMYYYY,
                            minDate = LocalDate.of(2009,8,27),
                            maxDate = LocalDate.of(2020,9,17),
                            onValueChange = {
                                Log.d(
                                    "DateInput",
                                    "Day: ${it.day.joinToString()}; Month: ${it.month.joinToString()}; Year: ${it.year.joinToString()}"
                                )
                            },
                            onEditingComplete = { Log.d("DateInput", it.toString()) },
                            contentTextStyle = TextStyle(fontSize = 25.sp, color = Color.Black),
                            hintTextStyle = TextStyle(fontSize = 25.sp, color = Color.Gray),
                            delimiter = '.'
                        )
                    }
                }
            }
        }
    }
}
