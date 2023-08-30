package com.lanars.compose.datetextfieldexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.lanars.compose.datetextfield.DateTextField
import com.lanars.compose.datetextfield.Format
import com.lanars.compose.datetextfieldexample.ui.theme.DateTextFieldExampleTheme
import org.threeten.bp.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DateTextFieldExampleTheme {
                DateTextFieldExample()
            }
        }
    }
}

@Composable
fun DateTextFieldExample() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        DateTextField(
            modifier = Modifier.onFocusChanged {
                Log.d(
                    "DateInput",
                    it.toString()
                )
            },
            format = Format.DDMMYYYY,
            minDate = LocalDate.now().minusYears(1),
            maxDate = LocalDate.now().plusYears(1),
            onValueChanged = { Log.d("DateInput", it.toString()) },
            textStyle = MaterialTheme.typography.displayMedium,
            hintTextStyle = MaterialTheme.typography.displayMedium.copy(color = Color.Gray),
            delimiter = '.',
            delimiterSpacing = 4.dp,
        )
    }
}
