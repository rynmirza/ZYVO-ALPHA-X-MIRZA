package com.example.zyvo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.zyvo.model.LiveRoom
import com.example.zyvo.model.RoomType
import com.example.zyvo.model.UserProfile
import com.example.zyvo.ui.theme.*

// Data Models for Home Page Reference Content
data class TopHostItem(
    val id: String,
    val name: String,
    val isVerified: Boolean = true,
    val category: String,
    val viewerCount: String,
    val imageUrl: String,
    val roomType: RoomType = RoomType.SINGLE_LIVE
)

data class PopularHostItem(
    val id: String,
    val name: String,
    val popularity: String,
    val imageUrl: String,
    val isGoldCrown: Boolean = true,
    val hasRose: Boolean = false
)

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
    // Reference Data items matching the design source of truth
    val topLiveHosts = remember {
        listOf(
            TopHostItem(
                id = "nusrat_jahan",
                name = "Nusrat Jahan",
                category = "❤️ Let's Talk",
                viewerCount = "12.5K",
                imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80"
            ),
            TopHostItem(
                id = "maisha",
                name = "Maisha",
                category = "🌊 Good Vibes ✨",
                viewerCount = "8.7K",
                imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80"
            ),
            TopHostItem(
                id = "ayesha_live",
                name = "Ayesha Live",
                category = "🎵 Music Live",
                viewerCount = "9.2K",
                imageUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80"
            ),
            TopHostItem(
                id = "cute_angel",
                name = "Cute Angel",
                category = "⭐ Happy Time",
                viewerCount = "7.1K",
                imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80"
            )
        )
    }

    val popularHosts = remember {
        listOf(
            PopularHostItem(
                id = "king_of_kings",
                name = "King Of King's",
                popularity = "127.5M",
                imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
                isGoldCrown = true
            ),
            PopularHostItem(
                id = "drama_queen",
                name = "Drama Queen",
                popularity = "98.7M",
                imageUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&auto=format&fit=crop&q=80",
                isGoldCrown = true
            ),
            PopularHostItem(
                id = "jannatul_islam",
                name = "Jannatul Islam",
                popularity = "75.2M",
                imageUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=500&auto=format&fit=crop&q=80",
                isGoldCrown = true
            ),
            PopularHostItem(
                id = "husnat_smita",
                name = "Husnat Smita",
                popularity = "64.1M",
                imageUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=500&auto=format&fit=crop&q=80",
                isGoldCrown = false
            ),
            PopularHostItem(
                id = "send_rose",
                name = "Send Rose",
                popularity = "58.3M",
                imageUrl = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=500&auto=format&fit=crop&q=80",
                isGoldCrown = true,
                hasRose = true
            )
        )
    }

    Scaffold(
        containerColor = Color(0xFF090712) // Deep space black/navy
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. TOP HEADER
            item {
                Spacer(modifier = Modifier.height(6.dp))
                HomeTopHeader(
                    onOpenAnalytics = onOpenAnalyticsClick
                )
            }

            // 2. HERO PROMOTIONAL BANNER
            item {
                HeroPromoBanner(onGoLiveClick = onGoLiveClick)
            }

            // 3. QUICK ACCESS HORIZONTAL CARD
            item {
                QuickAccessCard(
                    selectedCategory = selectedCategory,
                    onSelectCategory = onSelectCategory
                )
            }

            // 4. TOP LIVE SECTION
            item {
                SectionHeader(
                    icon = "🔥",
                    title = "Top Live",
                    onViewAllClick = { onSelectCategory(null) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Show reference items first or dynamic active rooms
                    items(topLiveHosts) { hostItem ->
                        TopLiveCard(
                            item = hostItem,
                            onClick = {
                                val matchRoom = rooms.firstOrNull()
                                if (matchRoom != null) {
                                    onRoomClick(matchRoom)
                                } else {
                                    onGoLiveClick()
                                }
                            }
                        )
                    }

                    // Dynamically append any active user rooms
                    items(rooms) { room ->
                        DynamicRoomCard(
                            room = room,
                            onClick = { onRoomClick(room) }
                        )
                    }
                }
            }

            // 5. POPULAR HOSTS SECTION
            item {
                SectionHeader(
                    icon = "👑",
                    title = "Popular Hosts",
                    onViewAllClick = { onOpenAnalyticsClick() }
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(popularHosts) { host ->
                        PopularHostCrownItem(
                            host = host,
                            onOpenUserDetail = onOpenUserDetail
                        )
                    }
                }
            }

            // 6. VIP PROMOTION BANNER
            item {
                VipUpgradeBanner(onUpgradeClick = onOpenAnalyticsClick)
            }

            // 7. FEATURE CARDS GRID (2x2)
            item {
                FeatureCardsGrid(
                    onSelectCategory = onSelectCategory,
                    onGoLiveClick = onGoLiveClick,
                    onOpenAnalyticsClick = onOpenAnalyticsClick
                )
            }

            // Bottom Spacing for Fixed Navigation Bar
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// --------------------------------------------------------------------------------
// 1. TOP HEADER COMPOSABLE
// --------------------------------------------------------------------------------
@Composable
fun HomeTopHeader(
    onOpenAnalytics: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ZYVO Logo & Tagline
        Column {
            Text(
                text = "ZYVO",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFFF007A), Color(0xFF9D4EDD), Color(0xFF00F0FF))
                    )
                ),
                letterSpacing = 1.sp
            )
            Text(
                text = "WATCH • CONNECT • SHINE",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 1.5.sp
            )
        }

        // Top-Right Action Icons (Search, Trophy, Notifications with badge)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderCircleIconButton(
                icon = Icons.Default.Search,
                contentDescription = "Search",
                onClick = { }
            )
            HeaderCircleIconButton(
                icon = Icons.Default.EmojiEvents,
                contentDescription = "Leaderboard",
                onClick = onOpenAnalytics
            )
            HeaderCircleIconButton(
                icon = Icons.Default.Notifications,
                contentDescription = "Notifications",
                hasBadge = true,
                onClick = { }
            )
        }
    }
}

