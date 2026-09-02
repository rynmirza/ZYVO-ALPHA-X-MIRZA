package com.example.zyvo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.zyvo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ==========================================
// Models
// ==========================================
enum class RoomType {
    SINGLE, PK, MULTI, AUDIO
}

data class LiveRoom(
    val id: String,
    val title: String,
    val description: String,
    val hostName: String,
    val type: RoomType,
    val viewersCount: Int,
    val hostAvatar: String,
    val hostEmoji: String,
    val initialGems: Int = 125000
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val senderEmoji: String,
    val text: String,
    val isSystem: Boolean = false,
    val isVIP: Boolean = false
)

data class GiftItem(
    val id: String,
    val name: String,
    val emoji: String,
    val costCoins: Int,
    val animType: String
)

data class RechargePack(
    val id: String,
    val gemsAmount: Int,
    val priceUsd: String,
    val isPopular: Boolean = false
)

// ==========================================
// ViewModel
// ==========================================
class LiveStreamViewModel : ViewModel() {
    val gemsIconUrl = "https://i.ibb.co/9FGn8GZ/file-00000000b09c821183d31e687671ec03.png"

    // App state
    private val _currentScreen = MutableStateFlow("explore")
    val currentScreen: StateFlow<String> = _currentScreen

    private val _userGems = MutableStateFlow(12568)
    val userGems: StateFlow<Int> = _userGems

    private val _userCoins = MutableStateFlow(125680)
    val userCoins: StateFlow<Int> = _userCoins

    private val _activeRoom = MutableStateFlow<LiveRoom?>(null)
    val activeRoom: StateFlow<LiveRoom?> = _activeRoom

    // Rooms
    private val _rooms = MutableStateFlow<List<LiveRoom>>(emptyList())
    val rooms: StateFlow<List<LiveRoom>> = _rooms

    // Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    // PK Battle stats
    private val _pkAlphaScore = MutableStateFlow(5000)
    val pkAlphaScore: StateFlow<Int> = _pkAlphaScore

    private val _pkBetaScore = MutableStateFlow(4800)
    val pkBetaScore: StateFlow<Int> = _pkBetaScore

    // Room stats
    private val _roomGems = MutableStateFlow(0)
    val roomGems: StateFlow<Int> = _roomGems

    // Selected filter in Explore
    private val _exploreFilter = MutableStateFlow<RoomType?>(null)
    val exploreFilter: StateFlow<RoomType?> = _exploreFilter

    // Controls
    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted

    private val _isCameraOn = MutableStateFlow(true)
    val isCameraOn: StateFlow<Boolean> = _isCameraOn

    private val _beautyFilterSelected = MutableStateFlow("None")
    val beautyFilterSelected: StateFlow<String> = _beautyFilterSelected

    // Flying animations trigger
    private val _giftAnimationTrigger = MutableStateFlow<String?>(null)
    val giftAnimationTrigger: StateFlow<String?> = _giftAnimationTrigger

    init {
        loadRooms()
    }

