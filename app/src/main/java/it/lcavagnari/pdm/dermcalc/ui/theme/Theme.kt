package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

val LocalDarkTheme = compositionLocalOf { false }
val LocalToggleDarkTheme = compositionLocalOf<() -> Unit> { {} }


private val DarkColorScheme = darkColorScheme(
    primary = Yellow80,
    onPrimary = Color.Black,
    secondary = Gold80,
    onSecondary = Color.Black,
    tertiary = Amber80,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    outline = Color.White // For the classic dialogue borders
)

private val LightColorScheme = lightColorScheme(
    primary = Yellow40,
    onPrimary = Color.White,
    secondary = Gold40,
    onSecondary = Color.White,
    tertiary = Amber40,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = Color.Black,
    surface = SurfaceLight,
    onSurface = Color.Black,
    outline = Color.Black
)

@Composable
fun DermCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    onToggleDarkTheme: () -> Unit = {},
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalToggleDarkTheme provides onToggleDarkTheme
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}