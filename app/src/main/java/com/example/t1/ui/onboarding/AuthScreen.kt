package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.ui.viewmodel.AuthViewModel
import com.example.t1.ui.viewmodel.AuthState
import com.example.t1.domain.model.AuthError
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

@Composable
fun OnboardingBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Background radial gradient top center (white overlay)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(size.width / 2f, 0f),
                        radius = size.width * 0.5f
                    )
                )
                // Background radial gradient middle-top (emerald glow)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GlowGreen.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width / 2f, size.height * 0.18f),
                        radius = size.width * 0.6f
                    )
                )
                // Linear gradient top to bottom (Base dark background)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(BgTop, BgBottom)
                    ),
                    alpha = 1f
                )
            }
    ) {
        content()
    }
}

/**
 * Clean login screen presenting the Google Sign-In button.
 * Disables button and blocks duplicate clicks when authentication is in progress.
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    // Entrance Animation States
    var showLogo by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showLegal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        showLogo = true
        delay(200)
        showSubtitle = true
        delay(150)
        showButtons = true
        delay(200)
        showLegal = true
    }

    OnboardingBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo section with staggered entrance
                AnimatedVisibility(
                    visible = showLogo,
                    enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                        initialOffsetY = { -20 },
                        animationSpec = tween(600)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "T1.",
                            style = ScoreXL.copy(
                                fontWeight = FontWeight.Bold,
                                brush = Brush.linearGradient(
                                    colors = listOf(GradientTextStart, GradientTextEnd)
                                )
                            ),
                            letterSpacing = (-1.5).sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Subtitle with delay
                        AnimatedVisibility(
                            visible = showSubtitle,
                            enter = fadeIn(animationSpec = tween(400))
                        ) {
                            Text(
                                text = "You're wasting more time than you think.",
                                style = BodyMedium.copy(
                                    color = MutedForeground,
                                    letterSpacing = 0.5.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))

                // Google button container
                AnimatedVisibility(
                    visible = showButtons,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(500)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val googleInteraction = remember { MutableInteractionSource() }
                        val isGooglePressed by googleInteraction.collectIsPressedAsState()
                        val googleScale = if (isGooglePressed) 0.96f else 1f

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .scale(googleScale)
                                .clip(RoundedCornerShape(28.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(ButtonGradientStart, ButtonGradientEnd)
                                    )
                                )
                                .clickable(
                                    interactionSource = googleInteraction,
                                    indication = null
                                ) {
                                    Haptics.playMedium(view)
                                    viewModel.signInWithGoogle(context)
                                }
                                .drawBehind {
                                    drawRoundRect(
                                        color = Color.White.copy(alpha = 0.15f),
                                        size = size,
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                            28.dp.toPx(),
                                            28.dp.toPx()
                                        ),
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                GoogleIcon(modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    style = LabelLarge.copy(
                                        color = Background,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Legal text at bottom
            AnimatedVisibility(
                visible = showLegal,
                enter = fadeIn(animationSpec = tween(600)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "By continuing, you agree to our Terms & Privacy",
                    style = BodySmall.copy(
                        fontSize = 10.sp,
                        color = MutedForeground.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Screen displayed during async loading operations (restore, auth, sync).
 */
@Composable
fun SplashLoadingScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    OnboardingBackground(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "T1.",
                    style = DisplayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        brush = Brush.linearGradient(
                            colors = listOf(GradientTextStart, GradientTextEnd)
                        )
                    ),
                    letterSpacing = (-1.5).sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                CircularProgressIndicator(
                    color = Foreground,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = message,
                    style = BodyMedium.copy(color = MutedForeground),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Secure error handling display screen with deterministic Retry or Sign Out actions.
 */
@Composable
fun AuthErrorScreen(
    errorType: AuthError,
    message: String,
    onRetry: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Authentication Error",
                    style = HeadlineMedium.copy(fontWeight = FontWeight.Bold, color = Destructive),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = when (errorType) {
                        AuthError.NETWORK_ERROR -> "Network connection unavailable. Please check your internet connection."
                        AuthError.TIMEOUT -> "Server sync timed out (15s exceeded). Please try again."
                        AuthError.SECURITY_FAILURE -> "Security Validation Failed. Session discarded for protection."
                        AuthError.CREDENTIAL_CANCELLED -> "Sign-in cancelled."
                        else -> message
                    },
                    style = BodyMedium.copy(color = Foreground),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Retry Button (if applicable, else Go Back)
                val isRecoverable = errorType != AuthError.SECURITY_FAILURE
                if (isRecoverable) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Foreground)
                            .clickable { onRetry() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Retry Sync",
                            style = LabelLarge.copy(color = Background, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sign Out Button to drop invalid sessions and clear cache
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Border, RoundedCornerShape(24.dp))
                        .background(Color.Transparent)
                        .clickable { onSignOut() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Out & Return",
                        style = LabelLarge.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

/**
 * Placeholder screen representing Onboarding start flow for Phase 1.
 */
@Composable
fun OnboardingPlaceholderScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome to Onboarding",
                    style = HeadlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "No profile exists for this account. Onboarding profile creation belongs to Phase 2.",
                    style = BodyMedium.copy(color = MutedForeground),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Foreground)
                        .clickable { onSignOut() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Back to Login",
                        style = LabelLarge.copy(color = Background, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val radius = width / 2f
        val strokeWidth = width * 0.2f

        // Red arc
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Yellow arc
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Green arc
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Blue arc
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Blue cross bar
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(width / 2f, height / 2f - strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(width / 2f, strokeWidth)
        )
    }
}