    private fun loadRooms() {
        _rooms.value = listOf(
            LiveRoom(
                id = "room_1",
                title = "✨ TOP HOST QUEEN Live & 50M Gem Gala 👑",
                description = "Welcome to the elite Gem stage! Dropping massive rewards for supporters.",
                hostName = "Ansharah Gahni",
                type = RoomType.PK,
                viewersCount = 42500,
                hostAvatar = "https://cdn.pixabay.com/photo/2018/01/06/09/25/hijab-3064633_1280.jpg",
                hostEmoji = "👸",
                initialGems = 485000
            ),
            LiveRoom(
                id = "room_2",
                title = "⚔️ EXECUTIVE PK BATTLE Royale | 10M Gems Drops",
                description = "High-stakes executive PK battle! Support team ALPHA or team BETA!",
                hostName = "Alpha Rajpoot",
                type = RoomType.PK,
                viewersCount = 89000,
                hostAvatar = "https://cdn.pixabay.com/photo/2016/11/18/19/07/man-1836437_1280.jpg",
                hostEmoji = "🦁",
                initialGems = 950000
            ),
            LiveRoom(
                id = "room_3",
                title = "👥 Global Multi-Guest Video Lounge 🌟",
                description = "Live video discussions on the future of gaming, web3, and live content.",
                hostName = "Elena 'PixelQueen'",
                type = RoomType.MULTI,
                viewersCount = 12300,
                hostAvatar = "https://cdn.pixabay.com/photo/2017/08/30/12/45/girl-2696947_1280.jpg",
                hostEmoji = "👾",
                initialGems = 45000
            ),
            LiveRoom(
                id = "room_4",
                title = "🎙️ Cozy Zen Music Cafe | Audio Stage with VIP Seats ☕",
                description = "Sit back, grab a coffee, request a seat, and share your acoustic covers.",
                hostName = "Kai Sterling",
                type = RoomType.AUDIO,
                viewersCount = 8500,
                hostAvatar = "https://cdn.pixabay.com/photo/2015/07/30/17/24/audience-868074_1280.jpg",
                hostEmoji = "🎧",
                initialGems = 23000
            ),
            LiveRoom(
                id = "room_5",
                title = "⭐ Single Live Sovereign Stream with Rayan Mirza (CEO)",
                description = "Q&A session with the Zyvo Executive Leadership. Talking global expansion.",
                hostName = "Rayan Mirza",
                type = RoomType.SINGLE,
                viewersCount = 95400,
                hostAvatar = "https://cdn.pixabay.com/photo/2020/07/08/18/04/man-5384666_1280.jpg",
                hostEmoji = "👑",
                initialGems = 999900
            )
        )
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun setExploreFilter(type: RoomType?) {
        _exploreFilter.value = type
    }

    fun enterRoom(room: LiveRoom) {
        _activeRoom.value = room
        _roomGems.value = room.initialGems
        _isMuted.value = false
        _isCameraOn.value = true
        _beautyFilterSelected.value = "None"
        _currentScreen.value = "stream_room"

        // Initialize chat
        _chatMessages.value = listOf(
            ChatMessage("sys1", "ZYVO HQ", "🔮", "Welcome to ${room.hostName}'s Official Live! Active SVIP privilege active.", isSystem = true),
            ChatMessage("sys2", "Rayan Mirza (CEO)", "👑", "Welcome everyone! Keep shining and dropping those beautiful Gems! 🔮", isVIP = true),
            ChatMessage("c1", "CrownPrince_99", "👑", "Team Alpha is unbeatable tonight! Let's go! 🔥"),
            ChatMessage("c2", "Drama Queen", "💅", "Sent a Luxury Gem Ring to support the host! ❤️")
        )
    }

    fun leaveRoom() {
        _activeRoom.value = null
        _currentScreen.value = "explore"
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = "Alex Vance (You)",
            senderEmoji = "🚀",
            text = text,
            isVIP = true
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }

    fun sendGift(gift: GiftItem) {
        if (_userCoins.value >= gift.costCoins) {
            _userCoins.value -= gift.costCoins
            _roomGems.value += gift.costCoins / 10 // conversion rate

            val systemMsg = ChatMessage(
                id = "gift_${System.currentTimeMillis()}",
                senderName = "Alex Vance (You)",
                senderEmoji = "🚀",
                text = "sent a ${gift.name} ${gift.emoji} (Gems power-up!)",
                isSystem = true
            )
            _chatMessages.value = _chatMessages.value + systemMsg

            // Trigger animation
            _giftAnimationTrigger.value = gift.animType
        }
    }

    fun clearGiftAnimation() {
        _giftAnimationTrigger.value = null
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleCamera() {
        _isCameraOn.value = !_isCameraOn.value
    }

    fun setBeautyFilter(filter: String) {
        _beautyFilterSelected.value = filter
    }

    fun supportPK(team: String) {
        if (team == "alpha") {
            _pkAlphaScore.value += 1500
            _roomGems.value += 150
            _giftAnimationTrigger.value = "sparkle"
        } else {
            _pkBetaScore.value += 1500
            _roomGems.value += 150
            _giftAnimationTrigger.value = "burst"
        }
    }

    fun buyGems(pack: RechargePack) {
        _userGems.value += pack.gemsAmount
    }
}

// ==========================================
// MainActivity Entry Point
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZyvoLiveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LiveStreamViewModel = viewModel()
                    MainLayout(viewModel)
                }
            }
        }
    }
}

