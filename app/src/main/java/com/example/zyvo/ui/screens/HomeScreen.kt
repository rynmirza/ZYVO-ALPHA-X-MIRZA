package com.example.zyvo.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.zyvo.model.LiveRoom
import com.example.zyvo.model.RoomType
import com.example.zyvo.model.UserProfile
import com.example.zyvo.model.VipTier
import com.example.zyvo.ui.components.ExecutiveAvatar
import com.example.zyvo.ui.components.ExecutiveGrandBanner
import com.example.zyvo.ui.components.openWhatsAppChat
import com.example.zyvo.ui.theme.*

@Composable
fun HomeScreen(
    rooms: List<LiveRoom>,
    selectedCategory: RoomType?,
    searchQuery: String,
    coinBalance: Int,
    ceoProfile: UserProfile? = null,
    coFounderProfile: UserProfile? = null,
    ansharahProfile: UserProfile? = null,
    onSelectCategory: (RoomType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRoomClick: (LiveRoom) -> Unit,
    onGoLiveClick: () -> Unit,
    onOpenAnalyticsClick: () -> Unit,
    onOpenUserDetail: ((String) -> Unit)? = null
) {
    var selectedTopTab by remember { mutableStateOf(1) } // 0: Following, 1: Popular, 2: PK, 3: Audio, 4: New

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
                // Top App Bar with Zyvo Brand & Search icon
                HomeTopBar(
                    coinBalance = coinBalance,
                    onOpenAnalytics = onOpenAnalyticsClick
                )
            }

            // Top Category Nav Tabs (Following, Popular, PK, Audio, New + Search)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val categoryTabs = listOf("Following", "Popular", "PK", "Audio", "New")
                        items(categoryTabs.size) { index ->
                            val title = categoryTabs[index]
                            val isSelected = selectedTopTab == index
                            Column(
                                modifier = Modifier
                                    .clickable {
                                        selectedTopTab = index
                                        when (index) {
                                            0 -> onSelectCategory(null)
                                            1 -> onSelectCategory(RoomType.SINGLE_LIVE)
                                            2 -> onSelectCategory(RoomType.PK_BATTLE)
                                            3 -> onSelectCategory(RoomType.AUDIO_STAGE)
                                            4 -> onSelectCategory(RoomType.MULTI_GUEST)
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) TextPrimary else TextMuted
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(22.dp)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(ElectricMagenta)
                                    )
                                }
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { /* expand search */ }
                    )
                }
            }

            // High Impact CEO Rayan Mirza & Co-Founder Alpha Rajpoot Grand Showcase Banner
            item {
                ExecutiveGrandBanner(
                    ceoProfile = ceoProfile,
                    coFounderProfile = coFounderProfile,
                    onOpenProfile = { userId -> onOpenUserDetail?.invoke(userId) },
                    onOpenLiveRoom = { roomId ->
                        val room = rooms.find { it.id == roomId }
                        if (room != null) onRoomClick(room)
                    }
                )
            }

            // Hero Promo Banner (Exact visual style of reference layout)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF8A003D), Color(0xFF4A0033), Color(0xFF260538))
                            )
                        )
                        .border(1.5.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .clickable { onGoLiveClick() }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldAccent)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("OFFICIAL CONTEST", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "BE A STAR - BE ON TOP",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Stream now to win 500,000 Coins & SVIP 3 status",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldAccent
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 32.sp)
                        }
                    }
                }
            }

            // Popular Hosts Avatar Row
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hall of Fame & Popular Hosts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("👑", fontSize = 16.sp)
                        }

                        Text(
                            text = "View all >",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.clickable { onOpenAnalyticsClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // CEO Rayan Mirza (Rank #1)
                        item {
                            val context = LocalContext.current
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { onOpenUserDetail?.invoke("ceo_rayan") }
                            ) {
                                ExecutiveAvatar(
                                    avatarUrl = "https://cdn.phototourl.com/free/2026-09-01-f3e014af-6987-41b0-8bcf-732294379e68.png",
                                    avatarEmoji = "👑",
                                    size = 58.dp,
                                    userLevel = 99,
                                    vipTier = VipTier.VIP_9,
                                    showCrown = true,
                                    showLevelBadge = true
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "RAYAN MIRZA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldAccent,
                                    maxLines = 1
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "★ 99.9M",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366))
                                            .clickable {
                                                openWhatsAppChat(context, "+44 7868 713315", "RAYAN MIRZA")
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("💬", fontSize = 9.sp)
                                    }
                                }
                            }
                        }

                        // Co-Founder Alpha Rajpoot (Rank #2)
                        item {
                            val context = LocalContext.current
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { onOpenUserDetail?.invoke("co_founder_alpha") }
                            ) {
                                ExecutiveAvatar(
                                    avatarUrl = "https://cdn.phototourl.com/free/2026-09-01-4aa927e1-ee25-497a-ae9e-4201e9d81679.jpg",
                                    avatarEmoji = "🦁",
                                    size = 58.dp,
                                    userLevel = 99,
                                    vipTier = VipTier.VIP_9,
                                    showCrown = true,
                                    showLevelBadge = true
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ALPHA RAJPOOT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonCyan,
                                    maxLines = 1
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "★ 88.8M",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366))
                                            .clickable {
                                                openWhatsAppChat(context, "+447366 387620", "ALPHA RAJPOOT")
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("💬", fontSize = 9.sp)
                                    }
                                }
                            }
                        }

                        // Top Host Queen: Ansharah Gahni (Rank #3 / SVIP 7)
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { onOpenUserDetail?.invoke("ansharah_gahni") }
                            ) {
                                ExecutiveAvatar(
                                    avatarUrl = "https://mp3tourl.com/images/1788287833535-dc94ba6e-5e98-4349-b949-cd1521ff4618.jpg",
                                    avatarEmoji = "👸",
                                    size = 58.dp,
                                    userLevel = 89,
                                    vipTier = VipTier.SVIP_7,
                                    showCrown = true,
                                    showLevelBadge = true
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ANSHARAH GAHNI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFF00AA),
                                    maxLines = 1
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "★ 78.5M",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFF007A))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "SVIP 7",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        val popularHosts = listOf(
                            Triple("Elena 'PixelQueen'", "🎮", "69.0M"),
                            Triple("Apex Arenas", "⚔️", "52.0M"),
                            Triple("Kai Sterling", "🎧", "28.4M"),
                            Triple("Marcus Vance", "☕", "14.5M")
                        )

                        items(popularHosts) { (name, emoji, score) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(DarkSurface)
                                        .border(1.5.dp, GoldAccent.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 26.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "★ $score",
                                    fontSize = 9.sp,
                                    color = GoldAccent
                                )
                            }
                        }
                    }
                }
            }

            // Top Live Grid Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top Live Broadcasts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "${rooms.size} Active",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonCyan
                    )
                }
            }

            // 2x2 Top Live Cards Grid
            if (rooms.isNotEmpty()) {
                val gridRooms = rooms.take(6)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        gridRooms.chunked(2).forEach { rowRooms ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowRooms.forEach { room ->
                                    val isExecutive = room.creatorIdentity == "ceo_rayan" || room.creatorIdentity == "co_founder_alpha"
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(190.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    if (isExecutive) {
                                                        listOf(Color(0xFF42083D), DarkSurface)
                                                    } else {
                                                        listOf(Color(0xFF321252), DarkSurface)
                                                    }
                                                )
                                            )
                                            .border(
                                                if (isExecutive) 2.dp else 1.dp,
                                                if (isExecutive) GoldAccent else OverlayLight,
                                                RoundedCornerShape(18.dp)
                                            )
                                            .clickable { onRoomClick(room) }
                                            .padding(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Top Live Tag & Viewer Count
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isExecutive) GoldAccent else LiveRed)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = if (isExecutive) "👑 OFFICIAL" else "LIVE",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = if (isExecutive) Color.Black else TextPrimary
                                                    )
                                                }

                                                Text(
                                                    text = "👁️ ${room.viewerCount}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                            }

                                            // Center Host Avatar
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (!room.hostAvatarUrl.isNullOrBlank()) {
                                                    val context = LocalContext.current
                                                    AsyncImage(
                                                        model = ImageRequest.Builder(context)
                                                            .data(room.hostAvatarUrl)
                                                            .crossfade(true)
                                                            .build(),
                                                        contentDescription = room.hostName,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(54.dp)
                                                            .clip(CircleShape)
                                                            .border(2.dp, if (isExecutive) GoldAccent else NeonCyan, CircleShape)
                                                    )
                                                } else {
                                                    Text(text = room.hostAvatar, fontSize = 42.sp)
                                                }
                                            }

                                            // Bottom Details
                                            Column {
                                                Text(
                                                    text = room.hostName,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isExecutive) GoldAccent else TextPrimary,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = room.title,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextSecondary,
                                                    maxLines = 1
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

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun HomeTopBar(
    coinBalance: Int,
    onOpenAnalytics: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Zyvo Brand Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(NeonPurple, ElectricMagenta)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚡", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Zyvo Live",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Realtime WebRTC Studio",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
            }
        }

        // Right Actions: Coins Pill & Analytics Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Coin Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coinBalance",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
            }

            // Studio Analytics Button
            IconButton(
                onClick = onOpenAnalytics,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkSurface)
                    .testTag("home_analytics_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Studio Analytics",
                    tint = NeonPurpleLight
                )
            }
        }
    }
}

