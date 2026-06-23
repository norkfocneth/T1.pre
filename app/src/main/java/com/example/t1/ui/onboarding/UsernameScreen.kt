package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

val TAKEN_USERNAMES = listOf(
    "sensai", "focus", "tanaka", "erikson", "elena", "marcus", "miller", "chen",
    "ninja", "pro", "legend", "alpha", "beast", "king", "queen", "master"
)

enum class UsernameStatus {
    IDLE, CHECKING, AVAILABLE, TAKEN
}

@Composable
fun UsernameScreen(
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    var input by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(UsernameStatus.IDLE) }
    var suggestions by remember { mutableStateOf(listOf<String>()) }

    // Alphanumeric + Underscores filtering & Max 16 length
    val onInputChange: (String) -> Unit = { newValue ->
        val filtered = newValue.replace(Regex("[^a-zA-Z0-9_]"), "")
        if (filtered.length <= 16) {
            input = filtered
        }
    }

    // Debounce availability checker (600ms)
    LaunchedEffect(input) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            status = UsernameStatus.IDLE
            suggestions = emptyList()
            return@LaunchedEffect
        }

        status = UsernameStatus.CHECKING
        delay(600) // Debounce delay

        val lowercaseUsername = trimmed.lowercase()
        val isTaken = TAKEN_USERNAMES.contains(lowercaseUsername)
        if (isTaken) {
            status = UsernameStatus.TAKEN
            suggestions = listOf("_x", "2k", "_pro", ".go", "_hq").map { "$lowercaseUsername$it" }.take(3)
        } else {
            status = UsernameStatus.AVAILABLE
            suggestions = emptyList()
        }
    }

    val canContinue = status == UsernameStatus.AVAILABLE && input.trim().length >= 2

    // Border color depending on status
    val borderColor by animateColorAsState(
        targetValue = when (status) {
            UsernameStatus.IDLE -> Border
            UsernameStatus.CHECKING -> MutedForeground
            UsernameStatus.AVAILABLE -> GlowGreen
            UsernameStatus.TAKEN -> Destructive
        },
        animationSpec = tween(300),
        label = "borderColor"
    )

    val shadowGlowColor = when (status) {
        UsernameStatus.AVAILABLE -> GlowGreen.copy(alpha = 0.15f)
        UsernameStatus.TAKEN -> Destructive.copy(alpha = 0.15f)
        else -> Color.Transparent
    }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Suffix description
            Text(
                text = "CHOOSE YOUR IDENTITY",
                style = TrackingWide.copy(
                    color = MutedForeground,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Screen title
            Text(
                text = "Pick a username",
                style = HeadlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(
                        colors = listOf(GradientTextStart, GradientTextEnd)
                    )
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtitle
            Text(
                text = "This is how others will see you on the leaderboard",
                style = BodyMedium.copy(color = MutedForeground),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Input Container Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                    .background(Card)
                    .drawBehind {
                        // Apply glow shadow
                        if (shadowGlowColor != Color.Transparent) {
                            drawRect(
                                color = shadowGlowColor,
                                size = size
                            )
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Styled BasicTextField
                    BasicTextField(
                        value = input,
                        onValueChange = onInputChange,
                        textStyle = TextStyle(
                            fontFamily = SpaceGrotesk,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Foreground
                        ),
                        cursorBrush = SolidColor(Foreground),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box {
                                if (input.isEmpty()) {
                                    Text(
                                        text = "username",
                                        style = TextStyle(
                                            fontFamily = SpaceGrotesk,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MutedForeground.copy(alpha = 0.4f)
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    // .t1 suffix label
                    Text(
                        text = ".t1",
                        style = TextStyle(
                            fontFamily = SpaceGrotesk,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MutedForeground
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status icon box (Checking indicator / Check / X)
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (status) {
                            UsernameStatus.CHECKING -> {
                                CircularProgressIndicator(
                                    color = MutedForeground,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            UsernameStatus.AVAILABLE -> {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Available",
                                    tint = GlowGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            UsernameStatus.TAKEN -> {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Taken",
                                    tint = Destructive,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }

            // Live preview
            if (input.trim().isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Preview: ",
                        style = BodySmall.copy(
                            fontSize = 10.sp,
                            color = MutedForeground
                        )
                    )
                    Text(
                        text = "${input.lowercase()}.t1",
                        style = BodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Foreground
                        )
                    )
                }
            }

            // Status message
            Box(
                modifier = Modifier.height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (status) {
                    UsernameStatus.AVAILABLE -> {
                        Text(
                            text = "✓ ${input.lowercase()}.t1 is available!",
                            style = BodySmall.copy(
                                color = GlowGreen,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    UsernameStatus.TAKEN -> {
                        Text(
                            text = "✗ Username already taken",
                            style = BodySmall.copy(
                                color = Destructive,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    UsernameStatus.CHECKING -> {
                        Text(
                            text = "Checking availability...",
                            style = BodySmall.copy(
                                color = MutedForeground
                            )
                        )
                    }
                    else -> {}
                }
            }

            // Suggestions when taken
            AnimatedVisibility(
                visible = status == UsernameStatus.TAKEN && suggestions.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Suggestions",
                            tint = MutedForeground,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SUGGESTIONS",
                            style = TrackingMedium.copy(
                                color = MutedForeground,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        suggestions.forEach { suggestion ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Secondary)
                                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                                    .clickable {
                                        Haptics.playLight(view)
                                        input = suggestion
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$suggestion.t1",
                                    style = BodySmall.copy(
                                        color = Foreground,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue CTA
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale = if (isPressed && canContinue) 0.97f else 1f

            val buttonAlpha = if (canContinue) 1f else 0.3f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(buttonScale)
                    .alpha(buttonAlpha)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Foreground)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = canContinue
                    ) {
                        Haptics.playMedium(view)
                        onComplete("${input.trim().lowercase()}.t1")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continue",
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
