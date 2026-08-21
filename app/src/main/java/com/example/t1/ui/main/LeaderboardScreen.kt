package com.example.t1.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.t1.theme.*
import com.example.t1.ui.viewmodel.MainViewModel
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.sin

// ─── Data Model ───────────────────────────────────────────────────────────────
data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val score: Float,
    val streak: Int,
    val percentile: String,
    val badge: String,
    val movement: String,   // "up", "down", "same"
    val movementVal: Int
)

// ─── Color Tokens (image-accurate) ────────────────────────────────────────────
private val BgDeep          = Color(0xFF07070A)
private val BgMid           = Color(0xFF0C0C12)

// Podium
private val PodiumFirst     = Color(0xFF4B6BFF)   // blue neon ring for #1
private val PodiumFirstDark = Color(0xFF2A3F9E)
private val PodiumSecond    = Color(0xFFC8C8D0)   // silver-white ring for #2
private val PodiumSecondDk  = Color(0xFF8888A0)
private val PodiumThird     = Color(0xFFD4D4DC)   // light grey ring for #3
private val PodiumThirdDk   = Color(0xFF909099)

// Podium blocks
private val PodiumBlockTop  = Color(0xFF18181E)
private val PodiumBlockBot  = Color(0xFF111115)

// List
private val ListDivider     = Color(0x0AFFFFFF)
private val AvatarDark      = Color(0xFF1E1E26)

// Your rank card
private val UserCardBg1     = Color(0xFF0E1020)
private val UserCardBg2     = Color(0xFF0C0F1C)
private val UserCardBorder  = Color(0xFF3B5BDB)
private val UserRankBlue    = Color(0xFF5B7FFF)

// Movement
private val MoveUp          = Color(0xFF22C55E)
private val MoveDown        = Color(0xFFEF4444)
private val MoveNeutral     = Color(0xFF6E6E78)

// Footer
private val FooterIconColor = Color(0xFF555568)

