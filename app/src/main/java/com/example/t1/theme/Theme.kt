package com.example.t1.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Foreground,
    onPrimary = Background,
    secondary = Secondary,
    onSecondary = SecondaryForeground,
    background = Background,
    onBackground = Foreground,
    surface = Card,
    onSurface = CardForeground,
    error = Destructive,
    onError = DestructiveForeground,
    outline = Border,
    surfaceVariant = Muted,
    onSurfaceVariant = MutedForeground
)

val T1Typography = Typography(
    displayLarge = DisplayLarge,
    displayMedium = DisplayMedium,
    displaySmall = DisplaySmall,
    headlineLarge = HeadlineLarge,
    headlineMedium = HeadlineMedium,
    headlineSmall = HeadlineSmall,
    bodyLarge = BodyLarge,
    bodyMedium = BodyMedium,
    bodySmall = BodySmall,
    labelLarge = LabelLarge,
    labelMedium = LabelMedium,
    labelSmall = LabelSmall
)

@Composable
fun T1Theme(
    content: @Composable () -> Unit
) {
    // T1 is dark-only, matching the dark HSL tokens from the web app
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = T1Typography,
        content = content
    )
}
