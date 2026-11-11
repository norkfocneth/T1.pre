package com.example.t1.ui.tour

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics

@Composable
fun GuidedProductTourOverlay(
    userGoal: String = "PRODUCTIVITY",
    onStepSync: (TourStep) -> Unit,
    onCompleteTour: () -> Unit,
    onSkipTour: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = remember(userGoal) { TourStepCatalog.getSteps(userGoal) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = steps.getOrElse(currentStepIndex) { steps.first() }
    val view = LocalView.current
    val density = LocalDensity.current

    // Automatically synchronize navigation tab with current tour step
    LaunchedEffect(currentStepIndex) {
        onStepSync(currentStep)
    }

    // Smooth system back handling
    BackHandler {
        if (currentStepIndex > 0) {
            Haptics.playLight(view)
            currentStepIndex--
        } else {
            onSkipTour()
        }
    }

    // Animation progress for smooth entrance
    var stepAnimTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(currentStepIndex) {
        stepAnimTrigger++
    }

    val arrowProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 620, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "single_arrow_progress"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "callout_card_alpha"
    )

    // Breathing float motion for premium Apple-grade tactile feel
    val infiniteTransition = rememberInfiniteTransition(label = "tour_breathing")
    val breathingY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_y"
    )
    val spotlightGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spotlight_pulse"
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.58f))
    ) {
        // ══════════════════════════════════════════════════════════════════
        // 1. CANVAS: SPOTLIGHT RING & SINGLE CURVED NEON ARROW
        // ══════════════════════════════════════════════════════════════════
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // A. Draw Spotlight Target Ring
            currentStep.spotlight?.let { spot ->
                val targetCenter = Offset(spot.cxFraction * w, spot.cyFraction * h)
                val targetRadius = spot.radiusDp.dp.toPx()

                if (spot.isRect) {
                    val rectW = spot.widthFraction * w
                    val rectH = spot.heightFraction * h
                    val topLeft = Offset(targetCenter.x - rectW / 2, targetCenter.y - rectH / 2)

                    // Outer soft glow
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFA855F7).copy(alpha = 0.22f * spotlightGlowPulse),
                                Color.Transparent
                            ),
                            center = targetCenter,
                            radius = rectW * 0.7f
                        ),
                        topLeft = Offset(topLeft.x - 12.dp.toPx(), topLeft.y - 12.dp.toPx()),
                        size = Size(rectW + 24.dp.toPx(), rectH + 24.dp.toPx()),
                        cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
                    )
                    // High-tech glowing neon border
                    drawRoundRect(
                        color = Color(0xFFC084FC).copy(alpha = 0.80f),
                        topLeft = topLeft,
                        size = Size(rectW, rectH),
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                } else {
                    // Circular glowing target ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFA855F7).copy(alpha = 0.30f * spotlightGlowPulse),
                                Color.Transparent
                            ),
                            center = targetCenter,
                            radius = targetRadius * 1.35f
                        )
                    )
                    drawCircle(
                        color = Color(0xFFC084FC).copy(alpha = 0.85f),
                        center = targetCenter,
                        radius = targetRadius,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // B. Draw Single Animated Curved Neon Arrow
            currentStep.arrow?.let { arrow ->
                val sx = arrow.startXFraction * w
                val sy = arrow.startYFraction * h
                val cp1x = arrow.cp1XFraction * w
                val cp1y = arrow.cp1YFraction * h
                val cp2x = arrow.cp2XFraction * w
                val cp2y = arrow.cp2YFraction * h
                val ex = arrow.endXFraction * w
                val ey = arrow.endYFraction * h

                val fullPath = Path().apply {
                    moveTo(sx, sy)
                    cubicTo(cp1x, cp1y, cp2x, cp2y, ex, ey)
                }

                val androidPath = fullPath.asAndroidPath()
                val pathMeasure = android.graphics.PathMeasure(androidPath, false)
                val totalLength = pathMeasure.length

                if (totalLength > 0f) {
                    val segmentPath = android.graphics.Path()
                    val currentLen = totalLength * arrowProgress.coerceIn(0f, 1f)
                    pathMeasure.getSegment(0f, currentLen, segmentPath, true)

                    // Draw curved neon purple stroke
                    drawPath(
                        path = segmentPath.asComposePath(),
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFEDE9FE),
                                Color(0xFFC084FC),
                                Color(0xFFA855F7)
                            ),
                            start = Offset(sx, sy),
                            end = Offset(ex, ey)
                        ),
                        style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw crisp triangular arrowhead pointing at destination
                    if (arrowProgress > 0.82f) {
                        val arrowAlpha = ((arrowProgress - 0.82f) / 0.18f).coerceIn(0f, 1f)
                        val pos = FloatArray(2)
                        val tan = FloatArray(2)
                        pathMeasure.getPosTan(currentLen, pos, tan)
                        val angle = Math.toDegrees(Math.atan2(tan[1].toDouble(), tan[0].toDouble())).toFloat()

                        rotate(degrees = angle, pivot = Offset(pos[0], pos[1])) {
                            val headSize = 9.dp.toPx()
                            val arrowHead = Path().apply {
                                moveTo(pos[0], pos[1])
                                lineTo(pos[0] - headSize, pos[1] - headSize * 0.45f)
                                lineTo(pos[0] - headSize * 0.70f, pos[1])
                                lineTo(pos[0] - headSize, pos[1] + headSize * 0.45f)
                                close()
                            }
                            drawPath(
                                path = arrowHead,
                                color = Color(0xFFEDE9FE).copy(alpha = arrowAlpha)
                            )
                        }
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════
        // 2. TOP CONTROLS BAR: Skip (Left) | Category Indicator | Next (Right)
        // ══════════════════════════════════════════════════════════════════
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip Tour Button (Top Left)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
                    .clickable {
                        Haptics.playLight(view)
                        onSkipTour()
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Skip Tour",
                    style = LabelSmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    )
                )
            }

            // Step Indicator Pill (Top Center)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E1736).copy(alpha = 0.94f))
                    .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.40f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = currentStep.category,
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
                        text = "${currentStepIndex + 1} / ${steps.size}",
                        style = LabelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════
        // 3. STEP 10: INTERACTIVE BATTLE SIMULATION CARD (Tokens & Duration)
        // ══════════════════════════════════════════════════════════════════
        if (currentStep.isBattleInteractiveDemo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                    .offset(y = (-30).dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
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
                            width = 1.8.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFFC084FC),
                                    Color(0xFFA855F7),
                                    Color(0xFF7C3AED)
                                )
                            ),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚔️ 1v1 LIVE FOCUS DUEL",
                            style = LabelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC084FC),
                                letterSpacing = 1.5.sp,
                                fontSize = 11.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Opponent Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF7C3AED))
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "C",
                                    style = LabelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                            Column {
                                Text(
                                    text = "cypher.t1",
                                    style = LabelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Rank #2 • Focus Score 93 • 14 Wins",
                                    style = BodySmall.copy(color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Duration Selector (25 mins active)
                        Text(
                            text = "SELECT DUEL DURATION",
                            style = LabelSmall.copy(
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF7C3AED))
                                    .border(1.5.dp, Color.White, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⏱️ 25 Mins (Locked)",
                                    style = LabelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.06f))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⏱️ 50 Mins",
                                    style = LabelMedium.copy(color = Color.White.copy(alpha = 0.5f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Token Escrow Pot
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Wager: 20 T1 Tokens",
                                        style = LabelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Matched by opponent in escrow",
                                        style = BodySmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF59E0B).copy(alpha = 0.18f))
                                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "🪙 40 Pot",
                                        style = LabelSmall.copy(color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stasis Rules
                        Text(
                            text = "🛡️ Leaving T1 during battle for >15s forfeits the match and entire pot!",
                            style = BodySmall.copy(
                                color = Color(0xFFF87171),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════
        // 4. STEP 15: GRAND FINALE CARD (You're Ready!)
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
                        .padding(26.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🚀",
                            fontSize = 44.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "You're All Set!",
                            style = HeadlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "T1 is fully calibrated around your focus objective. Track your daily delta, win live focus fights, and climb to the top of the leaderboard.",
                            style = BodyMedium.copy(
                                color = Color.White.copy(alpha = 0.82f),
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
                                    onCompleteTour()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Get Started ➔",
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
        // 5. CALLOUT CARD (SINGLE FOCUSED EXPLANATION PER STEP)
        // ══════════════════════════════════════════════════════════════════
        if (!currentStep.isFinale) {
            val cardX = screenWidth * currentStep.cardXFraction
            val cardY = screenHeight * currentStep.cardYFraction
            val cardW = screenWidth * currentStep.cardWidthFraction

            Box(
                modifier = Modifier
                    .offset(x = cardX, y = cardY + breathingY.dp)
                    .width(cardW)
                    .graphicsLayer {
                        this.alpha = cardAlpha
                    }
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFFA855F7).copy(alpha = 0.5f),
                        spotColor = Color(0xFFC084FC).copy(alpha = 0.6f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF201838).copy(alpha = 0.96f),
                                Color(0xFF140F26).copy(alpha = 0.96f)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFC084FC),
                                Color(0xFFA855F7).copy(alpha = 0.5f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        // Tapping the card also smoothly advances to next step
                        Haptics.playMedium(view)
                        if (currentStepIndex < steps.size - 1) {
                            currentStepIndex++
                        } else {
                            onCompleteTour()
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = currentStep.iconEmoji,
                            fontSize = 18.sp
                        )
                        Text(
                            text = currentStep.title,
                            style = LabelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentStep.subtitle,
                        style = BodySmall.copy(
                            color = Color(0xFFE2E8F0).copy(alpha = 0.88f),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Tap Next ➔ to continue",
                            style = LabelSmall.copy(
                                color = Color(0xFFC084FC).copy(alpha = 0.85f),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
