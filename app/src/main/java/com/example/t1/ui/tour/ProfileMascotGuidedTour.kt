package com.example.t1.ui.tour

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.R
import com.example.t1.theme.*
import com.example.t1.util.Haptics

data class ProfileTourStep(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val mascotRes: Int,
    val mascotAlignLeft: Boolean = false,
    val cardAtTop: Boolean = false,
    val isFinale: Boolean = false
)

val ProfileTourSteps = listOf(
    // Step 0: Avatar & Edit Pencil
    ProfileTourStep(
        id = 0,
        title = "YOUR PROFILE & IDENTITY",
        subtitle = "Yeh aapki T1 identity hai. Edit pencil icon par tap karke apna display name aur personalized avatar kabhi bhi update karein.",
        iconEmoji = "👤",
        mascotRes = R.drawable.mascot_tour_sitting,
        mascotAlignLeft = true,
        cardAtTop = false
    ),
    // Step 1: Settings & Preferences
    ProfileTourStep(
        id = 1,
        title = "SETTINGS & PREFERENCES",
        subtitle = "Manage your account, focus reminders, notifications, Ghost Mode privacy aur exam preferences from here.",
        iconEmoji = "⚙️",
        mascotRes = R.drawable.mascot_live_improve,
        mascotAlignLeft = false,
        cardAtTop = false
    ),
    // Step 2: Focus Title Badge
    ProfileTourStep(
        id = 2,
        title = "YOUR FOCUS TITLE",
        subtitle = "A tag that reflects your focus style and journey. Edit par tap karke apna title customize karein (jaise 'Deep Work Enthusiast').",
        iconEmoji = "✨",
        mascotRes = R.drawable.mascot_modal_productivity,
        mascotAlignLeft = true,
        cardAtTop = false
    ),
    // Step 3: Focus Activity
    ProfileTourStep(
        id = 3,
        title = "YOUR FOCUS ACTIVITY",
        subtitle = "Total time you stayed focused aur completed focus sessions ka record yahan real-time track hota hai.",
        iconEmoji = "⚡",
        mascotRes = R.drawable.mascot_live_analytics,
        mascotAlignLeft = false,
        cardAtTop = true
    ),
    // Step 4: Weekly Trend
    ProfileTourStep(
        id = 4,
        title = "WEEKLY TREND",
        subtitle = "Aapka Focus Score progress over the last 7 days. Higher bars aur rising trend ka matlab hai better focus and habits.",
        iconEmoji = "📊",
        mascotRes = R.drawable.mascot_goal_pointer,
        mascotAlignLeft = true,
        cardAtTop = true
    ),
    // Step 5: Achievements & Badges
    ProfileTourStep(
        id = 5,
        title = "ACHIEVEMENTS & BADGES",
        subtitle = "Unlock streaks, milestones aur special ranks jaise '7 Day Streak' aur 'Top Performer' as you stay consistent.",
        iconEmoji = "🏆",
        mascotRes = R.drawable.mascot_modal_social,
        mascotAlignLeft = false,
        cardAtTop = true
    ),
    // Step 6: Grand Finale
    ProfileTourStep(
        id = 6,
        title = "ALL SET & READY TO BUILD!",
        subtitle = "Congratulations! Aapka complete T1 tour finish ho chuka hai. Apni daily consistency track karein, 1v1 fights jeetein aur top rank achieve karein!",
        iconEmoji = "🎉",
        mascotRes = R.drawable.mascot_live_hero,
        mascotAlignLeft = false,
        cardAtTop = false,
        isFinale = true
    )
)

