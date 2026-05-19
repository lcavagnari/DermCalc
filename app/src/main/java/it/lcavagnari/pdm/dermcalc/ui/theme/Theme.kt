package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/** Composition local providing the current dark-mode state. Consume with `LocalDarkTheme.current`. */
val LocalDarkTheme = compositionLocalOf { false }
/** Composition local providing a callback to flip the dark/light theme. Consume with `LocalToggleDarkTheme.current`. */
val LocalToggleDarkTheme = compositionLocalOf<() -> Unit> { {} }

private val DarkColorScheme = darkColorScheme(
    primary              = Determination,
    onPrimary            = Color.Black,
    primaryContainer     = DeterminationCont,
    onPrimaryContainer   = Determination,
    secondary            = NeutralUi,
    onSecondary          = DarkBackground,
    secondaryContainer   = DarkSurfaceLow,
    onSecondaryContainer = DarkOnSurface,
    tertiary             = SoulRed,
    onTertiary           = Color.White,
    tertiaryContainer    = DarkSurfaceLow,
    onTertiaryContainer  = SoulRed,
    background           = DarkBackground,
    onBackground         = DarkOnSurface,
    surface              = DarkSurface,
    onSurface            = DarkOnSurface,
    surfaceVariant       = DarkSurfaceLow,
    onSurfaceVariant     = DarkOnSurfaceDim,
    outline              = DarkOutline,
    outlineVariant       = DarkOutlineDim,
    error                = SoulRed,
    onError              = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary              = DeterminationMuted,
    onPrimary            = Color.White,
    primaryContainer     = DeterminationContL,
    onPrimaryContainer   = DeterminationMuted,
    secondary            = NeutralUiL,
    onSecondary          = Color.White,
    secondaryContainer   = LightSurfaceLow,
    onSecondaryContainer = LightOnSurface,
    tertiary             = SoulRedMuted,
    onTertiary           = Color.White,
    tertiaryContainer    = LightSurfaceLow,
    onTertiaryContainer  = SoulRedMuted,
    background           = LightBackground,
    onBackground         = LightOnSurface,
    surface              = LightSurface,
    onSurface            = LightOnSurface,
    surfaceVariant       = LightSurfaceLow,
    onSurfaceVariant     = LightOnSurfaceDim,
    outline              = LightOutline,
    outlineVariant       = LightOutlineDim,
    error                = SoulRedMuted,
    onError              = Color.White
)

/**
 * Root Material3 theme for DermCalc.
 *
 * Provides [LocalDarkTheme] and [LocalToggleDarkTheme] composition locals so any composable
 * in the tree can read the current mode or request a toggle without prop-drilling.
 *
 * @param darkTheme whether to apply the dark color scheme. Defaults to the system setting.
 * @param onToggleDarkTheme callback invoked when [LocalToggleDarkTheme] is consumed and called.
 * @param content composable content rendered inside the theme.
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
            typography  = Typography,
            content     = content
        )
    }
}
