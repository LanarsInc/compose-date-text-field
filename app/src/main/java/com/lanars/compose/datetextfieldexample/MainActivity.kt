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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
                        DateTextField3(
                            textStyle = MaterialTheme.typography.h3,
                            hintTextStyle = MaterialTheme.typography.h3.copy(color = Color.Gray),
                            onValueChanged = {
                                Log.d("DateInput", it.toString())
                            },
                        )
                    }
                }
            }
        }
    }
}