@Composable
fun ProfileMascotGuidedTour(
    currentStepIndex: Int,
    targetScreenOffset: Offset,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val currentStep = ProfileTourSteps.getOrElse(currentStepIndex) { ProfileTourSteps.first() }

    BackHandler {
        onSkip()
    }

    // Dynamic dialogue card measurement for anchor points
    var cardTopY by remember { mutableFloatStateOf(0f) }
    var cardBottomY by remember { mutableFloatStateOf(0f) }
    var cardCenterX by remember { mutableFloatStateOf(0f) }

    // Smooth entrance draw animation for the curved arrow
    var animTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(currentStepIndex) {
        animTrigger++
    }

    val arrowProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "profile_arrow_draw"
    )

    // Breathing float animation for Mascot
    val infiniteTransition = rememberInfiniteTransition(label = "profile_mascot_tour_anim")
    val mascotBobY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "profile_mascot_bob"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
    ) {
        // ══════════════════════════════════════════════════════════════════
        // 1. CANVAS: PRECISE CURVED NEON ARROW TO REAL ELEMENT
        // ══════════════════════════════════════════════════════════════════
        if (!currentStep.isFinale && targetScreenOffset != Offset.Zero) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Anchor arrow start to the dialogue card edge
                val startX = if (cardCenterX > 0f) cardCenterX else (if (currentStep.mascotAlignLeft) w * 0.60f else w * 0.40f)
                val startY = if (currentStep.cardAtTop) {
                    if (cardBottomY > 0f) cardBottomY else (h * 0.33f)
                } else {
                    if (cardTopY > 0f) cardTopY else (h * 0.70f)
                }

                val targetX = targetScreenOffset.x.coerceIn(24f, w - 24f)
                val targetY = targetScreenOffset.y.coerceIn(40f, h - 40f)

                // Smooth natural Bézier arc
                val fullPath = Path().apply {
                    moveTo(startX, startY)
                    val arcSpread = if (currentStep.mascotAlignLeft) 35.dp.toPx() else -35.dp.toPx()
                    val cp1 = Offset(startX + arcSpread, startY + (targetY - startY) * 0.35f)
                    val cp2 = Offset(targetX + arcSpread * 0.35f, startY + (targetY - startY) * 0.75f)
                    cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, targetX, targetY)
                }

                val androidPath = fullPath.asAndroidPath()
                val pathMeasure = android.graphics.PathMeasure(androidPath, false)
                val totalLength = pathMeasure.length

                if (totalLength > 0f) {
                    val segmentPath = android.graphics.Path()
                    val currentLen = totalLength * arrowProgress.coerceIn(0f, 1f)
                    pathMeasure.getSegment(0f, currentLen, segmentPath, true)

                    // Outer neon purple ambient glow
                    drawPath(
                        path = segmentPath.asComposePath(),
                        color = Color(0xFFA855F7).copy(alpha = 0.38f * arrowProgress),
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Sharp inner gradient stroke
                    drawPath(
                        path = segmentPath.asComposePath(),
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFEDE9FE),
                                Color(0xFFC084FC),
                                Color(0xFFA855F7)
                            ),
                            start = Offset(startX, startY),
                            end = Offset(targetX, targetY)
                        ),
                        style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw sharp triangular arrowhead pointing directly at the target element
                    if (arrowProgress > 0.70f) {
                        val arrowAlpha = ((arrowProgress - 0.70f) / 0.30f).coerceIn(0f, 1f)
                        val pos = FloatArray(2)
                        val tan = FloatArray(2)
                        pathMeasure.getPosTan(currentLen, pos, tan)
                        val angle = Math.toDegrees(Math.atan2(tan[1].toDouble(), tan[0].toDouble())).toFloat()

                        rotate(degrees = angle, pivot = Offset(pos[0], pos[1])) {
                            val headSize = 12.dp.toPx()
                            val arrowHead = Path().apply {
                                moveTo(pos[0], pos[1])
                                lineTo(pos[0] - headSize, pos[1] - headSize * 0.45f)
                                lineTo(pos[0] - headSize * 0.65f, pos[1])
                                lineTo(pos[0] - headSize, pos[1] + headSize * 0.45f)
                                close()
                            }
                            drawPath(
                                path = arrowHead,
                                color = Color(0xFFEDE9FE).copy(alpha = arrowAlpha)
                            )
                        }

                        // Subtle glowing beacon dot at destination
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFEDE9FE).copy(alpha = 0.90f * arrowAlpha),
                                    Color(0xFFA855F7).copy(alpha = 0.40f * arrowAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(targetX, targetY),
                                radius = 7.dp.toPx()
                            ),
                            center = Offset(targetX, targetY),
                            radius = 7.dp.toPx()
                        )
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════
        // 2. TOP CONTROLS BAR: Skip Tour (Left) | PROFILE • Step X of 7 (Center)
        // ══════════════════════════════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            // Skip Tour Button (Top Left)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                    .clickable {
                        Haptics.playLight(view)
                        onSkip()
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Skip Tour",
                    style = LabelSmall.copy(
                        color = Color.White.copy(alpha = 0.90f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    )
                )
            }

            // Step Indicator Pill (Top Center)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E1736).copy(alpha = 0.95f))
                    .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "PROFILE",
                        style = LabelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC084FC)
                        )
                    )
                    Text(
                        text = "•",
                        style = LabelSmall.copy(color = Color.White.copy(alpha = 0.4f))
                    )
                    Text(
                        text = "${currentStepIndex + 1} of ${ProfileTourSteps.size}",
                        style = LabelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════
        // 3. STEP 6: GRAND FINALE CARD (Celebration Welcome)
        // ══════════════════════════════════════════════════════════════════
        if (currentStep.isFinale) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF221A38),
                                    Color(0xFF161124),
                                    Color(0xFF0F0C1B)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFFEDE9FE),
                                    Color(0xFFC084FC),
                                    Color(0xFFA855F7)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = currentStep.mascotRes),
                            contentDescription = "Mascot Finale",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(126.dp)
                                .offset(y = mascotBobY.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Profile is Ready!",
                            style = HeadlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 23.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentStep.subtitle,
                            style = BodyMedium.copy(
                                color = Color.White.copy(alpha = 0.88f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFFEDE9FE),
                                            Color(0xFFDDD6FE),
                                            Color(0xFFC4B5FD)
                                        )
                                    )
                                )
                                .clickable {
                                    Haptics.playHeavy(view)
                                    onNext()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Start Using T1 ➔",
                                style = LabelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1A102E)
                                )
                            )
                        }
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════
        // 4. MASCOT & SPEECH BUBBLE DIALOGUE CARD (STEPS 0 to 5)
        // ══════════════════════════════════════════════════════════════════
        if (!currentStep.isFinale) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                contentAlignment = if (currentStep.cardAtTop) Alignment.TopCenter else Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            top = if (currentStep.cardAtTop) 72.dp else 0.dp,
                            bottom = if (!currentStep.cardAtTop) 125.dp else 0.dp
                        ),
                    horizontalAlignment = if (currentStep.mascotAlignLeft) Alignment.Start else Alignment.End
                ) {
                    // Mascot Figure
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 14.dp)
                            .offset(y = mascotBobY.dp)
                    ) {
                        Image(
                            painter = painterResource(id = currentStep.mascotRes),
                            contentDescription = "T1 Profile Mascot Guide",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(110.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Speech Dialogue Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInRoot()
                                cardTopY = pos.y
                                cardBottomY = pos.y + coords.size.height
                                cardCenterX = pos.x + coords.size.width / 2f
                            }
                            .shadow(
                                elevation = 14.dp,
                                shape = RoundedCornerShape(22.dp),
                                ambientColor = Color(0xFFA855F7).copy(alpha = 0.5f),
                                spotColor = Color(0xFFC084FC).copy(alpha = 0.6f)
                            )
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF211A38).copy(alpha = 0.96f),
                                        Color(0xFF140F26).copy(alpha = 0.96f)
                                    )
                                )
                            )
                            .border(
                                width = 1.6.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFEDE9FE),
                                        Color(0xFFC084FC),
                                        Color(0xFFA855F7).copy(alpha = 0.4f)
                                    )
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .clickable {
                                Haptics.playMedium(view)
                                onNext()
                            }
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = currentStep.iconEmoji,
                                    fontSize = 19.sp
                                )
                                Text(
                                    text = currentStep.title,
                                    style = LabelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = currentStep.subtitle,
                                style = BodySmall.copy(
                                    color = Color(0xFFE2E8F0).copy(alpha = 0.88f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.5.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Progress Dots
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (0 until ProfileTourSteps.size - 1).forEach { idx ->
                                        Box(
                                            modifier = Modifier
                                                .size(if (idx == currentStepIndex) 7.dp else 4.5.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (idx == currentStepIndex) Color(0xFFC084FC)
                                                    else Color.White.copy(alpha = 0.22f)
                                                )
                                        )
                                    }
                                }

                                // Interactive Next Button right inside the card!
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0xFFEDE9FE),
                                                    Color(0xFFDDD6FE),
                                                    Color(0xFFC4B5FD)
                                                )
                                            )
                                        )
                                        .clickable {
                                            Haptics.playMedium(view)
                                            onNext()
                                        }
                                        .padding(horizontal = 18.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Text(
                                            text = if (currentStepIndex == ProfileTourSteps.size - 2) "Finish" else "Next",
                                            style = LabelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = Color(0xFF1A102E)
                                            )
                                        )
                                        Text(
                                            text = "➔",
                                            style = LabelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = Color(0xFF1A102E)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

