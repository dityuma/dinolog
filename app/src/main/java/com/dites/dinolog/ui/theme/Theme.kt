package com.dites.dinolog.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = Pink400,
    onPrimary = White,
    primaryContainer = Pink200,
    onPrimaryContainer = Pink700,
    secondary = Blue800,
    onSecondary = White,
    secondaryContainer = Blue50,
    onSecondaryContainer = Blue900,
    tertiary = Yellow400,
    onTertiary = Blue900,
    background = Yellow200,
    onBackground = Blue900,
    surface = Yellow50,
    onSurface = Blue900,
    surfaceVariant = Yellow100,
    onSurfaceVariant = Blue800,
    outline = Yellow400,
    outlineVariant = Yellow300,
    error = Red400,
    onError = White,
    errorContainer = Red100,
    onErrorContainer = Red400
)

private val DarkColorScheme = darkColorScheme(
    primary = Pink400,
    onPrimary = White,
    primaryContainer = Pink700,
    onPrimaryContainer = Pink200,
    secondary = Blue200,
    onSecondary = Blue900,
    secondaryContainer = Blue900,
    onSecondaryContainer = Blue100,
    tertiary = Yellow300,
    onTertiary = Gray900,
    background = Gray900,
    onBackground = Blue100,
    surface = Gray800,
    onSurface = Blue100,
    surfaceVariant = Gray800,
    onSurfaceVariant = Blue200,
    outline = Gray600,
    outlineVariant = Gray400,
    error = Red400,
    onError = White,
    errorContainer = Red400,
    onErrorContainer = Red100
)

@Composable
fun DinoLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color to use our custom scheme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
