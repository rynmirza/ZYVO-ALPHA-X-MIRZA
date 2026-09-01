package com.example.zyvo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zyvo.model.UserProfile
import com.example.zyvo.model.VipTier
import com.example.zyvo.ui.theme.*

@Composable
fun UserProfileScreen(
    userProfile: UserProfile,
    coinBalance: Int,
    beansBalance: Int,
    followingCount: Int,
    onOpenVipStore: () -> Unit,
    onOpenRecharge: () -> Unit,
    onOpenWithdrawal: () -> Unit,
    onOpenTransactions: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenUserDetail: (String) -> Unit,
    onLogout: (() -> Unit)? = null
) {
    Scaffold(
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Top Settings / Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Profile & Wallet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onOpenAnalytics,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(DarkSurface)
                                .testTag("profile_analytics_btn")
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = "Analytics", tint = NeonPurpleLight)
                        }

                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(DarkSurface)
                                .testTag("profile_settings_btn")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                        }
                    }
                }
            }

            // User Header Profile Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2E1052), Color(0xFF140826))
                            )
                        )
                        .border(1.5.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(DarkBackground)
                                    .border(
                                        2.5.dp,
                                        if (userProfile.vipTier != VipTier.NONE) Color(android.graphics.Color.parseColor(userProfile.vipTier.avatarBorderColorHex))
                                        else NeonPurple,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = userProfile.avatarEmoji, fontSize = 38.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userProfile.displayName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )

                                    if (userProfile.vipTier != VipTier.NONE) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(GoldAccent)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = userProfile.vipTier.badge,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "@${userProfile.username} • ID: ${userProfile.userId}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeonCyan
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonPurpleDark)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "⭐ Lv.${userProfile.userLevel}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GoldAccent.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "💎 Wealth Lv.${userProfile.wealthLevel}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Level XP Progress Bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "User XP Progress", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(text = "${userProfile.userXp} / ${userProfile.nextLevelXp} XP", style = MaterialTheme.typography.labelSmall, color = NeonPurpleLight, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (userProfile.userXp.toFloat() / userProfile.nextLevelXp.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = NeonPurpleLight,
                                trackColor = DarkSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = userProfile.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface.copy(alpha = 0.6f))
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${userProfile.followersCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Followers", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$followingCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Following", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${userProfile.likesCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Likes", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${userProfile.diamondsEarnedTotal}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GoldAccent)
                                Text(text = "Diamonds", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }
                    }
                }
            }

            // VIP Membership Pass Banner Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4A0033), Color(0xFF1E0C36))
                            )
                        )
                        .border(1.5.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .clickable { onOpenVipStore() }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👑", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (userProfile.vipTier != VipTier.NONE) "Active ${userProfile.vipTier.displayName} Pass" else "Unlock VIP & SVIP Status",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Exclusive badges, entrance effects & 20% bonus rebates",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent
                                )
                            }
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = "VIP Store", tint = GoldAccent)
                    }
                }
            }

            // Wallet & Financial Center Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(1.dp, OverlayLight, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Zyvo Financial Center 💼",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Coins Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkCardElevated)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪙", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Coins Balance", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$coinBalance",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = GoldAccent
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onOpenRecharge,
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp)
                                        .testTag("wallet_recharge_btn")
                                ) {
                                    Text("Recharge", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                                }
                            }
                        }

                        // Host Earnings Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkCardElevated)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🫘", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Host Earnings", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$${String.format("%.2f", beansBalance / 100.0)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldGreen
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onOpenWithdrawal,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp)
                                        .testTag("wallet_withdraw_btn")
                                ) {
                                    Text("Cash Out", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Transactions History Action Button
                    OutlinedButton(
                        onClick = onOpenTransactions,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Full Transaction History", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }

            // Badges & Achievements Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(1.dp, OverlayLight, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Badges & Achievements 🏆",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(userProfile.badges) { badge ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NeonPurpleDark)
                                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🎖️", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = badge, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }

            item {
                if (onLogout != null) {
                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("logout_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(LiveRed, ElectricMagenta)))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Logout, contentDescription = "Log Out", tint = LiveRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Sign Out / Switch Account", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LiveRed)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
