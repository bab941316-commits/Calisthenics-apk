package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val VibrantColorScheme = lightColorScheme(
    primary = VibrantPurple,
    onPrimary = Color.White,
    primaryContainer = VibrantPurpleContainer,
    onPrimaryContainer = VibrantPurpleDark,
    secondary = VibrantCyan,
    onSecondary = Color.White,
    secondaryContainer = VibrantCyanContainer,
    onSecondaryContainer = VibrantCyan,
    tertiary = VibrantPurpleLight,
    onTertiary = VibrantPurpleDark,
    tertiaryContainer = VibrantPurplePill,
    onTertiaryContainer = VibrantOnPill,
    background = VibrantBackground,
    onBackground = VibrantOnBackground,
    surface = VibrantSurface,
    onSurface = VibrantOnSurface,
    surfaceVariant = VibrantSurfaceVariant,
    onSurfaceVariant = VibrantOnSurfaceVariant,
    outline = VibrantOutline
)

private val DarkVibrantColorScheme = darkColorScheme(
    primary = VibrantPurpleLight,
    onPrimary = VibrantPurpleDark,
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = VibrantPurpleContainer,
    secondary = Color(0xFF80D5EC),
    onSecondary = Color(0xFF003642),
    secondaryContainer = Color(0xFF004E5F),
    onSecondaryContainer = VibrantCyanContainer,
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkVibrantColorScheme else VibrantColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
