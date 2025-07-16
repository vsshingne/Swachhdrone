package com.example.firebaseauthapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1D4ED8),      // Primary Blue
    secondary = Color(0xFF10B981),    // Eco Green
    tertiary = Color(0xFFFACC15),     // Alert Yellow
    background = Color(0xFF1A1A1A),   // Dark Background
    surface = Color(0xFF2D2D2D),      // Dark Surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF1F2937),   // Dark Text
    onBackground = Color(0xFFE0E0E0), // Light Gray
    onSurface = Color(0xFFE0E0E0),    // Light Gray
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1D4ED8),      // Primary Blue
    secondary = Color(0xFF10B981),    // Eco Green
    tertiary = Color(0xFFFACC15),     // Alert Yellow
    background = Color(0xFFF9FAFB),   // Light Background
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF1F2937),   // Dark Text
    onBackground = Color(0xFF1F2937), // Dark Text
    onSurface = Color(0xFF1F2937),    // Dark Text
    error = Color(0xFFB00020)
)

@Composable
fun FirebaseAuthAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.setStatusBarColor(colorScheme.primary.toArgb())
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}