package com.example.t1.ui.permission

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.R
import com.example.t1.util.Haptics

/**
 * High-fidelity Usage Permission Fallback Screen for Dashboard.
 * Matches Step 2 design from onboarding with 3D Mascot presenting the T1 Shield.
 */
@Composable
fun UsagePermissionScreen(
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF06040A),
                        Color(0xFF10091E),
                        Color(0xFF07040D)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.32f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.22f),
                        radius = size.width * 0.5f
                    )
                )
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF7C3AED).copy(alpha = 0.16f),
                            Color.Transparent
                        ),
                        startY = size.height * 0.88f,
                        endY = size.height
                    )
                )
            }
            .padding(horizontal = 22.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top row with "Skip →" capsule on top right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1B1629).copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF382D4D), RoundedCornerShape(20.dp))
                            .clickable {
                                Haptics.playLight(view)
                                onLater()
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Skip ➔",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                // Center Hero Graphic: Mascot + Neon Shield on Pedestal
                Image(
                    painter = painterResource(id = R.drawable.mascot_perm_usage),
                    contentDescription = "Mascot presenting T1 Shield",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(175.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Step pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.22f))
                        .border(1.dp, Color(0xFF818CF8).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✦  SYSTEM PERMISSION",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFC7D2FE),
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Headline
                Text(
                    text = "Why We Need",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Usage Access",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFA855F7), Color(0xFFE879F9))
                        )
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle explanation
                Text(
                    text = "T1 is a Sprint Analyzer, not a spy. We read only aggregate app usage and screen unlock counts on your device to compute your Focus Score, track daily screen time by category, and power a fair Focus Fight experience.",
                    fontSize = 12.5.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 17.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Frosted Card with 3 rows
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
                                    Color(0xFF3B1D6E).copy(alpha = 0.25f)
                                )
                            ),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        RowItem(
                            icon = Icons.Default.BarChart,
                            title = "What we read",
                            subtitle = "App category usage, screen time and unlock counts."
                        )
                        RowItem(
                            icon = Icons.Default.Bolt,
                            title = "How we use it",
                            subtitle = "To calculate your Focus Score, 7-day baseline, category breakdown and anti-cheat checks."
                        )
                        RowItem(
                            icon = Icons.Default.Security,
                            title = "What we never access",
                            subtitle = "Messages, photos, contacts, GPS, or any private account data."
                        )
                    }
                }
            }

            // Bottom CTA section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF6D28D9),
                                    Color(0xFF9333EA),
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFFD8B4FE).copy(alpha = 0.6f), RoundedCornerShape(27.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                Haptics.playMedium(view)
                                onOpenSettings()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Enable ➔",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Set up later in Settings",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                Haptics.playLight(view)
                                onLater()
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun RowItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color(0xFF22173D))
                .border(1.dp, Color(0xFF3B2766), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFC084FC),
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 15.sp
            )
        }
    }
}
