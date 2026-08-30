package com.example.t1.ui.splash

import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.DisplayLarge
import com.example.t1.theme.Inter
import com.example.t1.theme.SpaceGrotesk
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Ultra-Premium, Netflix-Style Cinematic Intro Splash Screen for T1.
 * 
 * Choreography:
 * 1. Pitch black OLED canvas.
 * 2. Stage 1 (0ms - 800ms): "T1." emerges slowly & majestically with luminous ambient glow.
 * 3. Stage 2 (800ms - 1600ms): "TOP 1%" sub-headline tracks in with elegant letter spacing & haptics.
 * 4. Stage 3 (1600ms - 2100ms): Readability hold for visual absorption.
 * 5. Stage 4 (2100ms - 2800ms): Iconic Ta-Dum Exponential Zoom Surge into the camera.
 * 6. Smooth seamless reveal into the App.
 */
@Composable
fun CinematicSplashScreen(
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view: View = LocalView.current

    // Animation Controllers
    val t1Alpha = remember { Animatable(0f) }
    val t1Scale = remember { Animatable(0.88f) }
    
    val subtitleAlpha = remember { Animatable(0f) }
    val subtitleLetterSpacing = remember { Animatable(3f) }
    
    val cinematicZoom = remember { Animatable(1f) }
    val cinematicFadeOut = remember { Animatable(1f) }
    val ambientGlowIntensity = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // ─── Stage 1: "T1." Emerges Calmly & Majestically (0ms - 800ms) ───
        launch {
            t1Alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
        launch {
            t1Scale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 800, easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f))
            )
        }
        launch {
            ambientGlowIntensity.animateTo(
                targetValue = 0.6f,
                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
            )
        }

        delay(600)
        try { Haptics.playLight(view) } catch (_: Exception) {}

        // ─── Stage 2: "TOP 1%" Subtitle Fade & Graceful Tracking Expansion (800ms - 1600ms) ───
        delay(200)
        launch {
            subtitleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
        launch {
            subtitleLetterSpacing.animateTo(
                targetValue = 7.5f,
                animationSpec = tween(durationMillis = 750, easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f))
            )
        }

        delay(400)
        try { Haptics.playLight(view) } catch (_: Exception) {}

        // ─── Stage 3: Readability & Brand Absorption Hold (1600ms - 2100ms) ───
        delay(500)

        // ─── Stage 4: The Iconic Netflix Zoom Surge (2100ms - 2800ms) ───
        try { Haptics.playMedium(view) } catch (_: Exception) {}

        launch {
            ambientGlowIntensity.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
        launch {
            cinematicZoom.animateTo(
                targetValue = 15.0f,
                animationSpec = tween(
                    durationMillis = 700,
                    easing = CubicBezierEasing(0.7f, 0f, 0.84f, 0f) // Exponential zoom surge
                )
            )
        }
        launch {
            delay(350)
            cinematicFadeOut.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 350, easing = LinearEasing)
            )
        }

        delay(720)

        // ─── Stage 5: Trigger transition to App ───
        onAnimationComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Radial Cinematic Glow behind logo
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val glowRadius = size.minDimension * 0.7f * cinematicZoom.value

            if (ambientGlowIntensity.value > 0.01f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF80BF).copy(alpha = 0.24f * ambientGlowIntensity.value * cinematicFadeOut.value),
                            Color(0xFF80D8FF).copy(alpha = 0.14f * ambientGlowIntensity.value * cinematicFadeOut.value),
                            Color.Transparent
                        ),
                        center = centerOffset,
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = centerOffset
                )
            }
        }

        // Main Animated Typography Container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(cinematicZoom.value * t1Scale.value)
                .alpha(cinematicFadeOut.value)
        ) {
            // "T1." Brand Heading
            Text(
                text = "T1.",
                style = DisplayLarge.copy(
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 72.sp,
                    letterSpacing = (-2.5).sp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFE2E8F0),
                            Color(0xFFFF80BF)
                        )
                    )
                ),
                modifier = Modifier.alpha(t1Alpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // "TOP 1%" Subtitle
            Text(
                text = "TOP 1%",
                style = DisplayLarge.copy(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = subtitleLetterSpacing.value.sp,
                    color = Color(0xFF94A3B8)
                ),
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .padding(start = (subtitleLetterSpacing.value / 2).dp) // Center optical alignment
            )
        }
    }
}
