package com.example.zyvo.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.zyvo.model.UserProfile
import com.example.zyvo.model.VipTier
import com.example.zyvo.ui.components.ExecutiveAvatar
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
    val context = LocalContext.current
    var selectedBadgeDetail by remember { mutableStateOf<String?>(null) }

    // Shimmer & Aura Animations
    val infiniteTransition = rememberInfiniteTransition(label = "profile_anim")
    val haloRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "halo_rotate"
    )

    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    val primaryAccent = if (userProfile.vipTier != VipTier.NONE) GoldAccent else NeonCyan

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
                Spacer(modifier = Modifier.height(6.dp))
                // Top Settings / Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Profile & Wallet 👑",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Level ${userProfile.userLevel} Broadcaster",
                            fontSize = 11.sp,
                            color = primaryAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }

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

            // ==========================================
            // DELUXE PROFILE HEADER CARD WITH COVER & AVATAR
            // ==========================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF38084B),
                                    Color(0xFF1B0527),
                                    Color(0xFF100318)
                                )
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(primaryAccent, ElectricMagenta, primaryAccent)),
                            RoundedCornerShape(26.dp)
                        )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Dynamic Animated Cover Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(105.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF5A1478),
                                            Color(0xFF28073D),
                                            Color(0xFF160322)
                                        )
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "⚡ ZYVO LIVE CREATOR",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = primaryAccent
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .clickable {
                                            Toast.makeText(context, "Cover photo updated!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = "Edit Cover", tint = TextPrimary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit Cover", fontSize = 9.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Avatar & Profile Details Body
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            // Avatar Overlapping Cover
                            Row(
                                modifier = Modifier.offset(y = (-42).dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // Animated Avatar with Halo & Crown
                                Box(
                                    modifier = Modifier.size(86.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Halo Glow
                                    Box(
                                        modifier = Modifier
                                            .size(86.dp * auraPulse)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(primaryAccent.copy(alpha = 0.5f), Color.Transparent)
                                                )
                                            )
                                    )

                                    // Rotating Border
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .rotate(haloRotation)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.sweepGradient(
                                                    listOf(primaryAccent, ElectricMagenta, NeonCyan, primaryAccent)
                                                )
                                            )
                                    )

                                    // Avatar Core
                                    Box(
                                        modifier = Modifier
                                            .size(74.dp)
                                            .clip(CircleShape)
                                            .background(DarkBackground),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!userProfile.avatarUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(userProfile.avatarUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = userProfile.displayName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            Text(text = userProfile.avatarEmoji, fontSize = 38.sp)
                                        }
                                    }

                                    // VIP Crown
                                    if (userProfile.vipTier != VipTier.NONE) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .offset(y = (-8).dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(GoldAccent)
                                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text(text = "👑 ${userProfile.vipTier.badge}", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = userProfile.displayName,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(primaryAccent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = "Verified", tint = Color.Black, modifier = Modifier.size(12.dp))
                                        }
                                    }

                                    Text(
                                        text = "@${userProfile.username} • ID: ${userProfile.userId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height((-26).dp))

                            // Triple Prestige Badges Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NeonPurpleDark)
                                        .border(1.dp, NeonPurpleLight.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "⭐ Lv.${userProfile.userLevel}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF5A0835))
                                        .border(1.dp, ElectricMagenta.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "🎙️ Host Lv.${userProfile.hostLevel}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF80AB))
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF422B01))
                                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "💎 Wealth Lv.${userProfile.wealthLevel}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // XP Progress Bar
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "XP to Level ${userProfile.userLevel + 1}", fontSize = 10.sp, color = TextMuted)
                                    Text(text = "${userProfile.userXp} / ${userProfile.nextLevelXp} XP", fontSize = 10.sp, color = primaryAccent, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (userProfile.userXp.toFloat() / userProfile.nextLevelXp.toFloat()).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = primaryAccent,
                                    trackColor = DarkSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = userProfile.bio,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stats Grid
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSurface.copy(alpha = 0.8f))
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${userProfile.followersCount}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text(text = "Followers", fontSize = 10.sp, color = TextMuted)
                                }
                                Box(modifier = Modifier.height(24.dp).width(1.dp).background(OverlayLight))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "$followingCount", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text(text = "Following", fontSize = 10.sp, color = TextMuted)
                                }
                                Box(modifier = Modifier.height(24.dp).width(1.dp).background(OverlayLight))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${userProfile.likesCount}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text(text = "Likes", fontSize = 10.sp, color = TextMuted)
                                }
                                Box(modifier = Modifier.height(24.dp).width(1.dp).background(OverlayLight))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${userProfile.diamondsEarnedTotal}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = GoldAccent)
                                    Text(text = "💎 Wealth", fontSize = 10.sp, color = GoldAccent.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // VIP MEMBERSHIP PASS BANNER CARD
            // ==========================================
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
                        .border(1.5.dp, GoldAccent.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
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
                                    text = "Exclusive badge, animated halo & 20% bonus coins",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent
                                )
                            }
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = "VIP Store", tint = GoldAccent)
                    }
                }
            }

            // ==========================================
            // WALLET & FINANCIAL CENTER
            // ==========================================
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
                        // Coins Balance Card
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

            // ==========================================
            // BADGES & ACHIEVEMENTS SHOWCASE RACK
            // ==========================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(1.dp, OverlayLight, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Badges & Trophies 🏆",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${userProfile.badges.size} Unlocked",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(userProfile.badges) { badge ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF2A0845), Color(0xFF6441A5))
                                        )
                                    )
                                    .border(1.dp, primaryAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .clickable { selectedBadgeDetail = badge }
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

            // ==========================================
            // FOUNDERS & OFFICIAL CONTACTS SHORTCUT
            // ==========================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Official Founders & Executive Desk 👑",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // CEO Rayan Mirza
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .clickable { onOpenUserDetail("ceo_rayan") }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👑", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("RAYAN MIRZA (CEO & Founder)", fontWeight = FontWeight.Black, fontSize = 13.sp, color = GoldAccent)
                            Text("WhatsApp Desk: +44 7868 713315", fontSize = 10.sp, color = TextSecondary)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoldAccent)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Co-Founder Alpha Rajpoot
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .clickable { onOpenUserDetail("co_founder_alpha") }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🦁", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ALPHA RAJPOOT (Co-Founder)", fontWeight = FontWeight.Black, fontSize = 13.sp, color = ElectricMagenta)
                            Text("WhatsApp Desk: +447366 387620", fontSize = 10.sp, color = TextSecondary)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ElectricMagenta)
                    }
                }
            }

            // ==========================================
            // SIGN OUT BUTTON
            // ==========================================
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

        // BADGE DETAIL MODAL
        if (selectedBadgeDetail != null) {
            AlertDialog(
                onDismissRequest = { selectedBadgeDetail = null },
                containerColor = DarkSurface,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎖️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = selectedBadgeDetail!!, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(
                        text = "Official honorary insignia awarded for contribution, active broadcasting, and community leadership on ZYVO Live.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { selectedBadgeDetail = null },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                    ) {
                        Text("Got it", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
