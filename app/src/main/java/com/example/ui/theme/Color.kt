package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ==========================================
// ORANGE & WHITE THEME PALETTE
// ==========================================

// Orange Primary Brand Accents
val BrandOrange = Color(0xFFFF6600)
val BrandOrangeDark = Color(0xFFE65100)
val BrandOrangeLight = Color(0xFFFF9800)
val BrandSunsetAmber = Color(0xFFFFA000)
val BrandSunsetRed = Color(0xFFFF3D00)
val PureWhite = Color(0xFFFFFFFF)

// Backwards-compatible Brand Tokens mapped to Orange & White aesthetic
val ElectricViolet = BrandOrange
val ElectricVioletDark = BrandOrangeDark
val ElectricVioletLight = BrandOrangeLight

val HyperCyan = BrandSunsetAmber
val HyperCyanDark = Color(0xFFCC7000)
val HyperCyanMuted = Color(0x33FF9800)

val NeonPink = BrandSunsetRed
val NeonPinkMuted = Color(0x33FF3D00)

val MintGreen = Color(0xFF00C853)
val MintGreenDark = Color(0xFF009624)
val MintGreenMuted = Color(0x3300C853)

val AmberWarning = Color(0xFFFF9100)
val AmberWarningMuted = Color(0x33FF9100)

// Sunburst & Sunset Accents
val SunburstAmber = Color(0xFFFF9100)
val SoftSunrisePink = Color(0xFFFF7043)
val SkyGlowCyan = Color(0xFFFFAB40)

// Studio Dark Theme Palette (Deep Studio Midnight & Navy Blue)
val StudioBackground = Color(0xFF0A1128)
val StudioSurface = Color(0xFF101F3C)
val StudioSurfaceCard = Color(0xFF172B52)
val StudioSurfaceHover = Color(0xFF1F3868)
val StudioBorder = Color(0xFF2B4C84)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFCBDDF8)
val TextMuted = Color(0xFF86A3CF)

// Studio Light Theme Palette (Luminous Azure Sky & Ice Blue Canvas)
val LightStudioBackground = Color(0xFFE8F1FC)
val LightStudioSurface = Color(0xFFFFFFFF)
val LightStudioSurfaceCard = Color(0xFFFFFFFF)
val LightStudioSurfaceHover = Color(0xFFD6E8FB)
val LightStudioBorder = Color(0xFFBFDCF9)
val LightTextPrimary = Color(0xFF0A192F)
val LightTextSecondary = Color(0xFF335378)
val LightTextMuted = Color(0xFF6B8BAE)

@Immutable
data class StudioColors(
    val background: Color,
    val surface: Color,
    val surfaceCard: Color,
    val surfaceHover: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isLight: Boolean
)

val DarkStudioColors = StudioColors(
    background = StudioBackground,
    surface = StudioSurface,
    surfaceCard = StudioSurfaceCard,
    surfaceHover = StudioSurfaceHover,
    border = StudioBorder,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textMuted = TextMuted,
    isLight = false
)

val LightStudioColors = StudioColors(
    background = LightStudioBackground,
    surface = LightStudioSurface,
    surfaceCard = LightStudioSurfaceCard,
    surfaceHover = LightStudioSurfaceHover,
    border = LightStudioBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    isLight = true
)

val LocalStudioColors = staticCompositionLocalOf { LightStudioColors }

object AppTheme {
    val colors: StudioColors
        @Composable
        @ReadOnlyComposable
        get() = LocalStudioColors.current
}