@Composable
fun CategoryChipsRow(
    selectedCategory: RoomType?,
    onSelectCategory: (RoomType?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // "All" chip
        item {
            val isSelected = selectedCategory == null
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) NeonPurple else DarkSurface)
                    .border(
                        1.dp,
                        if (isSelected) NeonCyan else OverlayLight,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectCategory(null) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "✨ Explore All",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) TextPrimary else TextSecondary
                )
            }
        }

        items(RoomType.entries.toTypedArray()) { type ->
            val isSelected = selectedCategory == type
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) NeonPurple else DarkSurface)
                    .border(
                        1.dp,
                        if (isSelected) NeonCyan else OverlayLight,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectCategory(type) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = type.icon, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = type.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) TextPrimary else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedStreamBanner(
    room: LiveRoom,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF381463),
                        Color(0xFF1E0A38),
                        DarkBackground
                    )
                )
            )
            .border(1.5.dp, NeonPurple.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Pulse Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LiveRed)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(TextPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "FEATURED ${room.roomType.badge}",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Viewer Count
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(OverlayBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Viewers",
                        tint = NeonCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${room.viewerCount} watching",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = room.hostAvatar, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = room.hostName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonPurpleLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${room.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun LiveRoomCard(
    room: LiveRoom,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, OverlayLight, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Host Avatar Tile with Live Ring
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCardElevated)
                    .border(1.5.dp, NeonPurple, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = room.hostAvatar, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.hostName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Mode Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (room.roomType) {
                                    RoomType.SINGLE_LIVE -> NeonPurpleDark
                                    RoomType.MULTI_GUEST -> CyberBlue.copy(alpha = 0.3f)
                                    RoomType.AUDIO_STAGE -> EmeraldGreen.copy(alpha = 0.3f)
                                    RoomType.PK_BATTLE -> PkRed.copy(alpha = 0.3f)
                                    RoomType.TEAM_MODE -> GoldAccent.copy(alpha = 0.3f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${room.roomType.icon} ${room.roomType.badge}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = room.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stats: Viewers, Likes, Tags
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${room.viewerCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = ElectricMagenta,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${room.likesCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (room.tags.isNotEmpty()) {
                        Text(
                            text = "#${room.tags.first()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPurpleLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyRoomsState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📡", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "No streams match your filter",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Try another category or start your own live stream studio!",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}
