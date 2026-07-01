package com.example.t1.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Compare
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.t1.domain.model.LeaderboardEntry
import com.example.t1.ui.viewmodel.MainViewModel
import com.example.t1.theme.*
import com.example.t1.ui.onboarding.OnboardingBackground
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay
import kotlin.math.max

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val score: Float,
    val streak: Int,
    val percentile: String,
    val badge: String,
    val movement: String, // "up", "down", "same"
    val movementVal: Int
)

fun generateLeaderboardUsers(seed: Int): List<LeaderboardUser> {
    val names = listOf(
        "S.Tanaka", "Erikson.v", "Elena.D", "Marcus_A", "J.Miller", "K.Chen",
        "Nora.F", "R.Patel", "Zain.K", "Lily.W", "Omar.S", "Ava.J",
        "Dex.M", "Priya.R", "Leo.T", "Sam.B", "Rina.G", "Jay.H",
        "Milo.C", "Kira.N", "Hugo.L", "Zara.V", "Axel.D", "Nina.P",
        "Rex.Q", "Cleo.A", "Finn.O", "Ivy.Z", "Jude.E", "Lana.X",
        "Theo.W", "Maya.S", "Ravi.K", "Elle.B", "Nash.G", "Skye.F",
        "Drew.H", "Luna.M", "Kai.R", "Jade.T", "Beau.L", "Wren.C",
        "Cole.J", "Aria.N", "Troy.P", "Sage.V", "Rhys.D", "Tara.Q",
        "Blake.A", "Faye.O"
    )

    return names.mapIndexed { idx, name ->
        val score = max(10f, ((99.5f - idx * 0.9f - (seed * (idx + 1) % 5) * 0.3f) * 10).toInt() / 10f)
        val streak = max(1, 25 - idx + (seed * idx % 7))
        val percentile = "top ${max(1, ((idx + 1).toFloat() / 50 * 100).toInt())}%"
        val movement = when ((seed + idx) % 3) {
            0 -> "up"
            1 -> "down"
            else -> "same"
        }
        val movementVal = ((seed + idx) % 5) + 1

        LeaderboardUser(
            rank = idx + 1,
            name = "$name.t1",
            score = score,
            streak = streak,
            percentile = percentile,
            badge = "Silver badge",
            movement = movement,
            movementVal = movementVal
        )
    }.take(50)
}

enum class LeaderboardTab {
    DAY, WEEK, ALL
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LeaderboardScreen(
    username: String,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val view = LocalView.current
    var activeTab by remember { mutableStateOf(LeaderboardTab.WEEK) }
    val leaderboardEntries by mainViewModel.leaderboardState.collectAsStateWithLifecycle()
    val temporaryRank by mainViewModel.temporaryRank.collectAsStateWithLifecycle()
    val userProfile by mainViewModel.userProfile.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        mainViewModel.loadTemporaryRank()
    }

    val currentData = leaderboardEntries.map { entry ->
        val formattedName = entry.username
        val (movementDirection, movementVal) = when {
            entry.rankMovement.startsWith("▲") -> "up" to (entry.rankMovement.drop(1).toIntOrNull() ?: 0)
            entry.rankMovement.startsWith("▼") -> "down" to (entry.rankMovement.drop(1).toIntOrNull() ?: 0)
            entry.rankMovement == "NEW" -> "up" to 0
            else -> "same" to 0
        }
        LeaderboardUser(
            rank = entry.rank,
            name = formattedName,
            score = entry.focusScore.toFloat(),
            streak = entry.streak,
            percentile = "top ${entry.percentile}%",
            badge = entry.badge,
            movement = movementDirection,
            movementVal = movementVal
        )
    }

    val top3 = currentData.take(3)
    val rest = currentData.drop(3)

    val userEntryFromList = leaderboardEntries.firstOrNull {
        it.username.lowercase() == username.lowercase()
    }?.let { entry ->
        val formattedName = entry.username
        val (movementDirection, movementVal) = when {
            entry.rankMovement.startsWith("▲") -> "up" to (entry.rankMovement.drop(1).toIntOrNull() ?: 0)
            entry.rankMovement.startsWith("▼") -> "down" to (entry.rankMovement.drop(1).toIntOrNull() ?: 0)
            entry.rankMovement == "NEW" -> "up" to 0
            else -> "same" to 0
        }
        LeaderboardUser(
            rank = entry.rank,
            name = formattedName,
            score = entry.focusScore.toFloat(),
            streak = entry.streak,
            percentile = "top ${entry.percentile}%",
            badge = entry.badge,
            movement = movementDirection,
            movementVal = movementVal
        )
    }
    val userScoreState = mainViewModel.cachedFocusScore.collectAsStateWithLifecycle()
    
