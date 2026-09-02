package com.example.zyvo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepVioletBg = Color(0xFF0F0B1E)
val ElectricMagenta = Color(0xFFFF007A)
val GoldAccent = Color(0xFFFFD700)
val DarkSurface = Color(0xFF1B1437)
val CardSurface = Color(0xFF261D4C)
val TextLight = Color(0xFFF3E8FF)
val TextGray = Color(0xFFB0ACC0)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricMagenta,
    secondary = GoldAccent,
    background = DeepVioletBg,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextLight,
    onSurface = TextLight
)

@Composable
fun ZyvoLiveTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
