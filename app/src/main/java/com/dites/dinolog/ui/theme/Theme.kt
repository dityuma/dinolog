package com.dites.dinolog.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun DinoLogTheme(
    appTheme: AppTheme = AppTheme.CITRUS_SPARK,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.ALDABRA_GIANT -> AldabraGiantColors
        AppTheme.SULCATA_DESERT -> if (darkTheme) SulcataDesertDarkColors else SulcataDesertLightColors
        AppTheme.RADIATA_STARBURST -> RadiataStarburstColors
        AppTheme.CHERRY_HEAD -> if (darkTheme) CherryHeadDarkColors else CherryHeadLightColors
        AppTheme.PARDALIS_SAVANNA -> if (darkTheme) PardalisSavannaDarkColors else PardalisSavannaLightColors
        AppTheme.CITRUS_SPARK -> if (darkTheme) CitrusSparkDarkColors else CitrusSparkLightColors
        AppTheme.TEAL_BLOSSOM -> if (darkTheme) TealBlossomDarkColors else TealBlossomLightColors
        AppTheme.SAGE_GARDEN -> if (darkTheme) SageGardenDarkColors else SageGardenLightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = 
                !(darkTheme || appTheme == AppTheme.ALDABRA_GIANT || appTheme == AppTheme.RADIATA_STARBURST)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