@Composable
fun HeaderCircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    hasBadge: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF19142A))
            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        if (hasBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF007A))
                    .border(1.5.dp, Color(0xFF19142A), CircleShape)
            )
        }
    }
}

// --------------------------------------------------------------------------------
// 2. HERO PROMOTIONAL BANNER COMPOSABLE
// --------------------------------------------------------------------------------
@Composable
fun HeroPromoBanner(onGoLiveClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF260538),
                        Color(0xFF4C0E56),
                        Color(0xFF1C0738)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFF00AA).copy(alpha = 0.5f), Color(0xFF00F0FF).copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onGoLiveClick() }
    ) {
        // Starry night ambient canvas background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFFF00AA).copy(alpha = 0.15f),
                radius = size.width * 0.3f,
                center = Offset(size.width * 0.2f, size.height * 0.5f)
            )
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = 0.15f),
                radius = size.width * 0.3f,
                center = Offset(size.width * 0.85f, size.height * 0.5f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Mascot Graphic: Cartoon Mascot Cat holding pink camera
            MascotGraphic(modifier = Modifier.size(95.dp))

            // Center Text & CTA Button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BE A STAR",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "BE ON TOP",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Show your talent, get noticed\nand win amazing rewards!",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFD4C7FF),
                    textAlign = TextAlign.Center,
                    lineHeight = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF007A), Color(0xFF8A00D4))
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "JOIN NOW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            // Right Crown Trophy Visual with Golden Wings
            CrownTrophyGraphic(modifier = Modifier.size(90.dp))
        }

        // Carousel Indicator Dots at bottom center
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF007A))
            )
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }
        }
    }
}

@Composable
fun MascotGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glowing aura behind mascot
        Box(
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFFF00AA).copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        )
        // Cute cartoon mascot composite
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🐱", fontSize = 42.sp)
            Box(
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF007A), Color(0xFF9D4EDD))
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "LIVE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CrownTrophyGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Golden radiance aura
        Box(
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFFFD700).copy(alpha = 0.35f), Color.Transparent)
                    )
                )
        )
        // Golden Crown & Wings Composite
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "👑", fontSize = 44.sp)
            Text(
                text = "🏆",
                fontSize = 24.sp,
                modifier = Modifier.offset(y = (-14).dp)
            )
        }
    }
}

