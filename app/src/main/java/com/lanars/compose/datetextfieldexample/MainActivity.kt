package com.lanars.compose.datetextfieldexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.lanars.compose.datetextfield.DateDigitsPadding
import com.lanars.compose.datetextfield.DateTextField
import com.lanars.compose.datetextfield.DateTextField2
import com.lanars.compose.datetextfield.Format
import com.lanars.compose.datetextfield.new.DateTextField3
import com.lanars.compose.datetextfieldexample.ui.theme.DateTextFieldExampleTheme
import org.threeten.bp.LocalDate

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            var dateValue by remember { mutableStateOf<LocalDate?>(null) }

            DateTextFieldExampleTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(
                            32.dp,
                            Alignment.CenterVertically
                        )
                    ) {
                        DateTextField3()

                        DateTextField2()

                        DateTextField(
                            modifier = Modifier.onFocusChanged {
                                Log.d(
                                    "DateInput",
                                    it.toString()
                                )
                            },
                            format = Format.DDMMYYYY,
                            minDate = LocalDate.of(2000, 12, 2),
                            maxDate = LocalDate.of(2020, 12, 30),
                            onValueChange = {
                                Log.d(
                                    "DateInput",
                                    "Day: ${it.day.joinToString()}; Month: ${it.month.joinToString()}; Year: ${it.year.joinToString()}"
                                )
                            },
                            onEditingComplete = { Log.d("DateInput", it.toString()) },
                            contentTextStyle = TextStyle(fontSize = 25.sp, color = Color.Black),
                            hintTextStyle = TextStyle(fontSize = 25.sp, color = Color.Gray),
                            delimiter = '.',
                            padding = DateDigitsPadding(6.dp),
                            value = dateValue
                        )
                    }
                }
            }
        }
    }
}
