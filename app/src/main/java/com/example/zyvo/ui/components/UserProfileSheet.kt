package com.example.zyvo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zyvo.model.UserProfile
import com.example.zyvo.model.VipTier
import com.example.zyvo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileSheet(
    user: UserProfile,
    isSelf: Boolean = false,
    onDismiss: () -> Unit,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onOpenDm: () -> Unit,
    onBlock: () -> Unit,
    onReport: () -> Unit,
    onJoinLive: ((String) -> Unit)? = null,
    onOpenUserDetail: ((String) -> Unit)? = null
) {
    var showReportDialog by remember { mutableStateOf(false) }

    fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format(java.util.Locale.US, "%.2fM", count / 1_000_000.0)
            count >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", count / 1_000.0)
            else -> count.toString()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        scrimColor = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("user_profile_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Banner & Avatar Stack
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF4A148C),
                                Color(0xFF7B1FA2),
                                Color(0xFF311B92)
                            )
                        )
                    ),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { showReportDialog = true },
                    modifier = Modifier.padding(6.dp)
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextPrimary)
                }
            }

            // Avatar floating overlapping the banner
            Box(
                modifier = Modifier.offset(y = (-45).dp),
                contentAlignment = Alignment.Center
            ) {
                if (user.vipTier == VipTier.VIP_9 || !user.avatarUrl.isNullOrBlank() || user.userLevel >= 99) {
                    ExecutiveAvatar(
                        avatarUrl = user.avatarUrl,
                        avatarEmoji = user.avatarEmoji,
                        size = 86.dp,
                        userLevel = user.userLevel,
                        vipTier = user.vipTier,
                        showCrown = user.vipTier != VipTier.NONE,
                        showLevelBadge = false
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(DarkBackground)
                            .border(
                                3.dp,
                                if (user.vipTier != VipTier.NONE) Color(android.graphics.Color.parseColor(user.vipTier.avatarBorderColorHex))
                                else NeonPurple,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = user.avatarEmoji, fontSize = 46.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height((-35).dp))

            // Executive Tag if present
            if (user.executiveRole != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(GoldAccent, Color(0xFFFF007A))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "⭐ ${user.executiveRole} ⭐",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Name & VIP Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )

                if (user.vipTier != VipTier.NONE) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldAccent)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = user.vipTier.badge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "@${user.username} • ID: ${user.userId}",
                style = MaterialTheme.typography.bodyMedium,
                color = NeonCyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Level Badges Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonPurpleDark)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⭐ Lv. ${user.userLevel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Host Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElectricMagenta.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🎙️ Host Lv. ${user.hostLevel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricMagenta
                    )
                }

                // Wealth Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldAccent.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "💎 Wealth Lv. ${user.wealthLevel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bio
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row: Followers, Following, Likes, Receiving
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkCardElevated)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCount(user.followersCount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(text = "Followers", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }

                Divider(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp),
                    color = OverlayLight
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.followingCount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(text = "Following", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }

                Divider(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp),
                    color = OverlayLight
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCount(user.likesCount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(text = "Likes", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }

                Divider(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp),
                    color = OverlayLight
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCount(user.diamondsEarnedTotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Text(text = "Receiving", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }

            // Top Host Heavy Receiving Showcase Card
            if (user.userId == "ansharah_gahni" || user.isTopHost) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFF007A).copy(alpha = 0.2f),
                                    GoldAccent.copy(alpha = 0.25f),
                                    NeonPurpleDark
                                )
                            )
                        )
                        .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "💎 HEAVY RECEIVING TOP HOST",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldAccent
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("👑", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "78.5M+ Diamonds Earned • 75,800 Gifts • SVIP 7 QUEEN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldAccent)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Lv.89 ID",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Exclusive Following Section (If user follows only Founder & Co-Founder)
            if (user.userId == "ansharah_gahni" || user.followingUserIds.isNotEmpty()) {
                val context = LocalContext.current
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCardElevated)
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Following (${user.followingCount})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• Founder & Co-Founder Only",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Text("👑", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Founder Item: CEO Rayan Mirza
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .clickable {
                                onDismiss()
                                onOpenUserDetail?.invoke("ceo_rayan")
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExecutiveAvatar(
                            avatarUrl = "https://cdn.phototourl.com/free/2026-09-01-f3e014af-6987-41b0-8bcf-732294379e68.png",
                            avatarEmoji = "👑",
                            size = 40.dp,
                            userLevel = 99,
                            vipTier = VipTier.VIP_9,
                            showCrown = false,
                            showLevelBadge = false
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "RAYAN MIRZA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = GoldAccent
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("👑", fontSize = 11.sp)
                            }
                            Text(
                                text = "Founder & CEO • Lv.99 • VIP 9",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366))
                                .clickable {
                                    openWhatsAppChat(context, "+44 7868 713315", "RAYAN MIRZA")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💬", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Co-Founder Item: Alpha Rajpoot
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .clickable {
                                onDismiss()
                                onOpenUserDetail?.invoke("co_founder_alpha")
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExecutiveAvatar(
                            avatarUrl = "https://cdn.phototourl.com/free/2026-09-01-4aa927e1-ee25-497a-ae9e-4201e9d81679.jpg",
                            avatarEmoji = "🦁",
                            size = 40.dp,
                            userLevel = 99,
                            vipTier = VipTier.VIP_9,
                            showCrown = false,
                            showLevelBadge = false
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ALPHA RAJPOOT",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ElectricMagenta
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("🦁", fontSize = 11.sp)
                            }
                            Text(
                                text = "Co-Founder & Exec Director • Lv.99 • VIP 9",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366))
                                .clickable {
                                    openWhatsAppChat(context, "+447366 387620", "ALPHA RAJPOOT")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💬", fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Top Fans Section (Podium layout from reference image)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkCardElevated.copy(alpha = 0.6f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top Fans",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = TextMuted, modifier = Modifier.size(18.dp))
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val fans = listOf(
                        Triple("👑 1", "👑", "King Of Kings"),
                        Triple("🥈 2", "🦁", "Drama Queen"),
                        Triple("🥉 3", "🎧", "DJ Kai"),
                        Triple("4", "👾", "Pixel Queen")
                    )

                    fans.forEach { (rank, emoji, name) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(DarkBackground)
                                    .border(
                                        1.5.dp,
                                        if (rank.contains("1")) GoldAccent else NeonPurple,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = name, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live status banner if host is live right now
            if (user.isLiveNow && user.currentRoomId != null && onJoinLive != null) {
                Button(
                    onClick = {
                        onDismiss()
                        onJoinLive(user.currentRoomId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔴 LIVE NOW — Tap to Watch", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            // Action Buttons Row (Follow / Unfollow, Direct Message, Share)
            if (!isSelf) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Follow / Unfollow
                    Button(
                        onClick = {
                            if (user.isFollowedByCurrentUser) onUnfollow() else onFollow()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isFollowedByCurrentUser) DarkCardElevated else NeonPurple
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_sheet_follow_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (user.isFollowedByCurrentUser) Icons.Default.Check else Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (user.isFollowedByCurrentUser) "Following" else "Follow",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Direct Message Button
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenDm()
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NeonCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_sheet_dm_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Message", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }

                // WhatsApp Direct Contact Button for Executive Profiles
                if (user.whatsappNumber != null) {
                    val context = LocalContext.current
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            openWhatsAppChat(context, user.whatsappNumber, user.displayName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("whatsapp_executive_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "💬", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WhatsApp Direct: ${user.whatsappNumber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Report & Block Dialog
        if (showReportDialog) {
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                containerColor = DarkSurface,
                title = { Text(text = "User Options", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                onReport()
                                showReportDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Flag, contentDescription = null, tint = LiveRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Report User for Violations", color = LiveRed)
                            }
                        }
                        TextButton(
                            onClick = {
                                onBlock()
                                showReportDialog = false
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Block, contentDescription = null, tint = TextMuted)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Block @${user.username}", color = TextPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showReportDialog = false }) {
                        Text("Cancel", color = NeonCyan)
                    }
                }
            )
        }
    }
}
