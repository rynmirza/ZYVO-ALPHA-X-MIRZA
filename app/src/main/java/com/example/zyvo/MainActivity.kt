package com.example.zyvo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zyvo.ui.components.*
import com.example.zyvo.ui.screens.*
import com.example.zyvo.ui.theme.*
import com.example.zyvo.ui.viewmodel.LiveStreamViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZyvoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    ZyvoApp()
                }
            }
        }
    }
}

@Composable
fun ZyvoApp(
    viewModel: LiveStreamViewModel = viewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentRoom by viewModel.currentRoom.collectAsState()
    val filteredRooms by viewModel.filteredRooms.collectAsState()
    val followedRooms by viewModel.followedRooms.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val currentUserProfile by viewModel.currentUserProfile.collectAsState()
    val userProfiles by viewModel.userProfiles.collectAsState()
    val userCoinBalance by viewModel.userCoinBalance.collectAsState()
    val userBeansBalance by viewModel.userBeansBalance.collectAsState()
    val walletTransactions by viewModel.walletTransactions.collectAsState()
    val followingUserIds by viewModel.followingUserIds.collectAsState()
    val blockedUserIds by viewModel.blockedUserIds.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val directMessages by viewModel.directMessages.collectAsState()

    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedUserProfile by viewModel.selectedUserProfile.collectAsState()
    val activeDmPeerUserId by viewModel.activeDmPeerUserId.collectAsState()

    val showVipStoreDialog by viewModel.showVipStoreDialog.collectAsState()
    val showRechargeDialog by viewModel.showRechargeDialog.collectAsState()
    val showWithdrawalDialog by viewModel.showWithdrawalDialog.collectAsState()
    val showTransactionsDialog by viewModel.showTransactionsDialog.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val showCreateRoomSheet by viewModel.showCreateRoomSheet.collectAsState()

    var activeSubView by remember { mutableStateOf<String?>(null) } // "wallet", "vip_center", "rankings"

    val followingProfiles = remember(userProfiles, followingUserIds) {
        userProfiles.values.filter { followingUserIds.contains(it.userId) }
    }

    val blockedProfiles = remember(userProfiles, blockedUserIds) {
        userProfiles.values.filter { blockedUserIds.contains(it.userId) }
    }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { name, email, avatar, photoUrl ->
                viewModel.loginWithGoogle(name, email, avatar, photoUrl)
            }
        )
    } else {
        Scaffold(
            containerColor = DarkBackground,
        bottomBar = {
            if (currentRoom == null && activeSubView == null) {
                ZyvoBottomBar(
                    selectedTab = selectedTab,
                    onSelectTab = {
                        activeSubView = null
                        viewModel.setSelectedTab(it)
                    },
                    onGoLiveClick = { viewModel.setShowCreateRoomSheet(true) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentRoom != null) {
                // Active Live Room Studio / Audience view
                LiveRoomScreen(
                    room = currentRoom!!,
                    viewModel = viewModel,
                    onLeaveRoom = { viewModel.leaveRoom() }
                )
            } else if (activeSubView == "wallet") {
                WalletScreen(
                    coinBalance = userCoinBalance,
                    beansBalance = userBeansBalance,
                    onBack = { activeSubView = null },
                    onOpenRecharge = { viewModel.setShowRechargeDialog(true) },
                    onOpenVip = { activeSubView = "vip_center" },
                    onOpenWithdrawal = { viewModel.setShowWithdrawalDialog(true) },
                    onOpenTransactions = { viewModel.setShowTransactionsDialog(true) }
                )
            } else if (activeSubView == "vip_center") {
                VipCenterScreen(
                    currentVipTier = currentUserProfile.vipTier,
                    coinBalance = userCoinBalance,
                    onBack = { activeSubView = null },
                    onBuyVip = { tier, months, cost -> viewModel.buyVipPackage(tier, months, cost) },
                    onOpenRecharge = { viewModel.setShowRechargeDialog(true) }
                )
            } else if (activeSubView == "rankings") {
                RankingsScreen(
                    onUserClick = { userId -> viewModel.openUserProfile(userId) },
                    onFollowUser = { userId -> viewModel.followUser(userId) },
                    onUnfollowUser = { userId -> viewModel.unfollowUser(userId) }
                )
            } else {
                when (selectedTab) {
                    0 -> HomeScreen(
                        rooms = filteredRooms,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        coinBalance = userCoinBalance,
                        ceoProfile = userProfiles["ceo_rayan"],
                        coFounderProfile = userProfiles["co_founder_alpha"],
                        ansharahProfile = userProfiles["ansharah_gahni"],
                        onSelectCategory = { viewModel.setSelectedCategory(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onRoomClick = { room -> viewModel.joinRoom(room.id) },
                        onGoLiveClick = { viewModel.setShowCreateRoomSheet(true) },
                        onOpenAnalyticsClick = { activeSubView = "rankings" },
                        onOpenUserDetail = { userId -> viewModel.openUserProfile(userId) }
                    )
                    1 -> FollowingScreen(
                        followedRooms = followedRooms,
                        followingProfiles = followingProfiles,
                        conversations = conversations,
                        onRoomClick = { room -> viewModel.joinRoom(room.id) },
                        onUserClick = { userId -> viewModel.openUserProfile(userId) },
                        onOpenDm = { userId -> viewModel.openDmChat(userId) }
                    )
                    3 -> ChatScreen(
                        conversations = conversations,
                        followingProfiles = followingProfiles,
                        onOpenDm = { userId -> viewModel.openDmChat(userId) },
                        onUserClick = { userId -> viewModel.openUserProfile(userId) }
                    )
                    4 -> UserProfileScreen(
                        userProfile = currentUserProfile,
                        coinBalance = userCoinBalance,
                        beansBalance = userBeansBalance,
                        followingCount = followingUserIds.size,
                        onOpenVipStore = { activeSubView = "vip_center" },
                        onOpenRecharge = { activeSubView = "wallet" },
                        onOpenWithdrawal = { viewModel.setShowWithdrawalDialog(true) },
                        onOpenTransactions = { viewModel.setShowTransactionsDialog(true) },
                        onOpenSettings = { viewModel.setShowSettingsDialog(true) },
                        onOpenAnalytics = { activeSubView = "rankings" },
                        onOpenUserDetail = { userId -> viewModel.openUserProfile(userId) },
                        onLogout = { viewModel.logout() }
                    )
                    else -> HomeScreen(
                        rooms = filteredRooms,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        coinBalance = userCoinBalance,
                        onSelectCategory = { viewModel.setSelectedCategory(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onRoomClick = { room -> viewModel.joinRoom(room.id) },
                        onGoLiveClick = { viewModel.setShowCreateRoomSheet(true) },
                        onOpenAnalyticsClick = { activeSubView = "rankings" }
                    )
                }
            }

            // Global Dialogs & Sheets
            if (showCreateRoomSheet) {
                GoLiveScreen(
                    onDismiss = { viewModel.setShowCreateRoomSheet(false) },
                    onStartLive = { title, type, category, tags ->
                        viewModel.createRoom(title, type, category, tags, false)
                    }
                )
            }

            if (selectedUserProfile != null) {
                UserProfileSheet(
                    user = selectedUserProfile!!,
                    isSelf = selectedUserProfile!!.userId == currentUserProfile.userId,
                    onDismiss = { viewModel.closeUserProfile() },
                    onFollow = { viewModel.followUser(selectedUserProfile!!.userId) },
                    onUnfollow = { viewModel.unfollowUser(selectedUserProfile!!.userId) },
                    onOpenDm = { viewModel.openDmChat(selectedUserProfile!!.userId) },
                    onBlock = { viewModel.blockUser(selectedUserProfile!!.userId) },
                    onReport = { viewModel.reportUser(selectedUserProfile!!.userId, "Inappropriate Content") },
                    onJoinLive = { roomId -> viewModel.joinRoom(roomId) },
                    onOpenUserDetail = { userId -> viewModel.openUserProfile(userId) }
                )
            }

            if (showVipStoreDialog) {
                VipStoreDialog(
                    userCoinBalance = userCoinBalance,
                    currentVipTier = currentUserProfile.vipTier,
                    onDismiss = { viewModel.setShowVipStoreDialog(false) },
                    onBuyVip = { tier, months, cost -> viewModel.buyVipPackage(tier, months, cost) },
                    onOpenRecharge = {
                        viewModel.setShowVipStoreDialog(false)
                        viewModel.setShowRechargeDialog(true)
                    }
                )
            }

            if (showRechargeDialog) {
                RechargeDialog(
                    userCoinBalance = userCoinBalance,
                    onDismiss = { viewModel.setShowRechargeDialog(false) },
                    onRecharge = { title, coins, bonus, priceUsd, method ->
                        viewModel.rechargeCoins(title, coins, bonus, priceUsd, method)
                    }
                )
            }

            if (showWithdrawalDialog) {
                WithdrawalDialog(
                    userBeansBalance = userBeansBalance,
                    onDismiss = { viewModel.setShowWithdrawalDialog(false) },
                    onWithdraw = { beans, method, account ->
                        viewModel.requestWithdrawal(beans, method, account)
                    }
                )
            }

            if (showTransactionsDialog) {
                TransactionsDialog(
                    transactions = walletTransactions,
                    onDismiss = { viewModel.setShowTransactionsDialog(false) }
                )
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    currentUserProfile = currentUserProfile,
                    blockedUsers = blockedProfiles,
                    onDismiss = { viewModel.setShowSettingsDialog(false) },
                    onSaveProfile = { name, bio, gender, loc, avatar ->
                        viewModel.updateProfile(name, bio, gender, loc, avatar)
                    },
                    onUnblockUser = { userId -> viewModel.unblockUser(userId) }
                )
            }

            if (activeDmPeerUserId != null) {
                val peerProfile = viewModel.userProfiles.value[activeDmPeerUserId]
                    ?: viewModel.currentUserProfile.value
                val peerMessages = directMessages[activeDmPeerUserId] ?: emptyList()

                DirectMessageDialog(
                    peerUser = peerProfile,
                    messages = peerMessages,
                    onDismiss = { viewModel.closeDmChat() },
                    onSendMessage = { text -> viewModel.sendDirectMessage(activeDmPeerUserId!!, text) }
                )
            }
        }
    }
}
}

@Composable
fun ZyvoBottomBar(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onGoLiveClick: () -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("zyvo_bottom_bar")
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onSelectTab(0) },
            icon = { Icon(Icons.Default.Explore, contentDescription = "Home") },
            label = { Text("HOME", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonCyan,
                selectedTextColor = NeonCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NeonPurpleDark
            )
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onSelectTab(1) },
            icon = { Icon(Icons.Default.Tv, contentDescription = "Live") },
            label = { Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonCyan,
                selectedTextColor = NeonCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NeonPurpleDark
            )
        )

        // Center glowing Go Live button
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(ElectricMagenta, NeonPurple)
                    )
                )
                .border(2.dp, GoldAccent, CircleShape)
                .clickable { onGoLiveClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Videocam, contentDescription = "Go Live", tint = TextPrimary, modifier = Modifier.size(26.dp))
        }

        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onSelectTab(3) },
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
            label = { Text("CHAT", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonCyan,
                selectedTextColor = NeonCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NeonPurpleDark
            )
        )

        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onSelectTab(4) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("PROFILE", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonCyan,
                selectedTextColor = NeonCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NeonPurpleDark
            )
        )
    }
}
