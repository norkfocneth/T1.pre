package com.example.t1.theme

import androidx.compose.ui.graphics.Color

// ─── Core Palette (from CSS HSL variables) ───
val Background = Color(0xFF0A0A0B)        // hsl(240, 6%, 4%)
val Foreground = Color(0xFFF5F5F5)        // hsl(0, 0%, 96%)

val Card = Color(0xFF131316)              // hsl(240, 5%, 8%)
val CardForeground = Color(0xFFF5F5F5)

val Secondary = Color(0xFF202028)         // hsl(240, 4%, 14%)
val SecondaryForeground = Color(0xFFF5F5F5)

val MutedForeground = Color(0xFF6E6E78)   // hsl(240, 3%, 46%)
val Muted = Color(0xFF202028)

val Accent = Color(0xFF282830)            // hsl(240, 4%, 18%)
val AccentForeground = Color(0xFFF5F5F5)

val Border = Color(0xFF222230)            // hsl(240, 4%, 15%)
val Input = Color(0xFF222230)

val Destructive = Color(0xFFDC2626)       // hsl(0, 72%, 51%)
val DestructiveForeground = Color(0xFFF5F5F5)

// ─── Custom Tokens ───
val NavBg = Color(0xFF16161A)             // hsl(240, 5%, 10%)
val NavActive = Color(0xFFFFFFFF)         // hsl(0, 0%, 100%)
val NavInactive = Color(0xFF575764)       // hsl(240, 3%, 38%)

val SignOut = Color(0xFFCC4444)           // hsl(0, 65%, 55%)
val Success = Color(0xFF2DB553)           // hsl(140, 70%, 45%)
val Danger = Color(0xFFDC2626)            // hsl(0, 72%, 51%)
val Warning = Color(0xFFF59E0B)           // hsl(38, 92%, 50%)
val Info = Color(0xFF3B82F6)              // hsl(210, 70%, 55%)

val GlowPrimary = Color(0xFF7B9FDB)       // hsl(220, 60%, 70%)
val Surface = Color(0xFF141418)           // hsl(240, 5%, 9%)
val SurfaceRaised = Color(0xFF1C1C22)     // hsl(240, 5%, 12%)

// ─── Gradient Colors ───
val GradientStart = Color(0xFF0E0E14)     // hsl(240, 8%, 6%)
val GradientMid = Color(0xFF0C0C10)       // hsl(240, 6%, 5%)
val GradientEnd = Color(0xFF070709)       // hsl(240, 10%, 3%)

val BgBase = Color(0xFF080808)            // hsl(0, 0%, 3%)
val BgTop = Color(0xFF0F0F0F)             // hsl(0, 0%, 6%)
val BgBottom = Color(0xFF0A0A0A)          // hsl(0, 0%, 4%)

val GlowWhite = Color(0xFFFFFFFF)
val GlowGreen = Color(0xFF4ADE80)         // hsl(142, 64%, 52%)

// ─── Rank Colors ───
val RankGold = Color(0xFFEAB308)          // hsl(45, 93%, 47%)
val RankSilver = Color(0xFFBFBFBF)        // hsl(0, 0%, 75%)
val RankBronze = Color(0xFFB8733D)        // hsl(30, 60%, 45%)

// ─── Streak Colors ───
val StreakSmall = Color(0xFFFB923C)        // orange-400
val StreakMedium = Color(0xFFF97316)       // orange-500
val StreakIntense = Color(0xFFEF4444)      // red-500

// ─── Breakdown Colors ───
val BreakdownSocial = Color(0xFFDC4444)    // hsl(0, 72%, 55%)
val BreakdownProductive = Color(0xFF2DB553) // hsl(140, 70%, 45%)

// ─── Avatar Colors ───
val AvatarYellow = Color(0xFFEAB308)
val AvatarEmerald = Color(0xFF10B981)
val AvatarRed = Color(0xFFEF4444)
val AvatarBlue = Color(0xFF3B82F6)
val AvatarPurple = Color(0xFF8B5CF6)
val AvatarPink = Color(0xFFEC4899)
val AvatarCyan = Color(0xFF06B6D4)
val AvatarOrange = Color(0xFFF97316)

val AvatarColors = listOf(
    AvatarYellow, AvatarEmerald, AvatarRed, AvatarBlue,
    AvatarPurple, AvatarPink, AvatarCyan, AvatarOrange
)

// ─── Emerald shades ───
val Emerald400 = Color(0xFF34D399)
val Emerald500 = Color(0xFF10B981)

// ─── Red shades ───
val Red400 = Color(0xFFF87171)
val Red500 = Color(0xFFEF4444)

// ─── Text gradient approximation ───
val GradientTextStart = Color(0xFFFFFFFF)  // hsl(0, 0%, 100%)
val GradientTextEnd = Color(0xFFD1D1D8)    // hsl(240, 3%, 85%)

// ─── Button gradient ───
val ButtonGradientStart = Color(0xFFFFFFFF) // hsl(0, 0%, 100%)
val ButtonGradientEnd = Color(0xFFE8E8EB)   // hsl(240, 3%, 92%)
