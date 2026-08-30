package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = Color.White,
    primaryContainer = StudioSurfaceCard,
    onPrimaryContainer = ElectricVioletLight,
    secondary = HyperCyan,
    onSecondary = Color.Black,
    secondaryContainer = StudioSurfaceHover,
    onSecondaryContainer = HyperCyan,
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = StudioSurfaceCard,
    onTertiaryContainer = NeonPink,
    background = StudioBackground,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = StudioBorder,
    outlineVariant = StudioBorder
)

// A dark studio vibe is preferred for music production and song releasing
private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
