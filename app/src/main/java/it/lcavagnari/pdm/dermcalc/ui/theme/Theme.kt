package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

/** Composition local providing the current dark/light theme state. */
val LocalDarkTheme = compositionLocalOf { false }

/** Composition local providing the callback to toggle dark/light theme. */
val LocalToggleDarkTheme = compositionLocalOf<() -> Unit> { {} }


private val DarkColorScheme = darkColorScheme(
    primary = Yellow80,
    onPrimary = Color.DarkGray,
    secondary = Gold80,
    onSecondary = Color.Black,
    tertiary = Amber80,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = Color.Gray,
    onSurface = Color.White,
    surfaceVariant = SurfaceVariantDark,
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
    surface = Color.Gray,
    onSurface = Color.Black,
    surfaceVariant = SurfaceVariantLight,
    outline = Color.Black
)

/**
 * Root Material3 theme wrapper for the entire app.
 *
 * Provides [LocalDarkTheme] and [LocalToggleDarkTheme] composition locals so any composable
 * in the tree can read the current dark/light state or request a toggle.
 *
 * @param darkTheme - whether to use the dark color scheme. Defaults to the system setting.
 * @param onToggleDarkTheme - callback invoked to flip the dark/light state.
 * @param content - the composable content rendered inside the theme.
 */
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
