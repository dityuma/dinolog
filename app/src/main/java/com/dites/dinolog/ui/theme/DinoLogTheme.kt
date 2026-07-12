package com.dites.dinolog.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppTheme {
    ALDABRA_GIANT,
    SULCATA_DESERT,
    RADIATA_STARBURST,
    CHERRY_HEAD,
    PARDALIS_SAVANNA,
    CITRUS_SPARK,
    TEAL_BLOSSOM,
    SAGE_GARDEN
}

// ALDABRA_GIANT (Always dark)
val AldabraGiantColors = darkColorScheme(
    background = Color(0xFF2A2A2A),
    surface = Color(0xFF3A3A3A),
    surfaceVariant = Color(0xFF444444),
    primary = Color(0xFF9E9E9E),
    onPrimary = Color(0xFF1A1A1A),
    onBackground = Color(0xFFE8E8E0),
    onSurface = Color(0xFFE8E8E0),
    onSurfaceVariant = Color(0xFFBDBDB0),
    outline = Color(0xFF616161)
)

// SULCATA_DESERT
val SulcataDesertLightColors = lightColorScheme(
    background = Color(0xFFF5EDD6),
    surface = Color(0xFFE8D5A8),
    surfaceVariant = Color(0xFFEEDFC0),
    primary = Color(0xFFC8923A),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF5C3D0E),
    onSurface = Color(0xFF5C3D0E),
    onSurfaceVariant = Color(0xFF8B6914),
    outline = Color(0xFFD4A55A)
)

val SulcataDesertDarkColors = darkColorScheme(
    background = Color(0xFF2C1E08),
    surface = Color(0xFF3D2A10),
    surfaceVariant = Color(0xFF4A3418),
    primary = Color(0xFFC8923A),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFFF5DFA8),
    onSurface = Color(0xFFF5DFA8),
    onSurfaceVariant = Color(0xFFD4A55A),
    outline = Color(0xFF8B6914)
)

// RADIATA_STARBURST (Always dark)
val RadiataStarburstColors = darkColorScheme(
    background = Color(0xFF1A1A0F),
    surface = Color(0xFF2A2A18),
    surfaceVariant = Color(0xFF333320),
    primary = Color(0xFFF5C842),
    onPrimary = Color(0xFF1A1A0F),
    onBackground = Color(0xFFF5C842),
    onSurface = Color(0xFFF5E070),
    onSurfaceVariant = Color(0xFFD4A800),
    outline = Color(0xFF8B7200)
)

// CHERRY_HEAD
val CherryHeadLightColors = lightColorScheme(
    background = Color(0xFFF9F0EE),
    surface = Color(0xFFF0E0DC),
    surfaceVariant = Color(0xFFF5E8E5),
    primary = Color(0xFFC41E3A),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF2C1810),
    onSurface = Color(0xFF2C1810),
    onSurfaceVariant = Color(0xFF8B1A1A),
    outline = Color(0xFFE63950)
)

val CherryHeadDarkColors = darkColorScheme(
    background = Color(0xFF1A0808),
    surface = Color(0xFF2A1010),
    surfaceVariant = Color(0xFF381818),
    primary = Color(0xFFE63950),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFFF9D0D0),
    onSurface = Color(0xFFF9D0D0),
    onSurfaceVariant = Color(0xFFE63950),
    outline = Color(0xFF8B1A1A)
)

// PARDALIS_SAVANNA
val PardalisSavannaLightColors = lightColorScheme(
    background = Color(0xFFF7F0D8),
    surface = Color(0xFFEDE5B8),
    surfaceVariant = Color(0xFFF2ECC8),
    primary = Color(0xFF2C2C00),
    onPrimary = Color(0xFFF7F0D8),
    onBackground = Color(0xFF1A1A00),
    onSurface = Color(0xFF1A1A00),
    onSurfaceVariant = Color(0xFF5C4A00),
    outline = Color(0xFF3D3D00)
)

val PardalisSavannaDarkColors = darkColorScheme(
    background = Color(0xFF1A1A00),
    surface = Color(0xFF2A2A08),
    surfaceVariant = Color(0xFF333310),
    primary = Color(0xFFF5C842),
    onPrimary = Color(0xFF1A1A00),
    onBackground = Color(0xFFF7F0D8),
    onSurface = Color(0xFFF7F0D8),
    onSurfaceVariant = Color(0xFFD4C060),
    outline = Color(0xFF8B7200)
)

// CITRUS_SPARK
val CitrusSparkLightColors = lightColorScheme(
    background = Color(0xFFFFF176),
    surface = Color(0xFFFFFDE7),
    surfaceVariant = Color(0xFFFFF9C4),
    primary = Color(0xFFF06292),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0D47A1),
    onSurface = Color(0xFF0D47A1),
    onSurfaceVariant = Color(0xFF1565C0),
    outline = Color(0xFFFFEB3B)
)

val CitrusSparkDarkColors = darkColorScheme(
    background = Color(0xFF212121),
    surface = Color(0xFF424242),
    surfaceVariant = Color(0xFF616161),
    primary = Color(0xFFF06292),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFFBBDEFB),
    onSurface = Color(0xFFBBDEFB),
    onSurfaceVariant = Color(0xFF90CAF9),
    outline = Color(0xFF757575)
)

// TEAL_BLOSSOM
val TealBlossomLightColors = lightColorScheme(
    background = Color(0xFF00897B),
    surface = Color(0xFF00695C),
    surfaceVariant = Color(0xFF00796B),
    primary = Color(0xFFF8F0F0),
    onPrimary = Color(0xFFE91E8C),
    onBackground = Color(0xFFFCE4EC),
    onSurface = Color(0xFFFCE4EC),
    onSurfaceVariant = Color(0xFFE0F2F1),
    outline = Color(0xFF80CBC4)
)

val TealBlossomDarkColors = darkColorScheme(
    background = Color(0xFF004D40),
    surface = Color(0xFF00695C),
    surfaceVariant = Color(0xFF00796B),
    primary = Color(0xFFF8F0F0),
    onPrimary = Color(0xFFE91E8C),
    onBackground = Color(0xFFFCE4EC),
    onSurface = Color(0xFFFCE4EC),
    onSurfaceVariant = Color(0xFFB2DFDB),
    outline = Color(0xFF4DB6AC)
)

// SAGE_GARDEN
val SageGardenLightColors = lightColorScheme(
    background = Color(0xFFFAF7F0),
    surface = Color(0xFFF0EBE0),
    surfaceVariant = Color(0xFFF5F0E8),
    primary = Color(0xFFC4A882),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF4A7C59),
    onSurface = Color(0xFF4A7C59),
    onSurfaceVariant = Color(0xFF7D9B76),
    outline = Color(0xFFC4A882)
)

val SageGardenDarkColors = darkColorScheme(
    background = Color(0xFF1A1F18),
    surface = Color(0xFF252C22),
    surfaceVariant = Color(0xFF2E372B),
    primary = Color(0xFFC4A882),
    onPrimary = Color(0xFF1A1F18),
    onBackground = Color(0xFFB8D4B0),
    onSurface = Color(0xFFB8D4B0),
    onSurfaceVariant = Color(0xFF8FB888),
    outline = Color(0xFF7D9B76)
)