// ==========================================
// Main Scaffold Layout
// ==========================================
@Composable
fun MainLayout(viewModel: LiveStreamViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeRoom by viewModel.activeRoom.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Screen area (Without bottom nav when inside a Stream Room to give cinematic experience)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentScreen) {
                    "explore" -> ExploreScreen(viewModel)
                    "stream_room" -> activeRoom?.let { StreamRoomScreen(it, viewModel) }
                    "wallet" -> WalletScreen(viewModel)
                    "studio" -> StudioAnalyticsScreen(viewModel)
                    "profile" -> ProfileScreen(viewModel)
                    else -> ExploreScreen(viewModel)
                }
            }

            // Bottom Navigation (Only visible when NOT inside a stream room)
            if (currentScreen != "stream_room") {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextLight,
                    modifier = Modifier.height(72.dp)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "explore",
                        onClick = { viewModel.setScreen("explore") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Explore") },
                        label = { Text("Explore", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricMagenta,
                            selectedTextColor = ElectricMagenta,
                            indicatorColor = CardSurface,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray
                        ),
                        modifier = Modifier.testTag("nav_explore")
                    )
                    NavigationBarItem(
                        selected = currentScreen == "wallet",
                        onClick = { viewModel.setScreen("wallet") },
                        icon = {
                            Box(modifier = Modifier.size(24.dp)) {
                                AsyncImage(
                                    model = viewModel.gemsIconUrl,
                                    contentDescription = "Gems",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        },
                        label = { Text("Wallet", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GoldAccent,
                            selectedTextColor = GoldAccent,
                            indicatorColor = CardSurface,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray
                        ),
                        modifier = Modifier.testTag("nav_wallet")
                    )
                    NavigationBarItem(
                        selected = currentScreen == "studio",
                        onClick = { viewModel.setScreen("studio") },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Studio") },
                        label = { Text("Studio", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricMagenta,
                            selectedTextColor = ElectricMagenta,
                            indicatorColor = CardSurface,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray
                        ),
                        modifier = Modifier.testTag("nav_studio")
                    )
                    NavigationBarItem(
                        selected = currentScreen == "profile",
                        onClick = { viewModel.setScreen("profile") },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricMagenta,
                            selectedTextColor = ElectricMagenta,
                            indicatorColor = CardSurface,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    }
}

// ==========================================
// Explore / Home Screen
// ==========================================
@Composable
fun ExploreScreen(viewModel: LiveStreamViewModel) {
    val rooms by viewModel.rooms.collectAsState()
    val activeFilter by viewModel.exploreFilter.collectAsState()

    val filteredRooms = remember(rooms, activeFilter) {
        if (activeFilter == null) rooms else rooms.filter { it.type == activeFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVioletBg)
            .padding(top = 16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ZYVO LIVE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricMagenta,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Real-time Creator Sovereignty Stage",
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

            // Quick coins balance display
            val coinBalance by viewModel.userCoins.collectAsState()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(CardSurface, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "🪙 ", fontSize = 14.sp)
                Text(
                    text = "%,d".format(coinBalance),
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Mode Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterBadge(label = "All Live", isSelected = activeFilter == null) {
                viewModel.setExploreFilter(null)
            }
            FilterBadge(label = "⚔️ PK Battle", isSelected = activeFilter == RoomType.PK) {
                viewModel.setExploreFilter(RoomType.PK)
            }
            FilterBadge(label = "👥 Multi-Guest", isSelected = activeFilter == RoomType.MULTI) {
                viewModel.setExploreFilter(RoomType.MULTI)
            }
            FilterBadge(label = "🎙️ Audio Stage", isSelected = activeFilter == RoomType.AUDIO) {
                viewModel.setExploreFilter(RoomType.AUDIO)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Rooms list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("explore_rooms_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredRooms) { room ->
                LiveRoomCard(room) {
                    viewModel.enterRoom(room)
                }
            }
        }
    }
}

@Composable
fun FilterBadge(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) ElectricMagenta else CardSurface,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                if (isSelected) Color.Transparent else TextGray.copy(alpha = 0.3f),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LiveRoomCard(room: LiveRoom, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // Placeholder background for simulation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CardSurface,
                            DeepVioletBg
                        )
                    )
                )
        )

        // Stream visual overlay representing active video feed
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = ElectricMagenta.copy(alpha = 0.08f),
                radius = 200.dp.toPx(),
                center = Offset(size.width, size.height / 2f)
            )
            drawCircle(
                color = GoldAccent.copy(alpha = 0.05f),
                radius = 120.dp.toPx(),
                center = Offset(0f, size.height)
            )
        }

        // Room content info
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(ElectricMagenta, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.White, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Viewers count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Viewers",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%,d".format(room.viewersCount),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                Text(
                    text = room.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.description,
                    color = TextGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Creator Host info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Host profile avatar fallback or loaded
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, GoldAccent, CircleShape)
                    ) {
                        AsyncImage(
                            model = room.hostAvatar,
                            contentDescription = "Host Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = room.hostName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(text = room.hostEmoji, fontSize = 12.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    // Type Indicator Badge
                    Text(
                        text = when (room.type) {
                            RoomType.SINGLE -> "Single Stream"
                            RoomType.PK -> "⚔️ PK Battle"
                            RoomType.MULTI -> "👥 Multi-Guest"
                            RoomType.AUDIO -> "🎙️ Audio Stage"
                        },
                        color = GoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(GoldAccent.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// Interactive Live Stream Room Screen
// ==========================================
@Composable
fun StreamRoomScreen(room: LiveRoom, viewModel: LiveStreamViewModel) {
    val roomGems by viewModel.roomGems.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isCameraOn by viewModel.isCameraOn.collectAsState()
    val beautyFilterSelected by viewModel.beautyFilterSelected.collectAsState()
    val giftAnimationTrigger by viewModel.giftAnimationTrigger.collectAsState()

    var showGiftDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showHostProfileDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVioletBg)
    ) {
        // Core Web Camera Feed Simulation
        Box(modifier = Modifier.fillMaxSize()) {
            if (isCameraOn) {
                // Animated space particle canvas to look like active live stream stream
                CameraStreamSimulation(beautyFilterSelected)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Camera off",
                            tint = TextGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Camera is turned off", color = TextGray, fontSize = 14.sp)
                    }
                }
            }
        }

        // Top Header Info Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Host profile card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable { showHostProfileDialog = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(1.dp, GoldAccent, CircleShape)
                ) {
                    AsyncImage(
                        model = room.hostAvatar,
                        contentDescription = "Host Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(room.hostName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(ElectricMagenta, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("%,d".format(room.viewersCount), color = TextLight, fontSize = 9.sp)
                    }
                }
            }

            // GEMS received indicators (Dynamic state with custom icon!)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Box(modifier = Modifier.size(16.dp)) {
                    AsyncImage(
                        model = viewModel.gemsIconUrl,
                        contentDescription = "Gems Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "%,d".format(roomGems),
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Leave button
            IconButton(
                onClick = { viewModel.leaveRoom() },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .size(36.dp)
                    .testTag("leave_room_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Leave", tint = Color.White)
            }
        }

        // Mid area depending on room type
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.Center)
        ) {
            when (room.type) {
                RoomType.PK -> PKBattleArea(viewModel)
                RoomType.MULTI -> MultiGuestGrid()
                RoomType.AUDIO -> AudioStageSeats()
                RoomType.SINGLE -> { /* Single Host cinematic is background simulation */ }
            }
        }

        // Live Chat Overlays & Send Message block at the bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Scrolling chat messages
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(chatMessages) { message ->
                        ChatBubble(message)
                    }
                }
            }

            // Keyboard controller & input
            var textInput by remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Text Input
                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Say something nice...", color = TextGray, fontSize = 12.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardSurface.copy(alpha = 0.6f),
                        unfocusedContainerColor = CardSurface.copy(alpha = 0.6f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("chat_input"),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                            keyboardController?.hide()
                        }
                    }),
                    singleLine = true
                )

                // Mute, Beauty Filters, and Gift floating action buttons
                IconButton(
                    onClick = { viewModel.toggleMute() },
                    modifier = Modifier
                        .background(if (isMuted) ElectricMagenta else CardSurface, CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        if (isMuted) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = "Mute",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier
                        .background(CardSurface, CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Beauty",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { showGiftDialog = true },
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(colors = listOf(ElectricMagenta, GoldAccent)),
                            CircleShape
                        )
                        .size(44.dp)
                        .testTag("gift_button")
                ) {
                    Box(modifier = Modifier.size(24.dp)) {
                        AsyncImage(
                            model = viewModel.gemsIconUrl,
                            contentDescription = "Send Gift",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Particle / Flying animations
        GiftAnimationLayer(giftAnimationTrigger, viewModel)

        // Gift Dialog overlay
        if (showGiftDialog) {
            GiftSelectionDialog(viewModel) {
                showGiftDialog = false
            }
        }

        // Beauty Filter dialog
        if (showFilterDialog) {
            BeautyFilterDialog(viewModel) {
                showFilterDialog = false
            }
        }

        // Host Profile Dialog with animated cover banner
        if (showHostProfileDialog) {
            AlertDialog(
                onDismissRequest = { showHostProfileDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showHostProfileDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricMagenta)
                    ) {
                        Text("Close", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showHostProfileDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("Follow Creator", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                title = null,
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // High fidelity cover with diagonal scrolling animation!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AnimatedCoverBanner()
                            
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = 10.dp)
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, GoldAccent, CircleShape)
                                    .background(CardSurface)
                            ) {
                                AsyncImage(
                                    model = room.hostAvatar,
                                    contentDescription = "Host Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(room.hostName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Official Broadcaster", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Broadcaster status stats
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("1.4M", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Followers", color = TextGray, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("8.9M", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Likes", color = TextGray, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("LV. 89", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Broadcaster", color = TextGray, fontSize = 10.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Bio in professional English
                        Text(
                            text = "A distinguished broadcast artist and content curator on Zyvo. Committed to delivering a premier live experience with high-energy PK matches and engaging community discussions.",
                            color = TextLight,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                },
                containerColor = DarkSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

// ==========================================
// Sub-components inside Live Room Screen
// ==========================================
@Composable
fun CameraStreamSimulation(filter: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "camera")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw stellar grid
            val brush = Brush.radialGradient(
                colors = listOf(Color(0xFF35125E), Color(0xFF0C0720)),
                center = Offset(size.width / 2, size.height / 2),
                radius = size.width
            )
            drawRect(brush = brush)
        }

        // Simulate camera overlays based on beauty filter selection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    when (filter) {
                        "Gold Aura" -> GoldAccent.copy(alpha = alphaAnim * 0.3f)
                        "Cosmic Pink" -> ElectricMagenta.copy(alpha = alphaAnim * 0.3f)
                        "Soft Glam" -> Color.White.copy(alpha = alphaAnim * 0.15f)
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (filter != "None") {
                Text(
                    text = "✨ $filter Active",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 110.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (msg.isSystem) {
            Box(
                modifier = Modifier
                    .background(ElectricMagenta.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(0.5.dp, ElectricMagenta.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${msg.senderEmoji} ZYVO NOTICE: ${msg.text}",
                    color = ElectricMagenta,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = msg.senderEmoji, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = msg.senderName,
                    color = if (msg.isVIP) GoldAccent else TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = msg.text,
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PKBattleArea(viewModel: LiveStreamViewModel) {
    val alphaScore by viewModel.pkAlphaScore.collectAsState()
    val betaScore by viewModel.pkBetaScore.collectAsState()

    val total = (alphaScore + betaScore).toFloat()
    val ratio = if (total == 0f) 0.5f else alphaScore.toFloat() / total

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Comparative Horizontal split screen representing PK video streams
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Team Alpha Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, ElectricMagenta, RoundedCornerShape(12.dp))
                    .background(CardSurface)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = ElectricMagenta.copy(alpha = 0.15f), radius = 60.dp.toPx())
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "TEAM ALPHA",
                        color = ElectricMagenta,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("🦁 Host Alpha", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { viewModel.supportPK("alpha") },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricMagenta),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("support_alpha")
                    ) {
                        Text("Support 🔥", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Team Beta Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFF00B4D8), RoundedCornerShape(12.dp))
                    .background(CardSurface)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = Color(0xFF00B4D8).copy(alpha = 0.15f), radius = 60.dp.toPx())
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "TEAM BETA",
                        color = Color(0xFF00B4D8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("👸 Host Queen", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { viewModel.supportPK("beta") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("support_beta")
                    ) {
                        Text("Support ⚡", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // PK Bar visualizer
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Alpha: %,d".format(alphaScore), color = ElectricMagenta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Beta: %,d".format(betaScore), color = Color(0xFF00B4D8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Comparative custom animated line/bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CardSurface)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(ratio.coerceAtLeast(0.05f))
                            .background(ElectricMagenta)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight((1f - ratio).coerceAtLeast(0.05f))
                            .background(Color(0xFF00B4D8))
                    )
                }
            }
        }
    }
}

@Composable
fun MultiGuestGrid() {
    val items = listOf(
        Pair("Elena (Host) 👾", "https://cdn.pixabay.com/photo/2017/08/30/12/45/girl-2696947_1280.jpg"),
        Pair("Marcus Vance ☕", "https://cdn.pixabay.com/photo/2016/11/29/13/14/man-1869761_1280.jpg"),
        Pair("Elena 'Pixel' 🎮", "https://cdn.pixabay.com/photo/2021/08/25/20/42/girl-6574488_1280.jpg"),
        Pair("Sovereign_33 👑", "https://cdn.pixabay.com/photo/2019/12/17/17/16/man-4702081_1280.jpg")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GuestTile(items[0].first, items[0].second, modifier = Modifier.weight(1f))
            GuestTile(items[1].first, items[1].second, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GuestTile(items[2].first, items[2].second, modifier = Modifier.weight(1f))
            GuestTile(items[3].first, items[3].second, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun GuestTile(name: String, avatarUrl: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(1.dp, TextGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Guest",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Name Tag
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(name, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AudioStageSeats() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎙️ VIP AUDIO STAGE SEATS", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Grid of 8 circular seat slots
        val rows = listOf(
            listOf(
                Triple("Host 🎧", "https://cdn.pixabay.com/photo/2015/07/30/17/24/audience-868074_1280.jpg", true),
                Triple("Seat 2 👑", "https://cdn.pixabay.com/photo/2016/11/18/19/07/man-1836437_1280.jpg", false),
                Triple("Seat 3 👾", "", false), // empty
                Triple("Seat 4 💅", "", false)
            ),
            listOf(
                Triple("Seat 5", "", true), // locked
                Triple("Seat 6", "", true),
                Triple("Seat 7", "", false),
                Triple("Seat 8", "", false)
            )
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { seat ->
                    AudioSeatBubble(label = seat.first, avatarUrl = seat.second, isAction = seat.third)
                }
            }
        }
    }
}

@Composable
fun AudioSeatBubble(label: String, avatarUrl: String, isAction: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(CardSurface)
                .border(1.5.dp, if (avatarUrl.isNotEmpty()) ElectricMagenta else TextGray.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl.isNotEmpty()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "User",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    if (isAction) Icons.Default.Lock else Icons.Default.PlayArrow,
                    contentDescription = "Seat State",
                    tint = TextGray.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextLight, fontSize = 10.sp)
    }
}

// ==========================================
// Gift Selection Dialog Component
// ==========================================
@Composable
fun GiftSelectionDialog(viewModel: LiveStreamViewModel, onDismiss: () -> Unit) {
    val coinBalance by viewModel.userCoins.collectAsState()
    val gemBalance by viewModel.userGems.collectAsState()

    val gifts = listOf(
        GiftItem("g1", "Neon Rose", "🌹", 99, "sparkle"),
        GiftItem("g2", "Luxury Gem Ring", "🔮", 1999, "burst"),
        GiftItem("g3", "Super Rocket", "🚀", 4999, "rocket"),
        GiftItem("g4", "Royal Crown", "👑", 9999, "crown")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.48f)
            .testTag("gift_dialog")
            .clickable(enabled = false) {}, // prevent click-through
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Send Premium Gift", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User assets display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙 Available Coins: ", color = TextGray, fontSize = 12.sp)
                    Text("%,d".format(coinBalance), color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(16.dp)) {
                        AsyncImage(
                            model = viewModel.gemsIconUrl,
                            contentDescription = "Gems Icon",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Available Gems: ", color = TextGray, fontSize = 12.sp)
                    Text("%,d".format(gemBalance), color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of Gifts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                gifts.forEach { gift ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.sendGift(gift)
                                onDismiss()
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(gift.emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(gift.name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("%,d 🪙".format(gift.costCoins), color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Gifts convert to host creator's Gems balance instantly.",
                color = TextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ==========================================
// Beauty Filter Selector Dialog
// ==========================================
@Composable
fun BeautyFilterDialog(viewModel: LiveStreamViewModel, onDismiss: () -> Unit) {
    val currentFilter by viewModel.beautyFilterSelected.collectAsState()
    val filters = listOf("None", "Gold Aura", "Cosmic Pink", "Soft Glam")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(enabled = false) {},
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Aesthetic Beauty Filters", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = filter == currentFilter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) ElectricMagenta else CardSurface,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.setBeautyFilter(filter)
                                onDismiss()
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// Custom Flying Particles Animation Layer
// ==========================================
@Composable
fun GiftAnimationLayer(animType: String?, viewModel: LiveStreamViewModel) {
    if (animType == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "anim")
    val posY by infiniteTransition.animateFloat(
        initialValue = 800f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "posY"
    )

    LaunchedEffect(animType) {
        delay(3000)
        viewModel.clearGiftAnimation()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = posY.dp)
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            when (animType) {
                "sparkle" -> Text("✨🔮✨", fontSize = 48.sp)
                "burst" -> Text("🔥👑🔥", fontSize = 48.sp)
                "rocket" -> Text("🚀🛸🌌", fontSize = 54.sp)
                "crown" -> Text("👑💫🌟", fontSize = 54.sp)
            }
        }
    }
}

// ==========================================
// Wallet Screen (Gems & Coins Store)
// ==========================================
@Composable
fun WalletScreen(viewModel: LiveStreamViewModel) {
    val coinBalance by viewModel.userCoins.collectAsState()
    val gemBalance by viewModel.userGems.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var processingPurchasePack by remember { mutableStateOf<RechargePack?>(null) }

    val packs = listOf(
        RechargePack("pk1", 500, "$4.99"),
        RechargePack("pk2", 1000, "$9.99", isPopular = true),
        RechargePack("pk3", 5000, "$44.99"),
        RechargePack("pk4", 10000, "$89.99")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVioletBg)
            .padding(16.dp)
    ) {
        Text(
            text = "PRESTIGE WALLET",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Text(
            text = "Top-up your balance to send gifts to your favorite creators.",
            color = TextGray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Total Assets Balance", color = TextGray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp)) {
                            AsyncImage(
                                model = viewModel.gemsIconUrl,
                                contentDescription = "Gems Icon",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "%,d Gems".format(gemBalance),
                            color = GoldAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🪙 %,d Coins".format(coinBalance),
                        color = TextLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Custom animated icon display
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = viewModel.gemsIconUrl,
                        contentDescription = "Gems Spin Animation",
                        modifier = Modifier
                            .fillMaxSize(0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "RECHARGE GEMS PACKAGES",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grid of Recharge Packs
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            packs.chunked(2).forEach { rowPacks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowPacks.forEach { pack ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    processingPurchasePack = pack
                                }
                                .testTag("recharge_pack_${pack.gemsAmount}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSurface)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(modifier = Modifier.size(28.dp)) {
                                        AsyncImage(
                                            model = viewModel.gemsIconUrl,
                                            contentDescription = "Gem Icon"
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${pack.gemsAmount} Gems",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = pack.priceUsd,
                                        color = GoldAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (pack.isPopular) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .background(
                                                ElectricMagenta,
                                                RoundedCornerShape(bottomStart = 8.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "POPULAR",
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mock payment dialog simulation
        processingPurchasePack?.let { pack ->
            AlertDialog(
                onDismissRequest = { processingPurchasePack = null },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.buyGems(pack)
                            processingPurchasePack = null
                            showSuccessDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricMagenta),
                        modifier = Modifier.testTag("confirm_recharge")
                    ) {
                        Text("Mock Pay ${pack.priceUsd}")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { processingPurchasePack = null }) {
                        Text("Cancel", color = TextGray)
                    }
                },
                title = { Text("Complete Purchase", color = Color.White) },
                text = {
                    Text(
                        "Would you like to complete a mockup purchase of ${pack.gemsAmount} Gems for ${pack.priceUsd}?",
                        color = TextLight
                    )
                },
                containerColor = DarkSurface
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showSuccessDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("Awesome!", color = Color.Black)
                    }
                },
                title = { Text("🔮 Top-up Successful!", color = GoldAccent, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Your account has been successfully loaded with premium Gems!",
                        color = TextLight
                    )
                },
                containerColor = DarkSurface
            )
        }
    }
}

// ==========================================
// Studio Analytics Screen
// ==========================================
@Composable
fun StudioAnalyticsScreen(viewModel: LiveStreamViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVioletBg)
            .padding(16.dp)
    ) {
        Text(
            text = "STUDIO HUB & ANALYTICS",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Text(
            text = "Track your platform broadcasting performance metrics.",
            color = TextGray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Metrics Grid Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "48,920 Gems",
                subtitle = "Gems Earned",
                icon = Icons.Default.Star,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "120.5 Hrs",
                subtitle = "Live Hours",
                icon = Icons.Default.Info,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "89.2K",
                subtitle = "Fans Reached",
                icon = Icons.Default.Person,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Level 89",
                subtitle = "Broadcaster Tier",
                icon = Icons.Default.Star,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "DAILY GEMS EARNINGS HISTORY",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Simulated Bar Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val days = listOf(
                        Pair("Mon", 0.4f),
                        Pair("Tue", 0.7f),
                        Pair("Wed", 0.5f),
                        Pair("Thu", 0.9f),
                        Pair("Fri", 0.6f),
                        Pair("Sat", 0.8f),
                        Pair("Sun", 1.0f)
                    )

                    days.forEach { day ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .fillMaxHeight(day.second)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(ElectricMagenta, GoldAccent)
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(day.first, color = TextGray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = TextGray, fontSize = 11.sp)
        }
    }
}

// ==========================================
// Profile & VIP Store Screen
// ==========================================
@Composable
fun AnimatedCoverBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "CoverTransition")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ElectricMagenta.copy(alpha = 0.6f),
                        DeepVioletBg,
                        GoldAccent.copy(alpha = 0.4f),
                        ElectricMagenta.copy(alpha = 0.6f)
                    ),
                    start = Offset(animOffset - 500f, 0f),
                    end = Offset(animOffset, 400f)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                center = Offset(size.width * 0.25f, size.height * 0.35f),
                radius = 18f
            )
            drawCircle(
                color = GoldAccent.copy(alpha = 0.1f),
                center = Offset(size.width * 0.75f, size.height * 0.65f),
                radius = 28f
            )
        }
    }
}

@Composable
fun ProfileScreen(viewModel: LiveStreamViewModel) {
    var showVipBenefitsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVioletBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper edge-to-edge cover with overlapping avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            AnimatedCoverBanner()

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(3.dp, ElectricMagenta, CircleShape)
                    .background(CardSurface)
            ) {
                AsyncImage(
                    model = "https://cdn.pixabay.com/photo/2021/08/25/20/42/girl-6574488_1280.jpg",
                    contentDescription = "User Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text("Alex Vance (You)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("UID: 88721369", color = TextGray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(10.dp))

        // Level / SVIP badge row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(ElectricMagenta.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("🌟 LEVEL 14", color = ElectricMagenta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .background(GoldAccent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .clickable { showVipBenefitsDialog = true }
                    .testTag("vip_store_trigger")
            ) {
                Text("👑 SVIP 7 VIP", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bio section in perfect professional English
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sovereign Biography", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Distinguished global creator and live broadcaster. Active PK Arena competitor and passionate community builder. Enthusiastically supporting the platform's premier hosts with over 10M+ Gems.",
                        color = TextLight,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Profile items list
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column {
                    ProfileMenuRow(icon = Icons.Default.Star, label = "Prestige Achievements")
                    HorizontalDivider(color = DeepVioletBg, thickness = 1.dp)
                    ProfileMenuRow(icon = Icons.Default.Star, label = "Platform Badges")
                    HorizontalDivider(color = DeepVioletBg, thickness = 1.dp)
                    ProfileMenuRow(icon = Icons.Default.Settings, label = "Settings")
                }
            }
        }

        if (showVipBenefitsDialog) {
            AlertDialog(
                onDismissRequest = { showVipBenefitsDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showVipBenefitsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("Understood", color = Color.Black)
                    }
                },
                title = { Text("👑 SVIP 7 Benefits Shop", color = GoldAccent, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("As a valued SVIP 7, you enjoy elite system benefits:", color = TextLight, fontSize = 12.sp)
                        Text("• 🔮 Gem Palace Entrance broad announcement aura.", color = TextLight, fontSize = 12.sp)
                        Text("• 🌟 Golden Broadcast live list entry privilege.", color = TextLight, fontSize = 12.sp)
                        Text("• 🛡️ Direct live room support priority channels.", color = TextLight, fontSize = 12.sp)
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}

@Composable
fun ProfileMenuRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = ElectricMagenta, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
    }
}