    val finalUserRank = temporaryRank ?: 1
    val userEntry = userEntryFromList ?: LeaderboardUser(
        rank = finalUserRank,
        name = userProfile?.displayName ?: userProfile?.username ?: username,
        score = userScoreState.value.toFloat(),
        streak = userProfile?.streak ?: 0,
        percentile = "top ${max(1, (finalUserRank * 100) / max(1, currentData.size))}%",
        badge = if (userProfile != null) {
            val pct = max(1, (finalUserRank * 100) / max(1, currentData.size))
            when {
                pct <= 1 -> "Apex Predator"
                pct <= 5 -> "Elite Focus"
                pct <= 10 -> "Deep Worker"
                pct <= 20 -> "Top Performer"
                pct <= 35 -> "Consistent Builder"
                pct <= 50 -> "Balanced Performer"
                else -> "Getting Started"
            }
        } else "Getting Started",
        movement = "same",
        movementVal = 0
    )

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "LEADERBOARD",
                style = TrackingWide.copy(
                    color = MutedForeground,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Stay hard to catch.",
                style = HeadlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(
                        colors = listOf(GradientTextStart, GradientTextEnd)
                    )
                ),
                letterSpacing = (-0.5).sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Tabs Selector Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LeaderboardTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    val tabTextAlpha = if (isSelected) 1f else 0.5f

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Foreground else Color.Transparent)
                            .clickable {
                                Haptics.playLight(view)
                                activeTab = tab
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (tab) {
                                LeaderboardTab.WEEK -> "GLOBAL"
                                LeaderboardTab.DAY -> "COLLEGE"
                                LeaderboardTab.ALL -> "FRIENDS"
                            },
                            style = LabelMedium.copy(
                                color = if (isSelected) Background else Foreground,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Tab Content (Podium and List)
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)).togetherWith(fadeOut(animationSpec = tween(250)))
                },
                label = "leaderboardContent",
                modifier = Modifier.weight(1f)
            ) { targetTab ->
                if (targetTab == LeaderboardTab.DAY || targetTab == LeaderboardTab.ALL) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Coming soon in updates",
                            style = HeadlineMedium.copy(
                                color = GlowPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "We are building community spaces just for you.",
                            style = BodyMedium.copy(color = MutedForeground),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val tabData = currentData
                    val tabTop3 = top3
                    val tabRest = rest

                    Column(modifier = Modifier.fillMaxSize()) {
                    // Podium Cards Row: displays 2nd (idx 1), 1st (idx 0), 3rd (idx 2)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val order = listOf(1, 0, 2)
                        order.forEach { idx ->
                            val entry = tabTop3.getOrNull(idx)
                            if (entry != null) {
                                val isFirst = entry.rank == 1
                                val cardScale = if (isFirst) 1.04f else 1.0f
                                val avatarSize = if (isFirst) 52.dp else 44.dp

                                val glowBrush = if (isFirst) {
                                    Brush.radialGradient(
                                        colors = listOf(RankGold.copy(alpha = 0.15f), Color.Transparent)
                                    )
                                } else null

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .scale(cardScale)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            1.dp,
                                            if (isFirst) RankGold.copy(alpha = 0.4f) else Border,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .background(Card)
                                        .drawBehind {
                                            if (glowBrush != null) {
                                                drawRect(brush = glowBrush)
                                            }
                                        }
                                        .padding(vertical = 16.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Avatar
                                    val charIndex = entry.name.firstOrNull()?.code ?: 0
                                    val avatarBg = AvatarColors[charIndex % AvatarColors.size]

                                    Box(
                                        modifier = Modifier
                                            .size(avatarSize)
                                            .clip(CircleShape)
                                            .background(avatarBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = entry.name.take(1),
                                            style = HeadlineSmall.copy(
                                                color = Background,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = entry.name,
                                        style = BodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Foreground
                                        ),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = "TOP ${entry.percentile.replace("top ", "")}",
                                        style = BodySmall.copy(
                                            color = MutedForeground,
                                            fontSize = 9.sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "${entry.score}",
                                        style = ScoreMedium.copy(
                                            color = Foreground,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Challenge/Compare CTA
                                    val challengeInteraction = remember { MutableInteractionSource() }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                                            .background(Secondary)
                                            .clickable(
                                                interactionSource = challengeInteraction,
                                                indication = null
                                            ) {
                                                Haptics.playLight(view)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isFirst) "CHALLENGE" else "COMPARE",
                                            style = LabelSmall.copy(
                                                fontSize = 9.sp,
                                                color = MutedForeground,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // User Personal Rank Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Border, RoundedCornerShape(16.dp))
                            .background(Card)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${userEntry.rank}  ",
                                        style = BodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MutedForeground
                                        )
                                    )
                                    Text(
                                        text = userEntry.name,
                                        style = BodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = Foreground
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${userEntry.percentile} · ${userEntry.badge}",
                                    style = BodySmall.copy(color = MutedForeground)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${userEntry.score}",
                                    style = ScoreMedium.copy(
                                        color = Foreground,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "Down",
                                        tint = Destructive,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "-${userEntry.movementVal}",
                                        style = BodySmall.copy(
                                            color = Destructive,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Scrollable List (ranks 4-50)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 120.dp)
                    ) {
                        itemsIndexed(tabRest) { idx, entry ->
                            var itemVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                delay(max(0, idx * 15L))
                                itemVisible = true
                            }

                            AnimatedVisibility(
                                visible = itemVisible,
                                enter = fadeIn(animationSpec = tween(250)) + androidx.compose.animation.slideInHorizontally(
                                    initialOffsetX = { -15 },
                                    animationSpec = tween(250)
                                ),
                                exit = fadeOut()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                        .drawBehind {
                                            // Draw bottom border line
                                            drawLine(
                                                color = Border,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${entry.rank}",
                                        style = ScoreSmall.copy(
                                            color = MutedForeground,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.width(36.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = entry.name,
                                                style = BodyMedium.copy(
                                                    fontWeight = FontWeight.Medium,
                                                    color = Foreground
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))

                                            if (entry.movement == "up") {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowUpward,
                                                        contentDescription = "Up",
                                                        tint = GlowGreen,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = "+${entry.movementVal}",
                                                        style = BodySmall.copy(
                                                            color = GlowGreen,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    )
                                                }
                                            } else if (entry.movement == "down") {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        contentDescription = "Down",
                                                        tint = Destructive,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = "-${entry.movementVal}",
                                                        style = BodySmall.copy(
                                                            color = Destructive,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = "top ${entry.percentile.replace("top ", "")} performer",
                                            style = BodySmall.copy(
                                                color = MutedForeground,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    Text(
                                        text = "${entry.score}",
                                        style = ScoreSmall.copy(
                                            color = Foreground,
                                            fontWeight = FontWeight.Bold
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
