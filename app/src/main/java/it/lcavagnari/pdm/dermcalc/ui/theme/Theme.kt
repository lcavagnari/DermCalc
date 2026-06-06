package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute


/** Composition local providing the current dark-mode state. Consume with `LocalDarkTheme.current`. */
val LocalDarkTheme = compositionLocalOf { false }
/** Composition local providing a callback to flip the dark/light theme. Consume with `LocalToggleDarkTheme.current`. */
val LocalToggleDarkTheme = compositionLocalOf { {} }
/** Composition local providing a throttled navigate callback. Consume with `LocalNavigate.current`. */
val LocalNavigate = compositionLocalOf<(AppRoute) -> Unit> { {} }
/** Composition local providing whether the current screen has fully settled (not mid-transition). Consume with `LocalIsIdle.current`. */
val LocalIsIdle = compositionLocalOf { true }
/** Composition local providing the bar alpha value for the current theme. Consume with `LocalBarAlpha.current`. */
val LocalBarAlpha = compositionLocalOf { 0.90f }

private val DarkColorScheme = darkColorScheme(
    primary              = Determination,
    onPrimary            = Color.Black,
    primaryContainer     = DeterminationCont,
    onPrimaryContainer   = Determination,
    secondary            = NeutralUi,
    onSecondary          = DarkBackground,
    secondaryContainer   = DarkSurfaceLow,
    onSecondaryContainer = DarkOnSurface,
    tertiary             = SoulDetermination,
    onTertiary           = Color.White,
    tertiaryContainer    = DarkSurfaceLow,
    onTertiaryContainer  = SoulDetermination,
    background           = DarkBackground,
    onBackground         = DarkOnSurface,
    surface              = DarkSurface,
    onSurface            = DarkOnSurface,
    surfaceVariant       = DarkSurfaceLow,
    inverseSurface       = LightSurfaceLow,
    onSurfaceVariant     = DarkOnSurfaceDim,
    outline              = DarkOutline,
    outlineVariant       = DarkOutlineDim,
    error                = SoulDetermination,
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
    inverseSurface       = DarkSurfaceLow,
    onSurfaceVariant     = LightOnSurfaceDim,
    outline              = LightOutline,
    outlineVariant       = LightOutlineDim,
    error                = SoulRedMuted,
    onError              = Color.White
)



fun onSoul(soulColor: Color): Color {
    return if (soulColor.luminance() > 0.18f) DarkSurface else LightSurface
}

// MaterialTheme.colorScheme.surfaceVariant
fun onSoulContainer(soulColor: Color): Color {
    return if (soulColor.luminance() > 0.18f) LightSurfaceLow else DarkSurfaceLow
}

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
    barAlpha: Float = if (darkTheme) 0.42f else 0.90f,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalToggleDarkTheme provides onToggleDarkTheme,
        LocalBarAlpha provides barAlpha
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography  = Typography,
            content     = content
        )
    }
}

//region TODOs
// L16,18,22: `compositionLocalOf` for rarely-changing values (bool, lambda, float) — use `staticCompositionLocalOf` to avoid unnecessary recomposition
// L101: magic number `0.42f` / `0.90f` for bar alpha — should be named constants
// L111: `typography  = Typography` — double space before `=`
//endregion

