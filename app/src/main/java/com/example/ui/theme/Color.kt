package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Cinematic Deep Space / Cyber Stream Palette
val CinemaBackground = Color(0xFF090C15)
val CinemaSurface = Color(0xFF0F1524)
val CinemaSurfaceVariant = Color(0xFF161F33)
val CinemaSurfaceHighlight = Color(0xFF1E2942)

val ElectricCyan = Color(0xFF00E5FF)
val NeonViolet = Color(0xFF8B5CF6)
val StreamAccent = Color(0xFF6366F1)
val CinemaRed = Color(0xFFFF3366)
val CinemaGold = Color(0xFFFFB800)
val StreamGreen = Color(0xFF10B981)

val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004D5B),
    onPrimaryContainer = Color(0xFF80F2FF),
    secondary = NeonViolet,
    onSecondary = Color(0xFF280056),
    secondaryContainer = Color(0xFF3F1B85),
    onSecondaryContainer = Color(0xFFE9DDFF),
    tertiary = CinemaGold,
    onTertiary = Color(0xFF422C00),
    background = CinemaBackground,
    onBackground = TextPrimary,
    surface = CinemaSurface,
    onSurface = TextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = CinemaSurfaceVariant,
    surfaceContainerHigh = CinemaSurfaceHighlight,
    outline = Color(0xFF2A3650),
    error = CinemaRed
)
