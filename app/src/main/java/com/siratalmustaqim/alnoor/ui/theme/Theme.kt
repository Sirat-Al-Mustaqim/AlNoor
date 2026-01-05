package com.siratalmustaqim.alnoor.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreenNight,
    onPrimary = White,
    primaryContainer = EmeraldGreenDark,
    onPrimaryContainer = EmeraldGreenLight,
    secondary = GoldLight,
    onSecondary = Black,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,
    tertiary = Gold,
    onTertiary = Black,
    background = CharcoalGray,
    onBackground = White,
    surface = SurfaceDark,
    onSurface = White,
    surfaceVariant = CardDark,
    onSurfaceVariant = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = White,
    primaryContainer = EmeraldGreenLight,
    onPrimaryContainer = EmeraldGreenDark,
    secondary = Gold,
    onSecondary = Black,
    secondaryContainer = GoldLight,
    onSecondaryContainer = GoldDark,
    tertiary = GoldDark,
    onTertiary = White,
    background = OffWhite,
    onBackground = CharcoalGray,
    surface = SurfaceLight,
    onSurface = CharcoalGray,
    surfaceVariant = CardLight,
    onSurfaceVariant = DarkGray
)

@Composable
fun AlNoorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}