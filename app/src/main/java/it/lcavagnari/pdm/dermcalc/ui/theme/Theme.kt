package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Yellow80,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF4D3900),
    onPrimaryContainer = Yellow80,
    secondary = Gold80,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3D2F00),
    onSecondaryContainer = Color(0xFFF5DFA0),
    tertiary = Amber80,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = Color(0xFFF0EFE9),
    surface = SurfaceDark,
    onSurface = Color(0xFFE8E7E1),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC8C7C1),
    outline = Color(0xFF8A8A8A),
)

private val LightColorScheme = lightColorScheme(
    primary = Yellow40,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFF0C2),
    onPrimaryContainer = Color(0xFF2B1F00),
    secondary = Gold40,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFF5E5A0),
    onSecondaryContainer = Color(0xFF231A00),
    tertiary = Amber40,
    onTertiary = Color.Black,
    background = BackgroundLight,
    onBackground = Color(0xFF1A1A1A),
    surface = SurfaceLight,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF4A4540),
    outline = Color(0xFF8C8070),
)

@Composable
fun DermCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
