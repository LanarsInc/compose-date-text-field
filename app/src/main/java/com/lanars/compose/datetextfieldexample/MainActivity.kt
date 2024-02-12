package com.lanars.compose.datetextfieldexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.lanars.compose.datetextfield.DateTextField
import com.lanars.compose.datetextfield.Format
import com.lanars.compose.datetextfieldexample.ui.theme.DateTextFieldExampleTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DateTextFieldExampleTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                            Text(text = "Min date: 02/12/2000 - Max date 30/12/2100", style = TextStyle(fontSize = 12.sp, color = Color.DarkGray))
                            DateTextField(
                                modifier = Modifier.onFocusChanged {
                                    Log.d(
                                        "DateInput",
                                        it.toString()
                                    )
                                },
                                format = Format.DDMMYYYY,
                                minDate = LocalDate.of(2000, 12, 2),
                                maxDate = LocalDate.of(2100, 12, 30),
                                onValueChanged = { Log.d("DateInput", it.toString()) },
                                cursorBrush = SolidColor(Color.Red),
                                textStyle = MaterialTheme.typography.displayMedium.copy(color = Color.Red),
                                hintTextStyle = MaterialTheme.typography.displayMedium.copy(color = Color.Gray),
                                delimiter = '.',
                                delimiterSpacing = 4.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}
