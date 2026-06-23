package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.t1.ui.viewmodel.AuthUiState
import com.example.t1.ui.viewmodel.AuthViewModel

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

@Composable
fun AuthScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val view = LocalView.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            showDialog = false
            onContinue()
        }
    }

    // Entrance Animation States
    var showLogo by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showLegal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showLogo = true
        delay(500)
        showSubtitle = true
        delay(400)
        showButtons = true
        delay(600)
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
                    enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                        initialOffsetY = { -40 },
                        animationSpec = tween(800)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Logo text "T1." with gradient effect
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
                            enter = fadeIn(animationSpec = tween(600))
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

                // Buttons container
                AnimatedVisibility(
                    visible = showButtons,
                    enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                        initialOffsetY = { 60 },
                        animationSpec = tween(600)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Google button
                        val googleInteraction = remember { MutableInteractionSource() }
                        val isGooglePressed by googleInteraction.collectIsPressedAsState()
                        val googleScale = if (isGooglePressed) 0.95f else 1f

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
                                    Toast.makeText(context, "Google Sign-In is currently disabled. Please use Email Sign-In.", Toast.LENGTH_LONG).show()
                                }
                                .drawBehind {
                                    // Custom glow shadow around button
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

                        // Email button
                        val emailInteraction = remember { MutableInteractionSource() }
                        val isEmailPressed by emailInteraction.collectIsPressedAsState()
                        val emailScale = if (isEmailPressed) 0.95f else 1f

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .scale(emailScale)
                                .clip(RoundedCornerShape(28.dp))
                                .border(1.dp, Border, RoundedCornerShape(28.dp))
                                .background(Color.Transparent)
                                .clickable(
                                    interactionSource = emailInteraction,
                                    indication = null
                                ) {
                                    Haptics.playMedium(view)
                                    showDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = Foreground,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Email",
                                    style = LabelLarge.copy(
                                        color = Foreground,
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
                enter = fadeIn(animationSpec = tween(800)),
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

    if (showDialog) {
        Dialog(onDismissRequest = {
            showDialog = false
            viewModel.resetState()
        }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Border, RoundedCornerShape(24.dp))
                    .background(Card)
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isSignUp) "CREATE ACCOUNT" else "SIGN IN",
                        style = TrackingWide.copy(
                            color = MutedForeground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Tab selector (Sign in / Sign up)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Secondary)
                            .border(1.dp, Border, RoundedCornerShape(20.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (!isSignUp) Foreground else Color.Transparent)
                                .clickable { isSignUp = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign In",
                                style = LabelMedium.copy(
                                    color = if (!isSignUp) Background else Foreground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSignUp) Foreground else Color.Transparent)
                                .clickable { isSignUp = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign Up",
                                style = LabelMedium.copy(
                                    color = if (isSignUp) Background else Foreground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Email Field
                    Text(
                        text = "EMAIL",
                        style = LabelSmall.copy(color = MutedForeground, fontSize = 9.sp),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BasicTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        textStyle = BodyMedium.copy(color = Foreground),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        cursorBrush = SolidColor(Foreground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .background(Background.copy(alpha = 0.5f))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Password Field
                    Text(
                        text = "PASSWORD",
                        style = LabelSmall.copy(color = MutedForeground, fontSize = 9.sp),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BasicTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        textStyle = BodyMedium.copy(color = Foreground),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        cursorBrush = SolidColor(Foreground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .background(Background.copy(alpha = 0.5f))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Error message
                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            style = BodySmall.copy(color = Destructive),
                            modifier = Modifier.padding(bottom = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // CTA Button
                    val isBtnEnabled = emailInput.trim().isNotEmpty() && passwordInput.length >= 6
                    val btnAlpha = if (isBtnEnabled && uiState !is AuthUiState.Loading) 1f else 0.5f
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .alpha(btnAlpha)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Foreground)
                            .clickable(enabled = isBtnEnabled && uiState !is AuthUiState.Loading) {
                                Haptics.playMedium(view)
                                if (isSignUp) {
                                    viewModel.signUp(emailInput, passwordInput)
                                } else {
                                    viewModel.signIn(emailInput, passwordInput)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(color = Background, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (isSignUp) "Create Account" else "Sign In",
                                style = LabelLarge.copy(color = Background, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
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
        // Draw Google G icon
        // We will just draw a nice colored representation since the SVG is complex,
        // but wait, we can also approximate or draw it using arc/lines! Let's do that.
        // Actually, a simple multi-colored Arc is standard and clean. Let's do a beautiful approximation.
        val radius = width / 2f
        val strokeWidth = width * 0.2f
        // Draw the multicolored arcs
        // Red sector
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Yellow sector
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Green sector
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Blue sector and center bar
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Draw horizontal line for "G"
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(width / 2f, height / 2f - strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(width / 2f, strokeWidth)
        )
    }
}
