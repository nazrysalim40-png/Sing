package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Orange & White Dark Scheme on Midnight Blue Background
private val DarkColorScheme = darkColorScheme(
    primary = BrandOrange,
    onPrimary = Color.White,
    primaryContainer = StudioSurfaceCard,
    onPrimaryContainer = BrandOrangeLight,
    secondary = BrandSunsetAmber,
    onSecondary = Color.Black,
    secondaryContainer = StudioSurfaceHover,
    onSecondaryContainer = BrandOrangeLight,
    tertiary = BrandSunsetRed,
    onTertiary = Color.White,
    tertiaryContainer = StudioSurfaceCard,
    onTertiaryContainer = BrandSunsetRed,
    background = StudioBackground,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = StudioBorder,
    outlineVariant = StudioBorder
)

// Crisp Orange & Pure White Scheme on Luminous Azure/Ice Blue Background
private val LightColorScheme = lightColorScheme(
    primary = BrandOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E8FB),
    onPrimaryContainer = BrandOrangeDark,
    secondary = BrandSunsetAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2EFFD),
    onSecondaryContainer = BrandOrangeDark,
    tertiary = BrandSunsetRed,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFEBE6),
    onTertiaryContainer = BrandSunsetRed,
    background = LightStudioBackground,
    onBackground = LightTextPrimary,
    surface = LightStudioSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightStudioSurfaceHover,
    onSurfaceVariant = LightTextSecondary,
    outline = LightStudioBorder,
    outlineVariant = LightStudioBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val studioColors = if (darkTheme) DarkStudioColors else LightStudioColors

    CompositionLocalProvider(
        LocalStudioColors provides studioColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
