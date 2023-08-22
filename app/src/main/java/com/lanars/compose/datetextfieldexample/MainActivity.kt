package com.lanars.compose.datetextfieldexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.lanars.compose.datetextfield.new.DateTextField3
import com.lanars.compose.datetextfieldexample.ui.theme.DateTextFieldExampleTheme

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
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DateTextField3(
                modifier = Modifier.imePadding(),
                textStyle = MaterialTheme.typography.displayMedium,
                hintTextStyle = MaterialTheme.typography.displayMedium.copy(color = Color.Gray),
                onValueChanged = {
                    Log.d("DateInput", it.toString())
                }
            )
        }
    }
}
