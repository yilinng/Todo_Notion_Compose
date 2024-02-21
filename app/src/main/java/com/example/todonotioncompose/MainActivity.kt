package com.example.todonotioncompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.todonotioncompose.ui.theme.TodoNotionComposeTheme
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TodoNotionComposeTheme {
                Surface {
                    val windowSize = calculateWindowSizeClass(this)
                    TodoNotionApp(
                        windowSize = windowSize.widthSizeClass,
                    )
                }
            }
        }
    }
}