// --------------------------------------------------------------------------------
// 3. QUICK ACCESS HORIZONTAL CARD
// --------------------------------------------------------------------------------
@Composable
fun QuickAccessCard(
    selectedCategory: RoomType?,
    onSelectCategory: (RoomType?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF161224))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickAccessItem(
                icon = Icons.Default.Videocam,
                iconBgGradient = listOf(Color(0xFFFF007A), Color(0xFFFF529A)),
                label = "Top Live",
                isSelected = selectedCategory == null,
                onClick = { onSelectCategory(null) }
            )
            QuickAccessItem(
                icon = Icons.Default.Call,
                iconBgGradient = listOf(Color(0xFF7209B7), Color(0xFFB5179E)),
                label = "PK Battle",
                isSelected = selectedCategory == RoomType.PK_BATTLE,
                onClick = { onSelectCategory(RoomType.PK_BATTLE) }
            )
            QuickAccessItem(
                icon = Icons.Default.Mic,
                iconBgGradient = listOf(Color(0xFF3A0CA3), Color(0xFF480CA8)),
                label = "Audio Live",
                isSelected = selectedCategory == RoomType.AUDIO_STAGE,
                onClick = { onSelectCategory(RoomType.AUDIO_STAGE) }
            )
            QuickAccessItem(
                icon = Icons.Default.Star,
                iconBgGradient = listOf(Color(0xFF4361EE), Color(0xFF4CC9F0)),
                label = "New",
                isSelected = selectedCategory == RoomType.MULTI_GUEST,
                onClick = { onSelectCategory(RoomType.MULTI_GUEST) }
            )
        }
    }
}

@Composable
fun QuickAccessItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgGradient: List<Color>,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(iconBgGradient))
                .border(
                    width = if (isSelected) 1.5.dp else 0.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFFB0ACC0)
        )
    }
}

// --------------------------------------------------------------------------------
// SECTION HEADER COMPOSABLE
// --------------------------------------------------------------------------------
@Composable
fun SectionHeader(
    icon: String,
    title: String,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onViewAllClick() }
        ) {
            Text(
                text = "View all",
                fontSize = 12.sp,
                color = Color(0xFFA09BAC),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFA09BAC),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// --------------------------------------------------------------------------------
// 4. TOP LIVE HOST CARDS
// --------------------------------------------------------------------------------
@Composable
fun TopLiveCard(
    item: TopHostItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(135.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B162B))
        ) {
            // High quality portrait image
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x66000000),
                                Color.Transparent,
                                Color(0xDD090712)
                            )
                        )
                    )
            )

            // LIVE Pill Badge (Top Left)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF007A))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "LIVE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            // Viewers Pill Overlay (Bottom Left on image)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .align(Alignment.BottomStart)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = item.viewerCount,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Host Name + Verified Checkmark
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified",
                tint = Color(0xFF00F0FF),
                modifier = Modifier.size(12.dp)
            )
        }

        // Category Tag
        Text(
            text = item.category,
            fontSize = 10.sp,
            color = Color(0xFFB0ACC0),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DynamicRoomCard(
    room: LiveRoom,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val coverPic = room.roomCoverUrl ?: room.hostAvatarUrl
    Column(
        modifier = Modifier
            .width(135.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B162B))
        ) {
            if (!coverPic.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(coverPic)
                        .crossfade(true)
                        .build(),
                    contentDescription = room.hostName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF381463), Color(0xFF120524)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = room.hostAvatar, fontSize = 38.sp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(Color(0x66000000), Color.Transparent, Color(0xDD090712)))
                    )
            )

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF007A))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(text = "LIVE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .align(Alignment.BottomStart)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "${room.viewerCount}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = room.hostName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(3.dp))
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(12.dp))
        }
        Text(text = "#${room.category}", fontSize = 10.sp, color = Color(0xFFB0ACC0), maxLines = 1)
    }
}

// --------------------------------------------------------------------------------
// 5. POPULAR HOSTS CROWN AVATARS
// --------------------------------------------------------------------------------
@Composable
fun PopularHostCrownItem(
    host: PopularHostItem,
    onOpenUserDetail: ((String) -> Unit)?
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(82.dp)
            .clickable { onOpenUserDetail?.invoke(host.id) }
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Crown Frame Visual
            CrownFrameHeader(
                isGold = host.isGoldCrown,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-6).dp)
                    .zIndex(2f)
            )

            // Circular Host Avatar
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(host.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = host.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            if (host.isGoldCrown) listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                            else listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E))
                        ),
                        shape = CircleShape
                    )
            )

            // Rose Badge overlay if requested
            if (host.hasRose) {
                Text(
                    text = "🌹",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-12).dp)
                )
            }

            // Popularity Pill Badge Overlay at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF4361EE), Color(0xFF7209B7))
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = host.popularity,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = host.name,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CrownFrameHeader(isGold: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = if (isGold) "👑" else "👑",
        fontSize = 20.sp,
        modifier = modifier
    )
}

