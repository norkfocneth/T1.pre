package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.R
import com.example.t1.util.Haptics

data class FirstQuestionOption(
    val id: Int,
    val text: String,
    val icon: ImageVector
)

val firstQuestionOptions = listOf(
    FirstQuestionOption(0, "I’d get more work done.", Icons.Default.Laptop),
    FirstQuestionOption(1, "I’d spend more time on myself.", Icons.Default.FavoriteBorder),
    FirstQuestionOption(2, "I’d be more present with people.", Icons.Default.PeopleOutline),
    FirstQuestionOption(3, "I’d sleep and wake up better.", Icons.Default.Bedtime),
    FirstQuestionOption(4, "I’d finally feel in control.", Icons.Default.WorkspacePremium)
)

@Composable
fun FirstOnboardingQuestionScreen(
    onOptionSelected: (selectedIndex: Int) -> Unit,
    onBack: () -> Unit = {},
    onSkip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current
    var selectedOptionId by remember { mutableIntStateOf(0) } // Default: 0 ("I'd get more work done")

    val bgDark = Color(0xFF090710)
    val cardBg = Color(0xFF151022)
    val cardBorder = Color(0xFF382952)
    val purplePrimary = Color(0xFF9333EA)
    val purpleGlow = Color(0xFFC084FC)
    val lilacAccent = Color(0xFFD8B4FE)
    val textMuted = Color(0xFFA59AB8)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── 1. TOP BRAND & PROGRESS HEADER ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // T1. Logo (Start)
                Text(
                    text = "T1.",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontSize = 24.sp,
                    letterSpacing = (-0.5).sp
                )

                // 5-Segment Progress Bar (Center)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until 5) {
                            val isActive = i == 0 // Screen 1 active
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isActive) {
                                            Brush.horizontalGradient(
                                                listOf(purplePrimary, purpleGlow)
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color.White.copy(alpha = 0.18f),
                                                    Color.White.copy(alpha = 0.12f)
                                                )
                                            )
                                        }
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "01 / 05",
                        color = textMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Skip Button (End)
                Text(
                    text = "Skip",
                    color = textMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            Haptics.playLight(view)
                            onSkip()
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            // ─── 2. HERO ILLUSTRATION (Moon + Mountain + Mascot + Speech Bubble) ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .heightIn(min = 210.dp, max = 250.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_onboarding_mountain),
                    contentDescription = "T1 Mascot on Mountain with Speech Bubble",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.FillWidth
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ─── 3. QUESTION CARD CONTAINER ───
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(26.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = cardBg.copy(alpha = 0.92f)
                ),
                border = BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(cardBorder.copy(alpha = 0.7f), Color(0xFF221935).copy(alpha = 0.4f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card Subtitle / Instruction
                    Text(
                        text = "Pick the one that feels most valuable to you.",
                        color = textMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5 Selectable Options
                    firstQuestionOptions.forEach { option ->
                        val isSelected = selectedOptionId == option.id
                        val itemInteraction = remember { MutableInteractionSource() }
                        val isPressed by itemInteraction.collectIsPressedAsState()
                        val animatedScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.97f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
                            label = "optionScale"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .scale(animatedScale)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF2D1B4D).copy(alpha = 0.95f),
                                                Color(0xFF201338).copy(alpha = 0.95f)
                                            )
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF1B142A).copy(alpha = 0.6f),
                                                Color(0xFF161022).copy(alpha = 0.6f)
                                            )
                                        )
                                    }
                                )
                                .border(
                                    border = if (isSelected) {
                                        BorderStroke(
                                            1.5.dp,
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF8B5CF6), Color(0xFFC084FC), Color(0xFFE9D5FF))
                                            )
                                        )
                                    } else {
                                        BorderStroke(1.dp, Color(0xFF2E2242))
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable(
                                    interactionSource = itemInteraction,
                                    indication = null,
                                    onClick = {
                                        Haptics.playLight(view)
                                        selectedOptionId = option.id
                                    }
                                )
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Option Icon Box
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) Color(0xFF3B2464) else Color(0xFF251B38)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else lilacAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Option Label
                            Text(
                                text = option.text,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )

                            // Radio Indicator
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFFC084FC)))
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, Color(0xFF4C3B65), CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ─── 4. BOTTOM ACTION BAR ((←) + Finish Setup ✨) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circular Back Button (←)
                val backInteraction = remember { MutableInteractionSource() }
                val isBackPressed by backInteraction.collectIsPressedAsState()
                val backScale by animateFloatAsState(
                    targetValue = if (isBackPressed) 0.9f else 1f,
                    label = "backScale"
                )

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .scale(backScale)
                        .clip(CircleShape)
                        .background(Color(0xFF1B142B))
                        .border(1.dp, Color(0xFF3E2D58), CircleShape)
                        .clickable(
                            interactionSource = backInteraction,
                            indication = null,
                            onClick = {
                                Haptics.playLight(view)
                                onBack()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Primary CTA: "Finish Setup ✨" Glowing Pill Button
                val ctaInteraction = remember { MutableInteractionSource() }
                val isCtaPressed by ctaInteraction.collectIsPressedAsState()
                val ctaScale by animateFloatAsState(
                    targetValue = if (isCtaPressed) 0.96f else 1f,
                    label = "ctaScale"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .scale(ctaScale)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = purplePrimary,
                            spotColor = purpleGlow
                        )
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF7C3AED),
                                    Color(0xFF9333EA),
                                    Color(0xFFC084FC),
                                    Color(0xFFE9D5FF)
                                )
                            )
                        )
                        .clickable(
                            interactionSource = ctaInteraction,
                            indication = null,
                            onClick = {
                                Haptics.playSuccess(context)
                                onOptionSelected(selectedOptionId)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Finish Setup",
                            color = Color(0xFF0F0C1B),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 0.3.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "✨",
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ─── 5. AMBIENT BOTTOM PURPLE WAVE ───
            Image(
                painter = painterResource(id = R.drawable.ambient_purple_wave),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp),
                contentScale = ContentScale.FillWidth,
                alpha = 0.75f
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
