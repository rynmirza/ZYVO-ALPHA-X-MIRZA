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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zyvo.model.LiveRoom
import com.example.zyvo.model.RoomType
import com.example.zyvo.ui.theme.*

@Composable
fun HomeScreen(
    rooms: List<LiveRoom>,
    selectedCategory: RoomType?,
    searchQuery: String,
    coinBalance: Int,
    onSelectCategory: (RoomType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRoomClick: (LiveRoom) -> Unit,
    onGoLiveClick: () -> Unit,
    onOpenAnalyticsClick: () -> Unit
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
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
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
                        Text(
                            text = "Popular Hosts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "View all >",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val popularHosts = listOf(
                            Triple("King Of King's", "👑", "127.5M"),
                            Triple("Drama Queen", "🦁", "98.7M"),
                            Triple("Jannatul Islam", "🌸", "75.2M"),
                            Triple("Husnat Smita", "🎤", "64.1M"),
                            Triple("Nusrat Jahan", "🌟", "52.9M")
                        )

                        items(popularHosts) { (name, emoji, score) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(DarkSurface)
                                        .border(2.dp, GoldAccent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 28.sp)
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
                        text = "Top Live",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "View all >",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // 2x2 Top Live Cards Grid
            if (rooms.isNotEmpty()) {
                val gridRooms = rooms.take(4)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        gridRooms.chunked(2).forEach { rowRooms ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowRooms.forEach { room ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color(0xFF321252), DarkSurface)
                                                )
                                            )
                                            .border(1.dp, OverlayLight, RoundedCornerShape(18.dp))
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
                                                        .background(LiveRed)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("LIVE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextPrimary)
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
                                                Text(text = room.hostAvatar, fontSize = 42.sp)
                                            }

                                            // Bottom Details
                                            Column {
                                                Text(
                                                    text = room.hostName,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
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
