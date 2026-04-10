package com.example.muniar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val IndustrialColorScheme = darkColorScheme(
    primary = IndustrialPrimary,
    secondary = IndustrialSecondary,
    background = IndustrialBlack,
    surface = IndustrialSurface,
    error = IndustrialError,
    onPrimary = IndustrialBlack,
    onSecondary = IndustrialBlack,
    onBackground = HighContrastWhite,
    onSurface = HighContrastWhite,
    onError = HighContrastWhite
)

@Composable
fun IndustrialTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IndustrialColorScheme,
        typography = Typography,
        content = content
    )
}
