package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.ui.graphics.Color

// --- Dark-theme accents (high luminance — readable on near-black backgrounds) ---
val Yellow80 = Color(0xFFFFE033)   // bright yellow — dark theme primary
val Gold80 = Color(0xFFD4B060)     // warm gold — dark theme secondary
val Amber80 = Color(0xFFFFB830)    // amber — dark theme tertiary

// --- Light-theme accents (medium luminance — readable on white/off-white backgrounds) ---
val Yellow40 = Color(0xFF8C6B00)   // muted yellow-gold — light theme primary
val Gold40 = Color(0xFF6B5C00)     // dark gold — light theme secondary
val Amber40 = Color(0xFF8A5300)    // dark amber — light theme tertiary

// --- Dark scheme surfaces ---
val BackgroundDark = Color(0xFF1A1A1A)
val SurfaceDark = Color(0xFF2C2C2C)
val SurfaceVariantDark = Color(0xFF3A3A3A)

// --- Light scheme surfaces ---
val BackgroundLight = Color(0xFFFAFAF5)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF0EDE0)

// --- Severity indicators (consumed directly by result card composables) ---
val SeverityMild = Color(0xFF4CAF50)
val SeverityModerate = Color(0xFFF59300)       // amber — light mode
val SeverityModerateOnDark = Color(0xFFFFD600) // yellow — dark mode
val SeveritySevere = Color(0xFFE53935)