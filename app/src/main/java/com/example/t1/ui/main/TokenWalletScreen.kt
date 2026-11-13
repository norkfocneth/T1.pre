package com.example.t1.ui.main

import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.data.database.entity.TokenTransactionEntity
import com.example.t1.theme.BodySmall
import com.example.t1.theme.Border
import com.example.t1.theme.Card
import com.example.t1.theme.Foreground
import com.example.t1.theme.GlowPrimary
import com.example.t1.theme.HeadlineMedium
import com.example.t1.theme.HeadlineSmall
import com.example.t1.theme.LabelMedium
import com.example.t1.theme.LabelSmall
import com.example.t1.theme.MutedForeground
import com.example.t1.theme.SurfaceRaised
import com.example.t1.ui.viewmodel.MainViewModel
import com.example.t1.util.Haptics
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TokenWalletScreen(
    currentBalance: Int,
    lifetimeEarned: Int,
    lifetimeSpent: Int,
    transactions: List<com.example.t1.data.database.entity.TokenTransactionEntity>,
    onClose: () -> Unit,
    onOpenMerch: () -> Unit,
    viewModel: MainViewModel
) {
    val view = LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current

    // Infinite rotation for animated coin
    val infiniteTransition = rememberInfiniteTransition(label = "coin_rotation")
    val coinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coin_rotation_anim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B)) // Sleek dark zinc background
            .clickable(enabled = false) {} // block click propagation
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Header (Back Arrow + Title)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Card)
                        .border(1.dp, Border, CircleShape)
                        .clickable {
                            Haptics.playLight(view)
                            onClose()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Foreground,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Token Wallet",
                        style = HeadlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Foreground,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Your auditable reward balance.",
                        style = BodySmall.copy(color = MutedForeground, fontSize = 11.sp)
                    )
                }
            }

            // Scrollable sections
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. Balance Card (Header statistics)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Card, SurfaceRaised)
                            )
                        )
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Current Balance",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 11.sp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = com.example.t1.R.drawable.t1_token),
                                    contentDescription = "Tokens",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .graphicsLayer(rotationY = coinRotation)
                                )
                                Text(
                                    text = java.text.NumberFormat.getIntegerInstance().format(currentBalance),
                                    style = HeadlineMedium.copy(color = Foreground, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Lifetime Earned",
                                        style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp)
                                    )
                                    Text(
                                        text = "+${java.text.NumberFormat.getIntegerInstance().format(lifetimeEarned)}",
                                        style = LabelSmall.copy(color = GlowPrimary, fontWeight = FontWeight.Bold)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Lifetime Spent",
                                        style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp)
                                    )
                                    Text(
                                        text = "-${java.text.NumberFormat.getIntegerInstance().format(lifetimeSpent)}",
                                        style = LabelSmall.copy(color = Color.Red.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        // Big pulsing animated coin
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(GlowPrimary.copy(alpha = 0.05f))
                                .border(1.dp, GlowPrimary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = com.example.t1.R.drawable.t1_token),
                                contentDescription = "Pulsing Coin",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .graphicsLayer(rotationY = coinRotation)
                            )
                        }
                    }
                }

                // 3. Developer / User Simulation Panel
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Simulate Token Earning (Test Limits & Cooldowns)",
                        style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Card)
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Tap any reward to simulate positive behavior earning. Limits (e.g. Daily login limit, Focus session limit of 10/day) are fully enforced and will trigger safe rollback if violated.",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp),
                                lineHeight = 14.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Daily Login (+5)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Daily Streak (+10..40)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Focus Delta (+25..90)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Focus Session (+15..35)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("7d Streak (+150)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("21d Habit (+600)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                            }
                        }
                    }
                }

                // 4. Coming Soon Merchandise Card
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Exclusives & Merchandise Store",
                        style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Card)
                            .border(1.dp, Border, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "T1. Signature Oversized Tee",
                                    style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Unlock with 6,000 Tokens. Minimal identity.",
                                    style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(GlowPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, GlowPrimary.copy(alpha = 0.3f), CircleShape)
                                    .clickable {
                                        Haptics.playLight(view)
                                        onOpenMerch()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "3D Preview",
                                    style = LabelSmall.copy(color = GlowPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }

                // 5. Recent Transactions Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Recent Transactions",
                        style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                    if (transactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Card)
                                .border(1.dp, Border, RoundedCornerShape(12.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions found yet.",
                                style = BodySmall.copy(color = MutedForeground)
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            transactions.take(15).forEach { tx ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Card)
                                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = tx.reason.replace("_", " "),
                                                style = LabelSmall.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(tx.timestamp),
                                                    style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp)
                                                )
                                                // Sync tag
                                                val syncColor = if (tx.status == "SYNCED") GlowPrimary.copy(alpha = 0.7f) else Color.Yellow.copy(alpha = 0.7f)
                                                Text(
                                                    text = tx.status,
                                                    style = LabelSmall.copy(color = syncColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }

                                        val prefix = if (tx.amount > 0) "+" else ""
                                        val color = if (tx.amount > 0) GlowPrimary else Color.Red.copy(alpha = 0.8f)
                                        Text(
                                            text = "$prefix${tx.amount}",
                                            style = LabelMedium.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

