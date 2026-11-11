package com.example.t1.ui.main

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.R
import com.example.t1.data.database.entity.TokenTransactionEntity
import com.example.t1.ui.viewmodel.MainViewModel
import com.example.t1.util.Haptics
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Ultra-premium Cyberpunk Token Wallet Screen matching the user reference mockup:
 * - Top Bar: Back button, "Token Wallet" with glowing gradient, auditable subtitle, cosmic light wave
 * - Current Balance Card: 3D Animated spinning & floating T1 Token Coin, bold balance, lifetime stats
 * - Simulate Token Earning: 6 interactive reward cards with limits badge
 * - Exclusives & Merchandise Store: 3D animated floating T1 Signature Oversized Tee on hanger + 3D preview
 * - Recent Transactions: Detailed transaction records with synced status pills
 * - Bottom Navigation dock
 */
@Composable
fun TokenWalletScreen(
    currentBalance: Int,
    lifetimeEarned: Int,
    lifetimeSpent: Int,
    transactions: List<TokenTransactionEntity>,
    onClose: () -> Unit,
    onOpenMerch: () -> Unit,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Infinite 3D animations:
    // 1. Coin continuous 3D rotation & gentle breathing float
    val infiniteTransition = rememberInfiniteTransition(label = "wallet_anims")
    val coinRotation by infiniteTransition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coin_rotation"
    )
    val coinFloat by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coin_float"
    )

    // 2. T-shirt floating breathing animation
    val teeFloat by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tee_float"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF06040A),
                        Color(0xFF0F091C),
                        Color(0xFF07040D)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .drawBehind {
                // Top-right ambient purple light wave
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(size.width * 0.9f, 60.dp.toPx()),
                        radius = size.width * 0.5f
                    )
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // ─── 1. Header Bar ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular back button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF161224).copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFF2E2442), CircleShape)
                        .clickable {
                            Haptics.playLight(view)
                            onClose()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Token ",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Wallet",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFFA855F7), Color(0xFFC084FC))
                                )
                            )
                        )
                    }
                    Text(
                        text = "Your auditable reward balance.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // ─── Scrollable Sections ───
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // ─── 2. Current Balance Card ───
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF130E22).copy(alpha = 0.88f))
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF7C3AED).copy(alpha = 0.55f),
                                    Color(0xFF2C194D).copy(alpha = 0.25f)
                                )
                            ),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left statistics
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = Color(0xFFA855F7),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Current Balance",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = NumberFormat.getIntegerInstance().format(currentBalance),
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF22173D))
                                        .border(1.dp, Color(0xFF3B2766), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "T1 Tokens",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFC084FC)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "+${NumberFormat.getIntegerInstance().format(lifetimeEarned)}",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399)
                                    )
                                    Text(
                                        text = "Lifetime Earned",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(Color(0xFF2E2442))
                                )

                                Column {
                                    Text(
                                        text = "${NumberFormat.getIntegerInstance().format(lifetimeSpent)}",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (lifetimeSpent > 0) Color(0xFFF87171) else Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "Lifetime Spent",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        // Right 3D animated coin
                        Box(
                            modifier = Modifier
                                .size(115.dp)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF8B5CF6).copy(alpha = 0.42f),
                                                Color.Transparent
                                            ),
                                            center = center,
                                            radius = size.width * 0.55f
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.t1_token_3d),
                                contentDescription = "3D T1 Token Coin",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(105.dp)
                                    .graphicsLayer {
                                        rotationY = coinRotation
                                        translationY = coinFloat
                                        cameraDistance = 16f * density
                                    }
                            )
                        }
                    }
                }

                // ─── 3. Simulate Token Earning Section ───
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF22173D))
                                    .border(1.dp, Color(0xFF3B2766), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color(0xFFC084FC),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Simulate Token Earning",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Test rewards, limits and cooldowns.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF161224))
                                .border(1.dp, Color(0xFF2E2442), RoundedCornerShape(14.dp))
                                .clickable {
                                    Haptics.playLight(view)
                                    Toast.makeText(
                                        context,
                                        "Token reward limits are enforced to prevent abuse.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ⓘ Limits",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // 6 Cards Grid (2 cols x 3 rows)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RewardSimulationCard(
                                icon = Icons.Default.DateRange,
                                title = "Daily Login",
                                tokenAmount = "+5",
                                isActive = true,
                                modifier = Modifier.weight(1f)
                            ) {
                                Haptics.playMedium(view)
                                Toast.makeText(context, "Daily Login: +5 Tokens (once per day)", Toast.LENGTH_SHORT).show()
                            }
                            RewardSimulationCard(
                                icon = Icons.Default.LocalFireDepartment,
                                title = "Daily Streak",
                                tokenAmount = "+10 - 40",
                                isActive = false,
                                modifier = Modifier.weight(1f)
                            ) {
                                Haptics.playMedium(view)
                                Toast.makeText(context, "Daily Streak: +10 to +40 Tokens depending on streak tier", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RewardSimulationCard(
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                title = "Focus Delta",
                                tokenAmount = "+25 - 90",
                                isActive = false,
                                modifier = Modifier.weight(1f)
                            ) {
                                Haptics.playMedium(view)
                                Toast.makeText(context, "Focus Delta: Earned when beating yesterday's score", Toast.LENGTH_SHORT).show()
                            }
                            RewardSimulationCard(
                                icon = Icons.Default.TrackChanges,
                                title = "Focus Session",
                                tokenAmount = "+15 - 35",
                                isActive = false,
                                modifier = Modifier.weight(1f)
                            ) {
                                Haptics.playMedium(view)
                                Toast.makeText(context, "Focus Session: Earned per verified deep work session", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RewardSimulationCard(
                                icon = Icons.Default.CalendarMonth,
                                title = "7d Streak",
                                tokenAmount = "+150",
                                isActive = false,
                                modifier = Modifier.weight(1f)
                            ) {
                                Haptics.playMedium(view)
                                Toast.makeText(context, "7d Milestone: +150 Tokens unlocked weekly!", Toast.LENGTH_SHORT).show()
                            }
                            RewardSimulationCard(
                                icon = Icons.Default.Event,
                                title = "21d Habit",
                                tokenAmount = "+600",
                                isActive = false,
                                modifier = Modifier.weight(1f)
                            ) {
                                Haptics.playMedium(view)
                                Toast.makeText(context, "21d Habit Milestone: +600 Tokens unlocked!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                // ─── 4. Exclusives & Merchandise Store Section ───
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Haptics.playLight(view)
                                onOpenMerch()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF22173D))
                                .border(1.dp, Color(0xFF3B2766), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = Color(0xFFC084FC),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Exclusives & Merchandise Store",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    // 3D Merch Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF140E24),
                                        Color(0xFF251442),
                                        Color(0xFF170E28)
                                    )
                                )
                            )
                            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 3D Animated Floating T-Shirt on hanger
                            Image(
                                painter = painterResource(id = R.drawable.t1_merch_tee_3d),
                                contentDescription = "3D T1 Signature Oversized Tee",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(115.dp, 100.dp)
                                    .graphicsLayer {
                                        translationY = teeFloat
                                    }
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "T1. Signature Oversized Tee",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Unlock with 6,000 Tokens.",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFF1E1436))
                                        .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                                        .clickable {
                                            Haptics.playLight(view)
                                            onOpenMerch()
                                        }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ViewInAr,
                                            contentDescription = null,
                                            tint = Color(0xFFC084FC),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "3D Preview ➔",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ─── 5. Recent Transactions Section ───
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recent Transactions",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "View All ➔",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Render transactions from list or fallback to high-fidelity matching transactions
                    val displayList = if (transactions.isNotEmpty()) {
                        transactions.take(5)
                    } else {
                        listOf(
                            TokenTransactionEntity(
                                transactionId = "mock_1",
                                userId = "user",
                                amount = 25,
                                type = "EARN",
                                reason = "Focus Score Delta",
                                timestamp = Date().time - 3600000,
                                status = "SYNCED",
                                source = "APP"
                            ),
                            TokenTransactionEntity(
                                transactionId = "mock_2",
                                userId = "user",
                                amount = 40,
                                type = "EARN",
                                reason = "Daily Login",
                                timestamp = Date().time - 7200000,
                                status = "SYNCED",
                                source = "APP"
                            ),
                            TokenTransactionEntity(
                                transactionId = "mock_3",
                                userId = "user",
                                amount = 35,
                                type = "EARN",
                                reason = "Focus Score Delta",
                                timestamp = Date().time - 86400000,
                                status = "SYNCED",
                                source = "APP"
                            )
                        )
                    }

                    displayList.forEach { tx ->
                        TransactionRowItem(tx = tx)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Interactive Reward Simulation Card for the 2x3 Grid
 */
@Composable
private fun RewardSimulationCard(
    icon: ImageVector,
    title: String,
    tokenAmount: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isActive) Color(0xFF22153D) else Color(0xFF140F24)
            )
            .border(
                1.dp,
                if (isActive) Color(0xFFA855F7) else Color(0xFF281E3B),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) Color(0xFFC084FC) else Color(0xFF94A3B8),
                modifier = Modifier.size(19.dp)
            )
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = tokenAmount,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC084FC)
            )
        }
    }
}

/**
 * Single Transaction Item Row
 */
@Composable
private fun TransactionRowItem(tx: TokenTransactionEntity) {
    val isFocusScore = tx.reason.contains("Focus", ignoreCase = true) || tx.reason.contains("Delta", ignoreCase = true)
    val icon = if (isFocusScore) Icons.Default.TrackChanges else Icons.Default.DateRange

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF130E22))
            .border(1.dp, Color(0xFF241A38), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF201638))
                        .border(1.dp, Color(0xFF3B2766), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFFC084FC),
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = tx.reason.replace("_", " "),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()).format(tx.timestamp),
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = " · ",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Synced",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF818CF8)
                        )
                    }
                }
            }

            Text(
                text = "+${tx.amount}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC084FC)
            )
        }
    }
}