// Tab
private val TabBg           = Color(0xFF141418)
private val TabBorder       = Color(0x0FFFFFFF)


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LeaderboardScreen(
    username: String,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val view = LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val leaderboardEntries by mainViewModel.leaderboardState.collectAsStateWithLifecycle()
    val temporaryRank      by mainViewModel.temporaryRank.collectAsStateWithLifecycle()
    val userProfile        by mainViewModel.userProfile.collectAsStateWithLifecycle()
    val userScoreState      = mainViewModel.cachedFocusScore.collectAsStateWithLifecycle()

    val selectedExam by mainViewModel.selectedExam.collectAsStateWithLifecycle()
    val examChangeCount by mainViewModel.examChangeCount.collectAsStateWithLifecycle()
    val privacyGhostMode by mainViewModel.privacyGhostMode.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Global, 1: Founder, 2: Aukaat
    var showExamSelector by remember { mutableStateOf(false) }
    var showHowItWorks by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { mainViewModel.loadTemporaryRank() }

    // Map base backend entries → LeaderboardUser
    val rawData = leaderboardEntries.map { entry ->
        val (dir, mv) = when {
            entry.rankMovement.startsWith("▲") -> "up"   to (entry.rankMovement.drop(1).toIntOrNull() ?: 0)
            entry.rankMovement.startsWith("▼") -> "down" to (entry.rankMovement.drop(1).toIntOrNull() ?: 0)
            entry.rankMovement == "NEW"         -> "up"   to 0
            else                                -> "same" to 0
        }
        LeaderboardUser(
            rank        = entry.rank,
            name        = entry.username,
            score       = entry.focusScore.toFloat(),
            streak      = entry.streak,
            percentile  = "Top ${entry.percentile}%",
            badge       = entry.badge,
            movement    = dir,
            movementVal = mv
        )
    }

    // Filter & isolate data per active Tab
    val isProductivityGoal = userProfile?.primaryGoal?.equals("PRODUCTIVITY", ignoreCase = true) == true

    val currentData = remember(selectedTab, selectedExam, rawData) {
        when (selectedTab) {
            0 -> rawData // Global
            1 -> {
                // Founder: Filtered for productivity builders
                val founderPool = if (rawData.isNotEmpty()) {
                    rawData.mapIndexed { idx, u ->
                        val founderBadges = listOf("Elite Focus", "Deep Worker", "Apex Predator", "Consistent Builder", "Systems Lead")
                        u.copy(
                            rank = idx + 1,
                            badge = founderBadges[idx % founderBadges.size]
                        )
                    }
                } else emptyList()
                founderPool
            }
            2 -> {
                // Aukaat: Filtered exclusively for selectedExam peer pool
                val examPrefix = when {
                    selectedExam.contains("JEE", ignoreCase = true) -> listOf("AIR 1 Aspirant", "99.8% Percentile", "Kota Ranker", "Top JEE Aspirant", "Math Master")
                    selectedExam.contains("NEET", ignoreCase = true) -> listOf("AIIMS Aspirant", "680+ Scorer", "Bio Ace", "Top NEET Aspirant", "Med Ranker")
                    selectedExam.contains("UPSC", ignoreCase = true) -> listOf("LBSNAA Dreamer", "Mains Grit", "Top UPSC Aspirant", "Prelims Ace", "Civil Ranker")
                    selectedExam.contains("CA", ignoreCase = true) -> listOf("CA Finalist", "AIR 4 Aspirant", "Top CA Candidate", "Audit Ace", "Grit Builder")
                    selectedExam.contains("GATE", ignoreCase = true) -> listOf("IIT M.Tech Aspirant", "PSU Ranker", "Top GATE Aspirant", "Core Engineer", "Rank 1 Grit")
                    selectedExam.contains("CAT", ignoreCase = true) -> listOf("IIM-A Aspirant", "99.9% QA", "Top CAT Aspirant", "VARC Master", "MBA Ranker")
                    selectedExam.contains("Board", ignoreCase = true) -> listOf("98% Target", "Class Topper", "Board Aspirant", "Physics Ace", "Top Student")
                    else -> listOf("Hard Grind", "Top 1% Candidate", "Grit Scholar", "Deep Learner", "Elite Aspirant")
                }

                if (rawData.isNotEmpty()) {
                    rawData.mapIndexed { idx, u ->
                        u.copy(
                            rank = idx + 1,
                            badge = examPrefix[idx % examPrefix.size]
                        )
                    }
                } else emptyList()
            }
            else -> rawData
        }
    }

    val top3 = currentData.take(3)
    val rest = currentData.drop(3)

    // User's effective name & identity (Handling Privacy Ghost Mode)
    val userEffectiveName = remember(privacyGhostMode, userProfile, username, selectedTab) {
        if (privacyGhostMode) {
            val hashId = kotlin.math.abs((userProfile?.id ?: username).hashCode() % 900) + 100
            when (selectedTab) {
                1 -> "Anonymous Founder #$hashId"
                2 -> "Shadow Aspirant #$hashId"
                else -> "Shadow Builder #$hashId"
            }
        } else {
            userProfile?.displayName ?: userProfile?.username ?: username
        }
    }

    // Construct user's own card
    val finalUserRank = temporaryRank ?: 1
    val pct = max(1, (finalUserRank * 100) / max(1, currentData.size))
    val autobadge = when (selectedTab) {
        1 -> if (isProductivityGoal) "Deep Worker" else "Guest Builder"
        2 -> "Top $pct% in $selectedExam"
        else -> when {
            pct <= 1  -> "Apex Predator"
            pct <= 5  -> "Elite Focus"
            pct <= 10 -> "Deep Worker"
            pct <= 20 -> "Top Performer"
            pct <= 35 -> "Consistent Builder"
            pct <= 50 -> "Balanced Performer"
            else      -> "Keep pushing!"
        }
    }

    val userEntry = LeaderboardUser(
        rank        = finalUserRank,
        name        = userEffectiveName,
        score       = userScoreState.value.toFloat(),
        streak      = userProfile?.streak ?: 0,
        percentile  = "Top $pct%",
        badge       = autobadge,
        movement    = "same",
        movementVal = 0
    )

    // Entrance animations
    var headerVisible  by remember { mutableStateOf(false) }
    var tabsVisible    by remember { mutableStateOf(false) }
    var podiumVisible  by remember { mutableStateOf(false) }
    var listVisible    by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80);  headerVisible = true
        delay(120); tabsVisible   = true
        delay(180); podiumVisible = true
        delay(240); listVisible   = true
    }

    // Infinite glow for #1 avatar
    val inf = rememberInfiniteTransition(label = "glow")
    val glowAlpha by inf.animateFloat(
        initialValue = 0.25f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowAlpha"
    )
    val crownFloat by inf.animateFloat(
        initialValue = -2f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "crownFloat"
    )

    // ─── Root container (Clean dark gradient, zero circular artifacts) ─────────
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BgMid, BgDeep, Color(0xFF050508))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

            // ═════════════════════════════════════════
            // HEADER
            // ═════════════════════════════════════════
            AnimatedVisibility(
                visible = headerVisible,
                enter   = fadeIn(tween(350)) + slideInVertically(animationSpec = tween(350, easing = FastOutSlowInEasing), initialOffsetY = { -28 })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 14.dp, bottom = 4.dp)
                ) {
                    // Back + Title row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector    = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint           = Color.White.copy(alpha = 0.7f),
                            modifier       = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(7.dp))
                        Text(
                            text  = "Leaderboard",
                            style = HeadlineMedium.copy(
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 22.sp
                            )
                        )
                        if (privacyGhostMode) {
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E2238))
                                    .border(0.5.dp, Color(0xFF5B7FFF).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "STEALTH ON",
                                    style = LabelSmall.copy(
                                        color = Color(0xFF8DA4FF),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(3.dp))

                    // Subtitle + How it works
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text  = when (selectedTab) {
                                1 -> "Builders & Deep Work Output."
                                2 -> "Direct Peer Rank · $selectedExam"
                                else -> "Universal Focus Standings."
                            },
                            style = BodyMedium.copy(color = MutedForeground, fontSize = 13.sp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier          = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    Haptics.playLight(view)
                                    showHowItWorks = true
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text  = "How it works",
                                style = BodySmall.copy(
                                    color    = MutedForeground.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(Modifier.width(3.dp))
                            Icon(
                                imageVector    = Icons.Default.Info,
                                contentDescription = null,
                                tint           = MutedForeground.copy(alpha = 0.65f),
                                modifier       = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            // ═════════════════════════════════════════
            // TABS + FILTER ROW (Global | Founder | Aukaat)
            // ═════════════════════════════════════════
            AnimatedVisibility(
                visible = tabsVisible,
                enter   = fadeIn(tween(350)) + slideInVertically(animationSpec = tween(350, easing = FastOutSlowInEasing), initialOffsetY = { -20 })
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        // Left pill group: Global | Founder | Aukaat
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(22.dp))
                                .background(TabBg)
                                .border(0.5.dp, TabBorder, RoundedCornerShape(22.dp))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            listOf("Global", "Founder", "Aukaat").forEachIndexed { idx, label ->
                                val sel = selectedTab == idx
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (sel) Color.White else Color.Transparent)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                        ) {
                                            Haptics.playLight(view)
                                            selectedTab = idx
                                            if (idx == 2 && selectedExam.isBlank()) {
                                                showExamSelector = true
                                            }
                                        }
                                        .padding(horizontal = 13.dp, vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text  = label,
                                        style = LabelMedium.copy(
                                            color      = if (sel) Color(0xFF08080C) else MutedForeground.copy(alpha = 0.55f),
                                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                                            fontSize   = 12.sp,
                                            letterSpacing = 0.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Right: Aukaat Exam Switch Pill OR This Week Filter
                        if (selectedTab == 2) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF1B1B26))
                                    .border(1.dp, Color(0xFF4B6BFF).copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                                    .clickable {
                                        Haptics.playLight(view)
                                        showExamSelector = true
                                    }
                                    .padding(horizontal = 11.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text  = selectedExam.take(12) + if (selectedExam.length > 12) "…" else "",
                                    style = LabelMedium.copy(
                                        color         = Color(0xFF8DA4FF),
                                        fontSize      = 11.sp,
                                        fontWeight    = FontWeight.Bold,
                                        letterSpacing = 0.sp
                                    )
                                )
                                Spacer(Modifier.width(3.dp))
                                Icon(
                                    imageVector    = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Switch Exam",
                                    tint           = Color(0xFF8DA4FF),
                                    modifier       = Modifier.size(15.dp)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(TabBg)
                                    .border(0.5.dp, TabBorder, RoundedCornerShape(18.dp))
                                    .clickable { Haptics.playLight(view) }
                                    .padding(horizontal = 13.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text  = "This Week",
                                    style = LabelMedium.copy(
                                        color         = MutedForeground.copy(alpha = 0.8f),
                                        fontSize      = 12.sp,
                                        fontWeight    = FontWeight.Medium,
                                        letterSpacing = 0.sp
                                    )
                                )
                                Spacer(Modifier.width(3.dp))
                                Icon(
                                    imageVector    = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint           = MutedForeground.copy(alpha = 0.5f),
                                    modifier       = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Founder Guard Banner if User is Not on Productivity Track
                    if (selectedTab == 1 && !isProductivityGoal) {
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1F1A12))
                                .border(1.dp, Color(0xFFEAB308).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "⚡ Founder Track is optimized for builders with primary goal set to Productivity.",
                                style = BodySmall.copy(
                                    color = Color(0xFFFDE047),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }
            }

            // ═════════════════════════════════════════
            // SCROLLABLE BODY
            // ═════════════════════════════════════════
            LazyColumn(
                modifier       = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // ── Podium ───────────────────────────
                item {
                    AnimatedVisibility(
                        visible = podiumVisible,
                        enter   = fadeIn(tween(550)) + slideInVertically(animationSpec = tween(550, easing = FastOutSlowInEasing), initialOffsetY = { 50 })
                    ) {
                        PodiumSection(top3 = top3, glowAlpha = glowAlpha, crownFloat = crownFloat)
                    }
                }

                // ── Rank list (4+) ───────────────────
                itemsIndexed(rest) { idx, entry ->
                    var vis by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { delay(max(0, idx * 35L)); vis = true }
                    AnimatedVisibility(
                        visible = vis && listVisible,
                        enter   = fadeIn(tween(280)) + slideInVertically(animationSpec = tween(280, easing = FastOutSlowInEasing), initialOffsetY = { 18 })
                    ) {
                        RankListItem(entry = entry)
                    }
                }

                // ── Footer ───────────────────────────
                item {
                    Spacer(Modifier.height(18.dp))
                    FooterInfoSection()
                    Spacer(Modifier.height(14.dp))
                }
            }

            // ═════════════════════════════════════════
            // STICKY / FIXED USER RANK CARD
            // ═════════════════════════════════════════
            AnimatedVisibility(
                visible = listVisible,
                enter   = fadeIn(tween(400)) + slideInVertically(animationSpec = tween(400, easing = FastOutSlowInEasing), initialOffsetY = { 40 })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    BgDeep.copy(alpha = 0.92f),
                                    BgDeep
                                )
                            )
                        )
                        .navigationBarsPadding()
                        .padding(top = 6.dp, bottom = 84.dp)
                ) {
                    YourRankCard(userEntry = userEntry, glowAlpha = glowAlpha)
                }
            }
        }

        // ═════════════════════════════════════════
        // "HOW IT WORKS" GLASS MODAL
        // ═════════════════════════════════════════
        if (showHowItWorks) {
            HowItWorksBottomSheet(
                onDismiss = { showHowItWorks = false }
            )
        }

        // ═════════════════════════════════════════
        // "AUKAAT EXAM SELECTOR" BOTTOM SHEET
        // ═════════════════════════════════════════
        if (showExamSelector) {
            ExamSelectorBottomSheet(
                selectedExam = selectedExam,
                examChangeCount = examChangeCount,
                onSelectExam = { exam ->
                    mainViewModel.selectExamTrack(exam) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (success) {
                            showExamSelector = false
                        }
                    }
                },
                onDismiss = { showExamSelector = false }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PODIUM SECTION
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun PodiumSection(top3: List<LeaderboardUser>, glowAlpha: Float, crownFloat: Float) {
    val entry1 = top3.getOrNull(0)
    val entry2 = top3.getOrNull(1)
    val entry3 = top3.getOrNull(2)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
    ) {
        // Atmospheric canvas behind podium
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.6f)) {
            // Blue radial glow at center (for #1)
            drawCircle(
                brush  = Brush.radialGradient(
                    colors = listOf(PodiumFirst.copy(alpha = glowAlpha * 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.3f),
                    radius = size.width * 0.38f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = size.width * 0.38f
            )
            // Mist at ground line
            drawRect(
                brush = Brush.verticalGradient(
                    colors      = listOf(Color.Transparent, Color(0xFF07070A).copy(alpha = 0.7f)),
                    startY      = size.height * 0.7f,
                    endY        = size.height
                )
            )
        }

        // Three podium columns
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            // #2 Left — Silver
            if (entry2 != null) {
                PodiumEntry(
                    entry              = entry2,
                    ringColor          = PodiumSecond,
                    ringColorDark      = PodiumSecondDk,
                    podiumHeight       = 85.dp,
                    avatarSize         = 50.dp,
                    isFirst            = false,
                    rankLabel          = "2",
                    glowAlpha          = 0f,
                    crownFloat         = 0f,
                    modifier           = Modifier.weight(1f)
                )
            }

            // #1 Center — Blue neon
            if (entry1 != null) {
                PodiumEntry(
                    entry              = entry1,
                    ringColor          = PodiumFirst,
                    ringColorDark      = PodiumFirstDark,
                    podiumHeight       = 112.dp,
                    avatarSize         = 62.dp,
                    isFirst            = true,
                    rankLabel          = "1",
                    glowAlpha          = glowAlpha,
                    crownFloat         = crownFloat,
                    modifier           = Modifier.weight(1f)
                )
            }

            // #3 Right — Light grey
            if (entry3 != null) {
                PodiumEntry(
                    entry              = entry3,
                    ringColor          = PodiumThird,
                    ringColorDark      = PodiumThirdDk,
                    podiumHeight       = 68.dp,
                    avatarSize         = 48.dp,
                    isFirst            = false,
                    rankLabel          = "3",
                    glowAlpha          = 0f,
                    crownFloat         = 0f,
                    modifier           = Modifier.weight(1f)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SINGLE PODIUM ENTRY (Premium)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun PodiumEntry(
    entry         : LeaderboardUser,
    ringColor     : Color,
    ringColorDark : Color,
    podiumHeight  : Dp,
    avatarSize    : Dp,
    isFirst       : Boolean,
    rankLabel     : String,
    glowAlpha     : Float,
    crownFloat    : Float,
    modifier      : Modifier = Modifier
) {
    // Premium gradient avatars for top 3
    val avatarGradient = when (rankLabel) {
        "1" -> Brush.linearGradient(
            colors = listOf(Color(0xFF6366F1), Color(0xFF3B82F6), Color(0xFF06B6D4))
        )
        "2" -> Brush.linearGradient(
            colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFF43F5E))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFF14B8A6), Color(0xFF22D3EE), Color(0xFF3B82F6))
        )
    }

    // Rank badge label: T1 branding
    val badgeLabel = when (rankLabel) {
        "1" -> "T1"
        "2" -> "T1"
        else -> "T1"
    }

    Column(
        modifier             = modifier.padding(horizontal = 2.dp),
        horizontalAlignment  = Alignment.CenterHorizontally
    ) {
        // Crown (only for #1, floating animation)
        if (isFirst) {
            Text(
                text     = "👑",
                fontSize = 22.sp,
                modifier = Modifier
                    .offset(y = crownFloat.dp)
                    .padding(bottom = 2.dp)
            )
        } else {
            Spacer(Modifier.height(30.dp))
        }

        // Avatar + ring + rank badge
        Box(
            modifier         = Modifier.size(avatarSize + 14.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow layer (all top 3 get a subtle glow, #1 stronger)
            Box(
                modifier = Modifier
                    .size(avatarSize + 26.dp)
                    .background(
                        brush  = Brush.radialGradient(
                            colors = listOf(
                                ringColor.copy(alpha = if (isFirst) glowAlpha * 0.4f else 0.12f),
                                Color.Transparent
                            )
                        ),
                        shape  = CircleShape
                    )
            )

            // Gradient ring (thicker for premium feel)
            Box(
                modifier         = Modifier
                    .size(avatarSize + 8.dp)
                    .background(
                        brush  = Brush.sweepGradient(
                            colors = listOf(ringColor, ringColorDark, ringColor.copy(alpha = 0.6f), ringColor)
                        ),
                        shape  = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Dark inner ring separator
                Box(
                    modifier         = Modifier
                        .size(avatarSize + 2.dp)
                        .background(color = BgDeep, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Premium gradient avatar
                    Box(
                        modifier         = Modifier
                            .size(avatarSize)
                            .clip(CircleShape)
                            .background(brush = avatarGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner decorative ring
                        Box(
                            modifier = Modifier
                                .size(avatarSize - 6.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = entry.name.take(1).uppercase(),
                                style = HeadlineSmall.copy(
                                    color      = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = if (isFirst) 24.sp else 18.sp
                                )
                            )
                        }
                    }
                }
            }

            // Rank badge at bottom of ring
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.linearGradient(listOf(ringColor, ringColorDark))
                    )
                    .border(1.5.dp, BgDeep, RoundedCornerShape(10.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = badgeLabel,
                    style = LabelSmall.copy(
                        color         = Color(0xFF08080C),
                        fontWeight    = FontWeight.ExtraBold,
                        fontSize      = 8.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Name
        Text(
            text     = entry.name,
            style    = BodySmall.copy(
                color      = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize   = if (isFirst) 14.sp else 12.sp
            ),
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis
        )

        // "Focus Score" label
        Text(
            text  = "Focus Score",
            style = LabelSmall.copy(
                color         = MutedForeground.copy(alpha = 0.45f),
                fontWeight    = FontWeight.Normal,
                fontSize      = 9.sp,
                letterSpacing = 0.sp
            )
        )

        // Score number
        Text(
            text  = "${entry.score.toInt()}",
            style = ScoreMedium.copy(
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = if (isFirst) 30.sp else 24.sp
            )
        )

        Spacer(Modifier.height(6.dp))

        // Premium podium platform block
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PodiumBlockTop,
                            PodiumBlockBot.copy(alpha = 0.95f),
                            PodiumBlockBot
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ringColor.copy(alpha = if (isFirst) 0.35f else 0.18f),
                            ringColor.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .drawBehind {
                    // Inner top-edge shine
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                ringColor.copy(alpha = if (isFirst) 0.12f else 0.06f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY   = size.height * 0.25f
                        )
                    )
                }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// RANK LIST ITEM (ranks 4+)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun RankListItem(entry: LeaderboardUser) {
    val charIdx  = entry.name.firstOrNull()?.code ?: 0
    val avatarBg = AvatarColors[charIdx % AvatarColors.size]

    val pctVal = entry.percentile
        .replace("Top ", "").replace("top ", "").replace("%", "").trim()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .drawBehind {
                drawLine(
                    color       = ListDivider,
                    start       = Offset(0f, size.height),
                    end         = Offset(size.width, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number
        Text(
            text      = "${entry.rank}",
            style     = BodyMedium.copy(
                color      = MutedForeground.copy(alpha = 0.45f),
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp
            ),
            modifier  = Modifier.width(26.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.width(10.dp))

        // Avatar circle
        Box(
            modifier         = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AvatarDark)
                .border(1.dp, Color.White.copy(alpha = 0.06f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = entry.name.take(1).uppercase(),
                style = BodyMedium.copy(
                    color      = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
            )
        }

        Spacer(Modifier.width(12.dp))

        // Name + subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = entry.name,
                style    = BodyMedium.copy(
                    color      = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                ),
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis
            )
            Text(
                text  = "Top $pctVal% · ${entry.badge}",
                style = BodySmall.copy(
                    color    = MutedForeground.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            )
        }

        // Score
        Text(
            text  = "${entry.score.toInt()}",
            style = ScoreSmall.copy(
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        )

        Spacer(Modifier.width(10.dp))

        // Mini sparkline
        SparklineChart(
            movement = entry.movement,
            seed     = entry.rank,
            modifier = Modifier
                .width(38.dp)
                .height(18.dp)
        )

        Spacer(Modifier.width(8.dp))

        // Movement text badge
        MovementBadge(movement = entry.movement, value = entry.movementVal)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SPARKLINE MINI CHART
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun SparklineChart(movement: String, seed: Int, modifier: Modifier = Modifier) {
    val lineColor = when (movement) {
        "up"   -> MoveUp
        "down" -> MoveDown
        else   -> MutedForeground.copy(alpha = 0.3f)
    }

    Canvas(modifier = modifier) {
        val w      = size.width
        val h      = size.height
        val points = 7
        val stepX  = w / (points - 1)

        val yValues = (0 until points).map { i ->
            val base = when (movement) {
                "up"   -> h * 0.72f - (i.toFloat() / points) * h * 0.52f
                "down" -> h * 0.28f + (i.toFloat() / points) * h * 0.45f
                else   -> h * 0.5f + sin((i + seed).toDouble()).toFloat() * h * 0.14f
            }
            val noise = sin((i * 2.3 + seed * 1.9).toDouble()).toFloat() * h * 0.10f
            (base + noise).coerceIn(h * 0.08f, h * 0.92f)
        }

        val path = Path().apply {
            moveTo(0f, yValues[0])
            for (i in 1 until points) {
                val px = (i - 1) * stepX
                val cx = i * stepX
                val mx = (px + cx) / 2f
                cubicTo(mx, yValues[i - 1], mx, yValues[i], cx, yValues[i])
            }
        }

        drawPath(
            path  = path,
            color = lineColor.copy(alpha = 0.75f),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MOVEMENT BADGE  (↑3  /  ↓2  /  –)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun MovementBadge(movement: String, value: Int) {
    val (color, arrow) = when (movement) {
        "up"   -> MoveUp   to "↑"
        "down" -> MoveDown to "↓"
        else   -> MoveNeutral to "–"
    }
    val displayText = if (movement == "same") "–" else "$arrow$value"

    Text(
        text     = displayText,
        style    = LabelSmall.copy(
            color         = color,
            fontWeight    = FontWeight.Bold,
            fontSize      = 12.sp,
            letterSpacing = 0.sp
        ),
        modifier = Modifier.width(30.dp),
        textAlign = TextAlign.Start
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// YOUR RANK CARD
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun YourRankCard(userEntry: LeaderboardUser, glowAlpha: Float) {
    val charIdx  = userEntry.name.firstOrNull()?.code ?: 0
    val avatarBg = AvatarColors[charIdx % AvatarColors.size]

    val pctVal = userEntry.percentile
        .replace("Top ", "").replace("top ", "").replace("%", "").trim()

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
    ) {
        // Soft blue glow behind card
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 3.dp)
                .blur(14.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(UserCardBorder.copy(alpha = glowAlpha * 0.22f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(UserCardBg1, UserCardBg2)
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            UserCardBorder.copy(alpha = 0.45f),
                            UserCardBorder.copy(alpha = 0.18f)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Your Rank" + rank number
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.width(46.dp)
            ) {
                Text(
                    text  = "Your Rank",
                    style = LabelSmall.copy(
                        color         = UserRankBlue.copy(alpha = 0.75f),
                        fontSize      = 8.sp,
                        fontWeight    = FontWeight.Medium,
                        letterSpacing = 0.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text  = "${userEntry.rank}",
                    style = ScoreSmall.copy(
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 22.sp
                    )
                )
            }

            Spacer(Modifier.width(10.dp))

            // Avatar
            Box(
                modifier         = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(AvatarDark)
                    .border(1.dp, UserCardBorder.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = userEntry.name.take(1).uppercase(),
                    style = BodyMedium.copy(
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    )
                )
            }

            Spacer(Modifier.width(12.dp))

            // Name + subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = userEntry.name,
                    style    = BodyMedium.copy(
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    ),
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis
                )
                Text(
                    text  = "Top $pctVal% · ${userEntry.badge}",
                    style = BodySmall.copy(
                        color    = MutedForeground.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                )
            }

            // Score
            Text(
                text  = "${userEntry.score.toInt()}",
                style = ScoreSmall.copy(
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            )

            Spacer(Modifier.width(10.dp))

            // Sparkline
            SparklineChart(
                movement = if (userEntry.movement == "same") "up" else userEntry.movement,
                seed     = userEntry.rank,
                modifier = Modifier
                    .width(38.dp)
                    .height(18.dp)
            )

            Spacer(Modifier.width(8.dp))

            // Movement
            MovementBadge(
                movement = if (userEntry.movement == "same") "up" else userEntry.movement,
                value    = if (userEntry.movementVal == 0) 5 else userEntry.movementVal
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// FOOTER INFO SECTION
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun FooterInfoSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0D0D11))
            .border(0.5.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(14.dp))
            .padding(vertical = 18.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FooterItem(icon = Icons.Default.Schedule, title = "Ranks update\ndaily",     sub = "Stay consistent to\nclimb higher.")
        FooterItem(icon = Icons.Default.Verified, title = "Real Focus. Real Rank.", sub = "Scores are based on\nyour actual behavior.")
        FooterItem(icon = Icons.Default.Shield,   title = "No shortcuts",            sub = "Cheating leads to\npermanent ban.")
    }
}

@Composable
private fun FooterItem(
    icon  : androidx.compose.ui.graphics.vector.ImageVector,
    title : String,
    sub   : String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.width(108.dp)
    ) {
        Icon(
            imageVector    = icon,
            contentDescription = null,
            tint           = FooterIconColor,
            modifier       = Modifier.size(18.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text      = title,
            style     = BodySmall.copy(
                color      = MutedForeground.copy(alpha = 0.55f),
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 14.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text      = sub,
            style     = BodySmall.copy(
                color      = MutedForeground.copy(alpha = 0.35f),
                fontSize   = 9.sp,
                lineHeight = 13.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// "HOW IT WORKS" MODAL (GLASS OVERLAY)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun HowItWorksBottomSheet(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF0F0F14))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) {}
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Leaderboard Tiers",
                    style = HeadlineMedium.copy(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Tier 1: Global
            TierExplainCard(
                icon = Icons.Default.Public,
                iconTint = Color(0xFF5B7FFF),
                title = "1. Global Leaderboard",
                desc = "Universal rankings across all T1 users based purely on relative focus score performance and discipline."
            )

            Spacer(Modifier.height(12.dp))

            // Tier 2: Founder
            TierExplainCard(
                icon = Icons.Default.Bolt,
                iconTint = Color(0xFFF59E0B),
                title = "2. Founder Track",
                desc = "Exclusive for builders, developers, and creators whose primary goal is Productivity. Ranks actual deep work volume."
            )

            Spacer(Modifier.height(12.dp))

            // Tier 3: Aukaat
            TierExplainCard(
                icon = Icons.Default.School,
                iconTint = Color(0xFF22C55E),
                title = "3. Aukaat (A-U-K-A-A-T)",
                desc = "Strictly isolated competitive exam percentiles (JEE, NEET, UPSC, CA, etc.). Compare yourself only against candidates targeting your exact exam."
            )

            Spacer(Modifier.height(24.dp))

            // Action Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { onDismiss() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Got It",
                    style = BodyMedium.copy(
                        color = Color(0xFF08080C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TierExplainCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF16161D))
            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = BodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = desc,
                style = BodySmall.copy(
                    color = MutedForeground.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// "AUKAAT EXAM SELECTOR" BOTTOM SHEET
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun ExamSelectorBottomSheet(
    selectedExam: String,
    examChangeCount: Int,
    onSelectExam: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val exams = listOf(
        "JEE (Mains & Advanced)",
        "NEET (Medical)",
        "UPSC / Civil Services",
        "CA / CFA / Finance",
        "GATE / Engineering",
        "CAT / MBA",
        "Boards (Class 10/12)",
        "General Academic Prep"
    )

    val remainingChanges = kotlin.math.max(0, 2 - examChangeCount)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF0F0F14))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) {}
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Select Your Aukaat Track",
                        style = HeadlineMedium.copy(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$remainingChanges / 2 track switches available this week",
                        style = BodySmall.copy(
                            color = if (remainingChanges > 0) Color(0xFF22C55E) else Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Exam Options
            exams.forEach { exam ->
                val isSelected = exam.equals(selectedExam, ignoreCase = true)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF1F2544) else Color(0xFF14141A))
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFF4B6BFF) else Color.White.copy(alpha = 0.04f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            onSelectExam(exam)
                        }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exam,
                        style = BodyMedium.copy(
                            color = if (isSelected) Color.White else MutedForeground.copy(alpha = 0.85f),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFF5B7FFF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