// --------------------------------------------------------------------------------
// 6. VIP PROMOTION BANNER
// --------------------------------------------------------------------------------
@Composable
fun VipUpgradeBanner(onUpgradeClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF22073A),
                        Color(0xFF420E64),
                        Color(0xFF1C0630)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFF00AA).copy(alpha = 0.5f), Color(0xFFFFD700).copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onUpgradeClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // VIP 7 Shield Emblem Visual
            VipShield7Emblem(modifier = Modifier.size(46.dp))

            Spacer(modifier = Modifier.width(10.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Upgrade to VIP",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Enjoy exclusive perks and rewards",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Upgrade Now Pill Button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF007A), Color(0xFF9D4EDD))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Upgrade Now",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VipShield7Emblem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Gold Shield Icon representation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFFF8C00))
                    )
                )
                .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "7",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4A0E00)
                )
            }
        }
    }
}

// --------------------------------------------------------------------------------
// 7. FEATURE CARDS GRID (2x2 GRID)
// --------------------------------------------------------------------------------
enum class FeatureVisualType {
    PK_BATTLE, GO_LIVE, TOP_GIFTING, DAILY_TASK
}

@Composable
fun FeatureCardsGrid(
    onSelectCategory: (RoomType?) -> Unit,
    onGoLiveClick: () -> Unit,
    onOpenAnalyticsClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Row 1: PK Battle & Go Live
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                modifier = Modifier.weight(1f),
                title = "PK Battle",
                subtitle = "Live Competition",
                buttonText = "Join Now",
                cardGradient = listOf(Color(0xFF1E0A38), Color(0xFF3C0E5A)),
                buttonGradient = listOf(Color(0xFF4361EE), Color(0xFF3A0CA3)),
                visualType = FeatureVisualType.PK_BATTLE,
                onClick = { onSelectCategory(RoomType.PK_BATTLE) }
            )

            FeatureCard(
                modifier = Modifier.weight(1f),
                title = "Go Live",
                subtitle = "Share your talent",
                buttonText = "Start Live",
                cardGradient = listOf(Color(0xFF2E083D), Color(0xFF5A0C6B)),
                buttonGradient = listOf(Color(0xFF7209B7), Color(0xFFFF007A)),
                visualType = FeatureVisualType.GO_LIVE,
                onClick = onGoLiveClick
            )
        }

        // Row 2: Top Gifting & Daily Task
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                modifier = Modifier.weight(1f),
                title = "Top Gifting",
                subtitle = "Support your favorite",
                buttonText = "Send Gift",
                cardGradient = listOf(Color(0xFF2A0930), Color(0xFF4A0A48)),
                buttonGradient = listOf(Color(0xFFFF007A), Color(0xFF7209B7)),
                visualType = FeatureVisualType.TOP_GIFTING,
                onClick = onOpenAnalyticsClick
            )

            FeatureCard(
                modifier = Modifier.weight(1f),
                title = "Daily Task",
                subtitle = "Complete & Earn",
                buttonText = "Check Now",
                cardGradient = listOf(Color(0xFF1F083B), Color(0xFF3E0F66)),
                buttonGradient = listOf(Color(0xFF7209B7), Color(0xFF4361EE)),
                visualType = FeatureVisualType.DAILY_TASK,
                onClick = onOpenAnalyticsClick
            )
        }
    }
}

@Composable
fun FeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    buttonText: String,
    cardGradient: List<Color>,
    buttonGradient: List<Color>,
    visualType: FeatureVisualType,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(cardGradient))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Text & Button Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(buttonGradient))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            // Right Visual Graphic according to feature card type
            Box(
                modifier = Modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                when (visualType) {
                    FeatureVisualType.PK_BATTLE -> PkBattleVisualGraphic()
                    FeatureVisualType.GO_LIVE -> GoLiveVisualGraphic()
                    FeatureVisualType.TOP_GIFTING -> TopGiftingVisualGraphic()
                    FeatureVisualType.DAILY_TASK -> DailyTaskVisualGraphic()
                }
            }
        }
    }
}

@Composable
fun PkBattleVisualGraphic() {
    Box(contentAlignment = Alignment.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
            Text("👩🏻", fontSize = 28.sp)
            Text("👩🏽", fontSize = 28.sp)
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFFFF007A))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text("VS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
fun GoLiveVisualGraphic() {
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFF007A), Color(0xFF7209B7))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TopGiftingVisualGraphic() {
    Box(contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎁", fontSize = 32.sp)
            Text("💕", fontSize = 10.sp, modifier = Modifier.offset(y = (-6).dp))
        }
    }
}

@Composable
fun DailyTaskVisualGraphic() {
    Box(contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📋", fontSize = 30.sp)
            Text("🪙", fontSize = 12.sp, modifier = Modifier.offset(y = (-6).dp))
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
