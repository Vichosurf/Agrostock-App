package com.example.agrostock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AgroGreenAccent,
    secondary = AgroGreenLight,
    tertiary = AgroRedControlled,
    background = AgroDarkHeader,
    surface = AgroDarkHeaderSub,
    onPrimary = AgroSurface,
    onSecondary = AgroTextPrimary,
    onBackground = AgroSurface,
    onSurface = AgroSurface
)

private val LightColorScheme = lightColorScheme(
    primary = AgroGreenPrimary,
    secondary = AgroGreenDark,
    tertiary = AgroRedControlled,
    background = AgroBackground,
    surface = AgroSurface,
    onPrimary = AgroSurface,
    onSecondary = AgroSurface,
    onBackground = AgroTextPrimary,
    onSurface = AgroTextPrimary
)

@Composable
fun AgrostockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
