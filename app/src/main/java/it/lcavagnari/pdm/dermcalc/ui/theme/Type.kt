package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import it.lcavagnari.pdm.dermcalc.R

val PixelDisplay      = FontFamily(Font(R.font.press_start_2p))
val PixelSoft         = FontFamily(Font(R.font.vt323))
val DeterminationMono = FontFamily(Font(R.font.determination_mono))
val ClinicalMono      = FontFamily(
    Font(R.font.jetbrains_mono_regular,  FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium,   FontWeight.Medium),
    Font(R.font.jetbrains_mono_semibold, FontWeight.SemiBold),
    Font(R.font.jetbrains_mono_bold,     FontWeight.Bold)
)

// Determination Mono is the app-wide font. It ships with a single Regular weight,
// so every style uses FontWeight.Normal — synthesized faux-bold on a pixel font
// renders poorly. Hierarchy comes from size, casing, and color instead of weight.
val Typography = Typography(
    displayLarge   = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.02.em),
    displayMedium  = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.02.em),
    displaySmall   = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.02.em),
    headlineLarge  = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 42.sp, lineHeight = 46.sp),
    headlineMedium = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 38.sp, lineHeight = 42.sp),
    headlineSmall  = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 34.sp, lineHeight = 38.sp),
    titleLarge     = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 26.sp, lineHeight = 34.sp),
    titleMedium    = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 24.sp, lineHeight = 32.sp),
    titleSmall     = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 22.sp, lineHeight = 30.sp),
    bodyLarge      = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 22.sp, lineHeight = 32.sp),
    bodyMedium     = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 28.sp),
    bodySmall      = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 24.sp),
    labelLarge     = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = 0.06.em),
    labelMedium    = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 17.sp, lineHeight = 22.sp),
    labelSmall     = TextStyle(fontFamily = DeterminationMono, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp)
)
