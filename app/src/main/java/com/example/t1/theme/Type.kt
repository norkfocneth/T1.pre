package com.example.t1.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.t1.R

val Inter = FontFamily(
    Font(R.font.inter, FontWeight.Light),
    Font(R.font.inter, FontWeight.Normal),
    Font(R.font.inter, FontWeight.Medium),
    Font(R.font.inter, FontWeight.SemiBold),
    Font(R.font.inter, FontWeight.Bold),
    Font(R.font.inter, FontWeight.ExtraBold),
    Font(R.font.inter, FontWeight.Black),
)

val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk, FontWeight.Normal),
    Font(R.font.space_grotesk, FontWeight.Medium),
    Font(R.font.space_grotesk, FontWeight.SemiBold),
    Font(R.font.space_grotesk, FontWeight.Bold),
)

// Body text (Inter)
val BodyLarge = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

val BodyMedium = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
)

val BodySmall = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
)

// Display text (Space Grotesk) — for scores, headings, numbers
val DisplayLarge = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 60.sp,
    letterSpacing = (-0.5).sp,
)

val DisplayMedium = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    letterSpacing = (-0.5).sp,
)

val DisplaySmall = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    letterSpacing = (-0.25).sp,
)

val HeadlineLarge = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp,
    letterSpacing = (-0.25).sp,
)

val HeadlineMedium = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    letterSpacing = (-0.25).sp,
)

val HeadlineSmall = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    letterSpacing = (-0.25).sp,
)

val LabelLarge = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    letterSpacing = 0.5.sp,
)

val LabelMedium = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    letterSpacing = 0.5.sp,
)

val LabelSmall = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    letterSpacing = 1.5.sp,
)

// Tracking styles (wide letter-spacing like CSS tracking-[0.3em])
val TrackingWide = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    letterSpacing = 3.sp,
)

val TrackingMedium = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    letterSpacing = 2.5.sp,
)

val TrackingNarrow = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    letterSpacing = 1.5.sp,
)

// Score display
val ScoreXL = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 72.sp,
    letterSpacing = (-1).sp,
)

val ScoreLarge = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 60.sp,
    letterSpacing = (-0.5).sp,
)

val ScoreMedium = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
)

val ScoreSmall = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
)

// Timer
val TimerDisplay = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 56.sp,
    letterSpacing = 0.sp,
)
