package com.example.zyvo.data

import com.example.zyvo.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ZyvoRepository(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {

    val currentUserIdentity: String = "user_me"
    val currentUserName: String = "Alex Vance"
    val currentUserAvatar: String = "🚀"

    // Auth state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun loginWithGoogle(displayName: String, email: String, avatarEmoji: String, avatarUrl: String? = null) {
        val cleanUid = "user_google_" + UUID.randomUUID().toString().take(8)
        val generatedUsername = email.substringBefore("@").replace(".", "_").lowercase()
        
        val newProfile = UserProfile(
            userId = cleanUid,
            username = generatedUsername,
            displayName = displayName,
            avatarEmoji = avatarEmoji,
            avatarUrl = avatarUrl,
            coverGradientIndex = 0,
            bio = "Official ZYVO Broadcaster & Creator 🎙️ Live on ZYVO!",
            gender = "Unspecified",
            location = "Global HQ 🌍",
            userLevel = 1,
            userXp = 0,
            nextLevelXp = 1000,
            wealthLevel = 1,
            hostLevel = 1,
            vipTier = VipTier.NONE,
            vipExpiresTimestamp = 0L,
            followersCount = 0,
            followingCount = 3,
            followingUserIds = listOf("ceo_rayan", "co_founder_alpha", "ansharah_gahni"),
            likesCount = 0,
            diamondsEarnedTotal = 0,
            giftsReceivedTotal = 0,
            liveStreamsCount = 0,
            badges = listOf("Verified User"),
            isLiveNow = false
        )

        _currentUserProfile.value = newProfile
        _userProfiles.update { map ->
            map + (cleanUid to newProfile)
        }
        _followingUserIds.value = setOf("ceo_rayan", "co_founder_alpha", "ansharah_gahni")
        _userCoinBalance.value = 500 // Welcome bonus coins
        _userBeansBalance.value = 0
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // Current User Profile
    private val _currentUserProfile = MutableStateFlow(
        UserProfile(
            userId = "user_me",
            username = "streamer",
            displayName = "New Streamer",
            avatarEmoji = "🚀",
            coverGradientIndex = 0,
            bio = "Official Zyvo Streamer ⚡",
            gender = "Unspecified",
            location = "Global HQ 🌍",
            userLevel = 1,
            userXp = 0,
            nextLevelXp = 1000,
            wealthLevel = 1,
            hostLevel = 1,
            vipTier = VipTier.NONE,
            vipExpiresTimestamp = 0L,
            followersCount = 0,
            followingCount = 3,
            followingUserIds = listOf("ceo_rayan", "co_founder_alpha", "ansharah_gahni"),
            likesCount = 0,
            diamondsEarnedTotal = 0,
            giftsReceivedTotal = 0,
            liveStreamsCount = 0,
            badges = listOf("Verified User"),
            isLiveNow = false
        )
    )
    val currentUserProfile: StateFlow<UserProfile> = _currentUserProfile.asStateFlow()

    // All Users Registry
    private val _userProfiles = MutableStateFlow<Map<String, UserProfile>>(emptyMap())
    val userProfiles: StateFlow<Map<String, UserProfile>> = _userProfiles.asStateFlow()

    // Wallet balances
    private val _userCoinBalance = MutableStateFlow(500)
    val userCoinBalance: StateFlow<Int> = _userCoinBalance.asStateFlow()

    private val _userBeansBalance = MutableStateFlow(0)
    val userBeansBalance: StateFlow<Int> = _userBeansBalance.asStateFlow()

    // Transaction History
    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    // Following & Blocked User IDs
    private val _followingUserIds = MutableStateFlow<Set<String>>(setOf("ceo_rayan", "co_founder_alpha", "ansharah_gahni"))
    val followingUserIds: StateFlow<Set<String>> = _followingUserIds.asStateFlow()

    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    // Direct Messages & Conversations
    private val _conversations = MutableStateFlow<List<ConversationSummary>>(emptyList())
    val conversations: StateFlow<List<ConversationSummary>> = _conversations.asStateFlow()

    private val _directMessages = MutableStateFlow<Map<String, List<DirectMessage>>>(emptyMap())
    val directMessages: StateFlow<Map<String, List<DirectMessage>>> = _directMessages.asStateFlow()

    // Live Rooms & Stream state
    private val _rooms = MutableStateFlow<List<LiveRoom>>(emptyList())
    val rooms: StateFlow<List<LiveRoom>> = _rooms.asStateFlow()

    private val _currentRoom = MutableStateFlow<LiveRoom?>(null)
    val currentRoom: StateFlow<LiveRoom?> = _currentRoom.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    private val _participants = MutableStateFlow<Map<String, List<Participant>>>(emptyMap())
    val participants: StateFlow<Map<String, List<Participant>>> = _participants.asStateFlow()

    private val _activeFloatingGifts = MutableStateFlow<List<ChatMessage>>(emptyList())
    val activeFloatingGifts: StateFlow<List<ChatMessage>> = _activeFloatingGifts.asStateFlow()

    private val _currentFilter = MutableStateFlow(BeautifyFilter.ORIGINAL)
    val currentFilter: StateFlow<BeautifyFilter> = _currentFilter.asStateFlow()

    private val _isMicMuted = MutableStateFlow(false)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isVideoMuted = MutableStateFlow(false)
    val isVideoMuted: StateFlow<Boolean> = _isVideoMuted.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(true)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _lastPlayedSfx = MutableStateFlow<String?>(null)
    val lastPlayedSfx: StateFlow<String?> = _lastPlayedSfx.asStateFlow()

    init {
        initializeInitialUsers()
        initializeInitialTransactions()
        initializeInitialRooms()
        initializeInitialDms()
        startLiveSimulation()
    }

    private fun initializeInitialUsers() {
        val initialUsers = mapOf(
            "ceo_rayan" to UserProfile(
                userId = "ceo_rayan",
                username = "rayan_mirza",
                displayName = "RAYAN MIRZA",
                avatarEmoji = "👑",
                avatarUrl = "https://cdn.phototourl.com/free/2026-09-01-f3e014af-6987-41b0-8bcf-732294379e68.png",
                coverGradientIndex = 0,
                bio = "👑 Founder & Chief Executive Officer (CEO) of ZYVO. Empowering millions of creators worldwide. Contact executive desk via WhatsApp: +44 7868 713315",
                gender = "Male",
                location = "London, UK 🇬🇧 / Global HQ 🌍",
                userLevel = 99,
                userXp = 9999999,
                nextLevelXp = 10000000,
                wealthLevel = 99,
                hostLevel = 99,
                vipTier = VipTier.VIP_9,
                vipExpiresTimestamp = System.currentTimeMillis() + (365L * 24 * 3600 * 1000),
                followersCount = 9850000,
                followingCount = 99,
                likesCount = 158000000,
                diamondsEarnedTotal = 99999999,
                giftsReceivedTotal = 99999,
                liveStreamsCount = 999,
                badges = listOf("👑 CEO & Founder", "💎 VIP 9 SUPREME", "⭐ Lv.99 Sovereign", "Official Verified", "God Tier Creator"),
                isFollowedByCurrentUser = true,
                isLiveNow = true,
                currentRoomId = "room_ceo_999",
                executiveRole = "CEO & FOUNDER",
                whatsappNumber = "+44 7868 713315",
                whatsappDirectUrl = "https://wa.me/447868713315"
            ),
            "co_founder_alpha" to UserProfile(
                userId = "co_founder_alpha",
                username = "alpha_rajpoot",
                displayName = "ALPHA RAJPOOT",
                avatarEmoji = "🦁",
                avatarUrl = "https://cdn.phototourl.com/free/2026-09-01-4aa927e1-ee25-497a-ae9e-4201e9d81679.jpg",
                coverGradientIndex = 2,
                bio = "🔥 Co-Founder & Executive Director at ZYVO. Head of Global PK Arenas, Creator Growth & Strategic Partnerships. WhatsApp Executive Desk: +447366 387620",
                gender = "Male",
                location = "London, UK 🇬🇧 / Global Operations 🌍",
                userLevel = 99,
                userXp = 9999999,
                nextLevelXp = 10000000,
                wealthLevel = 99,
                hostLevel = 99,
                vipTier = VipTier.VIP_9,
                vipExpiresTimestamp = System.currentTimeMillis() + (365L * 24 * 3600 * 1000),
                followersCount = 8420000,
                followingCount = 88,
                likesCount = 132000000,
                diamondsEarnedTotal = 88888888,
                giftsReceivedTotal = 88888,
                liveStreamsCount = 888,
                badges = listOf("🛡️ Co-Founder", "💎 VIP 9 SUPREME", "⭐ Lv.99 Sovereign", "Official Verified", "PK Grandmaster"),
                isFollowedByCurrentUser = true,
                isLiveNow = true,
                currentRoomId = "room_alpha_888",
                executiveRole = "CO-FOUNDER & EXECUTIVE DIRECTOR",
                whatsappNumber = "+447366 387620",
                whatsappDirectUrl = "https://wa.me/447366387620"
            ),
            "ansharah_gahni" to UserProfile(
                userId = "ansharah_gahni",
                username = "ansharah_gahni",
                displayName = "ANSHARAH GAHNI",
                avatarEmoji = "👸",
                avatarUrl = "https://mp3tourl.com/images/1788287833535-dc94ba6e-5e98-4349-b949-cd1521ff4618.jpg",
                coverGradientIndex = 3,
                bio = "✨ TOP HOST & ZYVO GLOBAL QUEEN 👑 Level 89 Superstar • SVIP 7 • Receiving Millions Daily 💖 Officially Following Founder Rayan Mirza & Co-Founder Alpha Rajpoot 🌍",
                gender = "Female",
                location = "Dubai, UAE 🇦🇪 / Global Host Stage 🌍",
                userLevel = 89,
                userXp = 8900000,
                nextLevelXp = 9000000,
                wealthLevel = 89,
                hostLevel = 89,
                vipTier = VipTier.SVIP_7,
                vipExpiresTimestamp = System.currentTimeMillis() + (365L * 24 * 3600 * 1000),
                followersCount = 7650000,
                followingCount = 2,
                followingUserIds = listOf("ceo_rayan", "co_founder_alpha"),
                likesCount = 98500000,
                diamondsEarnedTotal = 78500000,
                giftsReceivedTotal = 75800,
                liveStreamsCount = 740,
                badges = listOf("💎 SVIP 7 QUEEN", "👑 Top Host Queen", "⭐ Lv.89 Superstar", "Official Verified Broadcaster", "Million Receiving Elite"),
                isFollowedByCurrentUser = true,
                isLiveNow = true,
                currentRoomId = "room_ansharah_777",
                isTopHost = true
            )
        )
        _userProfiles.value = initialUsers
    }

    private fun initializeInitialTransactions() {
        _walletTransactions.value = listOf(
            WalletTransaction(
                id = "tx_101",
                title = "Coin Recharge Package",
                detail = "7,500 Coins + 2,000 Bonus Coins via Google Pay",
                coinAmount = 9500,
                usdAmount = 49.99,
                type = TransactionType.COIN_RECHARGE,
                status = TransactionStatus.COMPLETED,
                timestampFormatted = "Today, 14:32",
                iconEmoji = "💳"
            ),
            WalletTransaction(
                id = "tx_102",
                title = "Sent Golden Dragon Gift",
                detail = "To Kai Sterling in Cyberpunk Beats DJ Session",
                coinAmount = -2500,
                type = TransactionType.GIFT_SENT,
                status = TransactionStatus.COMPLETED,
                timestampFormatted = "Yesterday, 21:15",
                iconEmoji = "🐉"
            ),
            WalletTransaction(
                id = "tx_103",
                title = "Purchased VIP 3 Pass",
                detail = "1 Month VIP 3 Membership Renewal",
                coinAmount = -2000,
                type = TransactionType.VIP_PURCHASE,
                status = TransactionStatus.COMPLETED,
                timestampFormatted = "3 days ago",
                iconEmoji = "👑"
            ),
            WalletTransaction(
                id = "tx_104",
                title = "Host Earnings Payout",
                detail = "Withdrawal of $150.00 USD via PayPal Payout",
                beanAmount = -15000,
                usdAmount = 150.00,
                type = TransactionType.WITHDRAWAL,
                status = TransactionStatus.COMPLETED,
                timestampFormatted = "5 days ago",
                iconEmoji = "💸"
            )
        )
    }

    private fun initializeInitialRooms() {
        val ceoRoom = LiveRoom(
            id = "room_ceo_999",
            title = "👑 CEO RAYAN MIRZA | Official Global Keynote & Creator Summit",
            description = "Welcome to ZYVO HQ Live Stream! Discussing new features, massive creator rewards, and global partnership opportunities. Direct WhatsApp available.",
            creatorIdentity = "ceo_rayan",
            hostName = "RAYAN MIRZA (CEO)",
            hostAvatar = "👑",
            hostAvatarUrl = "https://cdn.phototourl.com/free/2026-09-01-f3e014af-6987-41b0-8bcf-732294379e68.png",
            roomCoverUrl = "https://cdn.phototourl.com/free/2026-09-01-f3e014af-6987-41b0-8bcf-732294379e68.png",
            coverStyle = "FULL_BACKDROP",
            roomType = RoomType.SINGLE_LIVE,
            category = "Official",
            tags = listOf("CEO", "Official", "Summit", "VIP9", "Lv99"),
            viewerCount = 148900,
            likesCount = 2850000,
            enableChat = true
        )

        val alphaRoom = LiveRoom(
            id = "room_alpha_888",
            title = "⚔️ ALPHA RAJPOOT | Zyvo Executive PK Battle & 10M Diamond Drop",
            description = "High stakes Executive Battle Stage! Dropping millions of diamonds and golden gifts for the community.",
            creatorIdentity = "co_founder_alpha",
            hostName = "ALPHA RAJPOOT (CO-FOUNDER)",
            hostAvatar = "🦁",
            hostAvatarUrl = "https://cdn.phototourl.com/free/2026-09-01-4aa927e1-ee25-497a-ae9e-4201e9d81679.jpg",
            roomCoverUrl = "https://cdn.phototourl.com/free/2026-09-01-4aa927e1-ee25-497a-ae9e-4201e9d81679.jpg",
            coverStyle = "FULL_BACKDROP",
            roomType = RoomType.PK_BATTLE,
            category = "PK Arena",
            tags = listOf("CoFounder", "PK", "HighStakes", "VIP9", "Lv99"),
            viewerCount = 112400,
            likesCount = 1950000,
            pkState = PkState(
                isActive = true,
                targetHostName = "Apex Arenas",
                targetHostAvatar = "⚔️",
                myScore = 890000,
                targetScore = 650000,
                remainingSeconds = 240
            )
        )

        val ansharahRoom = LiveRoom(
            id = "room_ansharah_777",
            title = "💎 ANSHARAH GAHNI | Top Host Queen Live & 50M Diamond Gala 👑",
            description = "Welcome to Ansharah Gahni's Official Live Stage! Level 89 Top Host • SVIP 7 • Dropping massive rewards with Founder & Co-Founder backing!",
            creatorIdentity = "ansharah_gahni",
            hostName = "ANSHARAH GAHNI",
            hostAvatar = "👸",
            hostAvatarUrl = "https://mp3tourl.com/images/1788287833535-dc94ba6e-5e98-4349-b949-cd1521ff4618.jpg",
            roomCoverUrl = "https://mp3tourl.com/images/1788287833535-dc94ba6e-5e98-4349-b949-cd1521ff4618.jpg",
            coverStyle = "FULL_BACKDROP",
            roomType = RoomType.SINGLE_LIVE,
            category = "Top Host",
            tags = listOf("TopHost", "SVIP7", "Lv89", "Queen", "Official"),
            viewerCount = 98400,
            likesCount = 1650000,
            enableChat = true
        )

        _rooms.value = listOf(ceoRoom, alphaRoom, ansharahRoom)

        // Pre-fill initial chat messages
        _chatMessages.value = mapOf(
            "room_ceo_999" to listOf(
                ChatMessage("c01", "system", "ZYVO HQ", senderAvatar = "👑", text = "👑 WELCOME TO CEO & FOUNDER RAYAN MIRZA OFFICIAL LIVE! VIP 9 SUPREME ACTIVE.", type = MessageType.SYSTEM),
                ChatMessage("c02", "vip_fan", "Lord_Vanguard", "💎", "Glory to Founder Rayan Mirza! Sent 50x Golden Dragons! 🐉"),
                ChatMessage("c03", "creator_1", "Mia_Vocal", "🎤", "Thank you for the creator fund upgrade! Zyvo is #1 🔥")
            ),
            "room_alpha_888" to listOf(
                ChatMessage("c04", "system", "ZYVO HQ", senderAvatar = "🛡️", text = "⚔️ WELCOME TO CO-FOUNDER ALPHA RAJPOOT EXECUTIVE PK ARENA! VIP 9 ACTIVE.", type = MessageType.SYSTEM),
                ChatMessage("c05", "pk_titan", "Gamer_Rex", "🦁", "Alpha Rajpoot crushing the arena! 100k combo!! 💥")
            ),
            "room_ansharah_777" to listOf(
                ChatMessage("ca1", "system", "ZYVO HQ", senderAvatar = "💎", text = "👑 WELCOME TO TOP HOST ANSHARAH GAHNI OFFICIAL LIVE! SVIP 7 ACTIVE.", type = MessageType.SYSTEM),
                ChatMessage("ca2", "ceo_rayan", "RAYAN MIRZA (CEO)", "👑", "Welcome Ansharah to the Top Host Spotlight! 💎 Sent 100,000 Diamonds!"),
                ChatMessage("ca3", "co_founder_alpha", "ALPHA RAJPOOT", "🦁", "Keep shining Queen Ansharah! Top Host power! 🔥"),
                ChatMessage("ca4", "vip_fan", "CrownPrince_99", "💎", "Sent 10x Galactic Dragon Palace to Queen Ansharah! 👸")
            )
        )

        // Pre-fill participants
        _participants.value = mapOf(
            "room_ceo_999" to listOf(
                Participant("ceo_rayan", "RAYAN MIRZA (CEO)", "👑", role = ParticipantRole.HOST),
                Participant("co_founder_alpha", "ALPHA RAJPOOT", "🦁", role = ParticipantRole.ADMIN),
                Participant("ansharah_gahni", "ANSHARAH GAHNI", "👸", role = ParticipantRole.ADMIN)
            ),
            "room_alpha_888" to listOf(
                Participant("co_founder_alpha", "ALPHA RAJPOOT", "🦁", role = ParticipantRole.HOST),
                Participant("ceo_rayan", "RAYAN MIRZA (CEO)", "👑", role = ParticipantRole.ADMIN)
            ),
            "room_ansharah_777" to listOf(
                Participant("ansharah_gahni", "ANSHARAH GAHNI", "👸", role = ParticipantRole.HOST),
                Participant("ceo_rayan", "RAYAN MIRZA (CEO)", "👑", role = ParticipantRole.ADMIN)
            )
        )
    }

    private fun initializeInitialDms() {
        val initialConversations = listOf(
            ConversationSummary(
                peerUserId = "ceo_rayan",
                peerDisplayName = "RAYAN MIRZA (CEO)",
                peerUsername = "@rayan_mirza",
                peerAvatarEmoji = "👑",
                peerVipTier = VipTier.VIP_9,
                lastMessageText = "Welcome to ZYVO Live! Reach out anytime on WhatsApp for official creator backing.",
                lastMessageTime = "12:00",
                unreadCount = 1,
                isPeerLive = true
            ),
            ConversationSummary(
                peerUserId = "co_founder_alpha",
                peerDisplayName = "ALPHA RAJPOOT",
                peerUsername = "@alpha_rajpoot",
                peerAvatarEmoji = "🦁",
                peerVipTier = VipTier.VIP_9,
                lastMessageText = "Welcome to the family! Join the PK Arenas and climb the global leaderboards.",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isPeerLive = true
            ),
            ConversationSummary(
                peerUserId = "ansharah_gahni",
                peerDisplayName = "ANSHARAH GAHNI",
                peerUsername = "@ansharah_gahni",
                peerAvatarEmoji = "👸",
                peerVipTier = VipTier.SVIP_7,
                lastMessageText = "Hello darling! Welcome to Zyvo Live! Let me know if you need any hosting tips ✨",
                lastMessageTime = "2 days ago",
                unreadCount = 0,
                isPeerLive = true
            )
        )
        _conversations.value = initialConversations

        _directMessages.value = mapOf(
            "ceo_rayan" to listOf(
                DirectMessage("m1", "ceo_rayan", "RAYAN MIRZA (CEO)", "👑", "Welcome to ZYVO Live! Reach out anytime on WhatsApp for official creator backing.", "12:00", false)
            ),
            "co_founder_alpha" to listOf(
                DirectMessage("m2", "co_founder_alpha", "ALPHA RAJPOOT", "🦁", "Welcome to the family! Join the PK Arenas and climb the global leaderboards.", "Yesterday", false)
            ),
            "ansharah_gahni" to listOf(
                DirectMessage("m3", "ansharah_gahni", "ANSHARAH GAHNI", "👸", "Hello darling! Welcome to Zyvo Live! Let me know if you need any hosting tips ✨", "2 days ago", false)
            )
        )
    }

    // === USER PROFILE & ACTIONS ===

    fun getUserProfile(userId: String): UserProfile {
        if (userId == currentUserIdentity || userId == _currentUserProfile.value.userId) {
            return _currentUserProfile.value
        }
        return _userProfiles.value[userId] ?: UserProfile(
            userId = userId,
            username = "user_$userId",
            displayName = "User $userId",
            avatarEmoji = "👤"
        )
    }

    fun updateProfile(
        displayName: String,
        bio: String,
        gender: String,
        location: String,
        avatarEmoji: String,
        username: String? = null,
        avatarUrl: String? = null,
        coverGradientIndex: Int? = null
    ) {
        _currentUserProfile.update { profile ->
            profile.copy(
                displayName = displayName.ifBlank { profile.displayName },
                username = username?.ifBlank { profile.username } ?: profile.username,
                bio = bio,
                gender = gender,
                location = location,
                avatarEmoji = avatarEmoji,
                avatarUrl = avatarUrl ?: profile.avatarUrl,
                coverGradientIndex = coverGradientIndex ?: profile.coverGradientIndex
            )
        }
        // Update user in directory as well
        val updated = _currentUserProfile.value
        _userProfiles.update { map ->
            map + (updated.userId to updated)
        }
    }

    fun followUser(userId: String) {
        _followingUserIds.update { it + userId }
        _userProfiles.update { map ->
            val p = map[userId]
            if (p != null) {
                map + (userId to p.copy(
                    isFollowedByCurrentUser = true,
                    followersCount = p.followersCount + 1
                ))
            } else map
        }
        _currentUserProfile.update { it.copy(followingCount = it.followingCount + 1) }
    }

    fun unfollowUser(userId: String) {
        _followingUserIds.update { it - userId }
        _userProfiles.update { map ->
            val p = map[userId]
            if (p != null) {
                map + (userId to p.copy(
                    isFollowedByCurrentUser = false,
                    followersCount = maxOf(0, p.followersCount - 1)
                ))
            } else map
        }
        _currentUserProfile.update { it.copy(followingCount = maxOf(0, it.followingCount - 1)) }
    }

    fun blockUser(userId: String) {
        _blockedUserIds.update { it + userId }
        _userProfiles.update { map ->
            val p = map[userId]
            if (p != null) {
                map + (userId to p.copy(isBlocked = true))
            } else map
        }
    }

    fun unblockUser(userId: String) {
        _blockedUserIds.update { it - userId }
        _userProfiles.update { map ->
            val p = map[userId]
            if (p != null) {
                map + (userId to p.copy(isBlocked = false))
            } else map
        }
    }

    fun reportUser(userId: String, reason: String) {
        // Log report simulation
    }

    // === WALLET & FINANCES ===

    fun rechargeCoins(packageTitle: String, coinAmount: Int, bonusCoins: Int, usdPrice: Double, paymentMethod: String) {
        val totalAdded = coinAmount + bonusCoins
        _userCoinBalance.update { it + totalAdded }

        // Increase user XP & Wealth level
        val addedXp = totalAdded * 2
        _currentUserProfile.update { p ->
            val newXp = p.userXp + addedXp
            val newLevel = if (newXp >= p.nextLevelXp) p.userLevel + 1 else p.userLevel
            val nextXp = if (newXp >= p.nextLevelXp) p.nextLevelXp + 3000 else p.nextLevelXp
            val newWealth = p.wealthLevel + (totalAdded / 1000)
            p.copy(
                userXp = newXp,
                userLevel = newLevel,
                nextLevelXp = nextXp,
                wealthLevel = maxOf(p.wealthLevel, newWealth)
            )
        }

        // Add transaction log
        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(6),
            title = "Recharged $packageTitle",
            detail = "+$totalAdded Coins via $paymentMethod",
            coinAmount = totalAdded,
            usdAmount = usdPrice,
            type = TransactionType.COIN_RECHARGE,
            status = TransactionStatus.COMPLETED,
            timestampFormatted = "Just now",
            iconEmoji = "💳"
        )
        _walletTransactions.update { listOf(tx) + it }
    }

    fun buyVipPackage(tier: VipTier, months: Int, coinCost: Int) {
        if (_userCoinBalance.value < coinCost) return

        _userCoinBalance.update { it - coinCost }

        val expiry = System.currentTimeMillis() + (months * 30L * 24 * 3600 * 1000)
        _currentUserProfile.update { p ->
            p.copy(
                vipTier = tier,
                vipExpiresTimestamp = expiry,
                badges = (p.badges + tier.badge).distinct()
            )
        }

        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(6),
            title = "Purchased ${tier.displayName}",
            detail = "$months Month Membership Pass",
            coinAmount = -coinCost,
            type = TransactionType.VIP_PURCHASE,
            status = TransactionStatus.COMPLETED,
            timestampFormatted = "Just now",
            iconEmoji = "👑"
        )
        _walletTransactions.update { listOf(tx) + it }
    }

    fun requestWithdrawal(beansAmount: Int, payoutMethod: String, accountDetail: String) {
        if (_userBeansBalance.value < beansAmount) return

        val usdAmount = beansAmount / 100.0
        _userBeansBalance.update { it - beansAmount }

        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(6),
            title = "Withdrawal Request ($${String.format("%.2f", usdAmount)})",
            detail = "Cashout of $beansAmount Beans to $payoutMethod ($accountDetail)",
            beanAmount = -beansAmount,
            usdAmount = usdAmount,
            type = TransactionType.WITHDRAWAL,
            status = TransactionStatus.COMPLETED,
            timestampFormatted = "Just now",
            iconEmoji = "💸"
        )
        _walletTransactions.update { listOf(tx) + it }
    }

    // === DIRECT MESSAGES ===

    fun sendDirectMessage(peerUserId: String, text: String, gift: Gift? = null) {
        if (text.isBlank() && gift == null) return

        val peer = getUserProfile(peerUserId)
        val msg = DirectMessage(
            id = UUID.randomUUID().toString(),
            senderId = currentUserIdentity,
            senderName = currentUserName,
            senderAvatar = currentUserAvatar,
            text = text.ifBlank { "Sent a ${gift?.name} ${gift?.iconEmoji}" },
            timestampFormatted = "Just now",
            isFromCurrentUser = true,
            giftAttached = gift
        )

        // Update messages map
        _directMessages.update { map ->
            val list = map[peerUserId] ?: emptyList()
            map + (peerUserId to (list + msg))
        }

        // Update conversation summary
        _conversations.update { list ->
            val existing = list.find { it.peerUserId == peerUserId }
            val updatedSummary = ConversationSummary(
                peerUserId = peerUserId,
                peerDisplayName = peer.displayName,
                peerUsername = "@${peer.username}",
                peerAvatarEmoji = peer.avatarEmoji,
                peerVipTier = peer.vipTier,
                lastMessageText = msg.text,
                lastMessageTime = "Just now",
                unreadCount = 0,
                isPeerLive = peer.isLiveNow
            )
            if (existing != null) {
                list.map { if (it.peerUserId == peerUserId) updatedSummary else it }
            } else {
                listOf(updatedSummary) + list
            }
        }
    }

    // === ROOMS & LIVE SIMULATION ===

    fun joinRoom(roomId: String) {
        val room = _rooms.value.find { it.id == roomId } ?: return
        _currentRoom.value = room

        // Add user as participant
        _participants.update { map ->
            val list = map[roomId] ?: emptyList()
            val exists = list.any { it.identity == currentUserIdentity }
            if (!exists) {
                map + (roomId to (list + Participant(currentUserIdentity, currentUserName, currentUserAvatar, ParticipantRole.VIEWER)))
            } else map
        }

        sendSystemMessage(roomId, "$currentUserName joined the live stream ✨")
    }

    fun leaveRoom() {
        val room = _currentRoom.value ?: return
        sendSystemMessage(room.id, "$currentUserName left the room")
        _currentRoom.value = null
    }

    fun createRoom(
        title: String,
        roomType: RoomType,
        category: String,
        tags: List<String>,
        isPrivate: Boolean = false,
        password: String? = null
    ) {
        val newRoomId = "room_${UUID.randomUUID().toString().take(8)}"
        val initialSeats = if (roomType == RoomType.MULTI_GUEST || roomType == RoomType.AUDIO_STAGE) {
            listOf(
                Seat(id = 1, occupied = true, assignedParticipant = currentUserIdentity, participantName = "$currentUserName (Host)", avatarEmoji = currentUserAvatar, role = "HOST"),
                Seat(id = 2, occupied = false, locked = false),
                Seat(id = 3, occupied = false, locked = false),
                Seat(id = 4, occupied = false, locked = false),
                Seat(id = 5, occupied = false, locked = false),
                Seat(id = 6, occupied = false, locked = false)
            )
        } else emptyList()

        val currentProfile = _currentUserProfile.value
        val newRoom = LiveRoom(
            id = newRoomId,
            title = title,
            creatorIdentity = currentUserIdentity,
            hostName = currentUserName,
            hostAvatar = currentUserAvatar,
            hostAvatarUrl = currentProfile.avatarUrl,
            roomCoverUrl = currentProfile.avatarUrl,
            coverStyle = "FULL_BACKDROP",
            roomType = roomType,
            category = category,
            tags = tags,
            isPrivate = isPrivate,
            password = password,
            viewerCount = 1,
            likesCount = 0,
            seats = initialSeats
        )

        _rooms.update { listOf(newRoom) + it }
        _currentRoom.value = newRoom
        _participants.value = mapOf(
            newRoomId to listOf(Participant(currentUserIdentity, currentUserName, currentUserAvatar, ParticipantRole.HOST))
        )
        sendSystemMessage(newRoomId, "Broadcast Studio live stream initiated! 🔴")
    }

    fun updateRoomCover(roomId: String, coverUrl: String, coverStyle: String = "FULL_BACKDROP") {
        _rooms.update { list ->
            list.map { room ->
                if (room.id == roomId) {
                    room.copy(roomCoverUrl = coverUrl, coverStyle = coverStyle)
                } else room
            }
        }
        if (_currentRoom.value?.id == roomId) {
            _currentRoom.value = _currentRoom.value?.copy(roomCoverUrl = coverUrl, coverStyle = coverStyle)
        }
    }

    fun updateBroadcasterProfilePic(userId: String, newAvatarUrl: String) {
        if (userId == currentUserIdentity) {
            _currentUserProfile.update { it.copy(avatarUrl = newAvatarUrl) }
        }
        _userProfiles.update { map ->
            val existing = map[userId]
            if (existing != null) {
                map + (userId to existing.copy(avatarUrl = newAvatarUrl))
            } else map
        }
        // Also update any live rooms hosted by this broadcaster
        _rooms.update { list ->
            list.map { room ->
                if (room.creatorIdentity == userId) {
                    room.copy(
                        hostAvatarUrl = newAvatarUrl,
                        roomCoverUrl = if (room.roomCoverUrl == room.hostAvatarUrl || room.roomCoverUrl.isNullOrBlank()) newAvatarUrl else room.roomCoverUrl
                    )
                } else room
            }
        }
        if (_currentRoom.value?.creatorIdentity == userId) {
            _currentRoom.value = _currentRoom.value?.copy(
                hostAvatarUrl = newAvatarUrl,
                roomCoverUrl = if (_currentRoom.value?.roomCoverUrl == _currentRoom.value?.hostAvatarUrl || _currentRoom.value?.roomCoverUrl.isNullOrBlank()) newAvatarUrl else _currentRoom.value?.roomCoverUrl
            )
        }
    }

    fun sendTextMessage(roomId: String, text: String, mention: String? = null) {
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderIdentity = currentUserIdentity,
            senderName = currentUserName,
            senderAvatar = currentUserAvatar,
            text = if (mention != null) "@$mention $text" else text,
            type = MessageType.TEXT
        )

        _chatMessages.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to (list + msg))
        }
    }

    fun sendGift(roomId: String, gift: Gift, count: Int = 1) {
        val totalCost = gift.coinCost * count
        if (_userCoinBalance.value < totalCost) return

        _userCoinBalance.update { it - totalCost }

        // Host receives beans (100% of coin cost added to host beans)
        val room = _rooms.value.find { it.id == roomId }
        if (room != null && room.creatorIdentity == currentUserIdentity) {
            _userBeansBalance.update { it + totalCost }
        }

        // Increase user XP & Wealth
        _currentUserProfile.update { p ->
            val addedXp = totalCost * 3
            val newXp = p.userXp + addedXp
            val newLevel = if (newXp >= p.nextLevelXp) p.userLevel + 1 else p.userLevel
            val nextXp = if (newXp >= p.nextLevelXp) p.nextLevelXp + 3000 else p.nextLevelXp
            val newWealth = p.wealthLevel + maxOf(1, totalCost / 500)
            p.copy(
                userXp = newXp,
                userLevel = newLevel,
                nextLevelXp = nextXp,
                wealthLevel = newWealth
            )
        }

        // Add Transaction
        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(6),
            title = "Sent ${count}x ${gift.name}",
            detail = "In stream: ${room?.title ?: "Live Room"}",
            coinAmount = -totalCost,
            type = TransactionType.GIFT_SENT,
            status = TransactionStatus.COMPLETED,
            timestampFormatted = "Just now",
            iconEmoji = gift.iconEmoji
        )
        _walletTransactions.update { listOf(tx) + it }

        val giftMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderIdentity = currentUserIdentity,
            senderName = currentUserName,
            senderAvatar = currentUserAvatar,
            text = "Sent $count x ${gift.name} ${gift.iconEmoji}!",
            type = MessageType.GIFT,
            gift = gift,
            giftCount = count
        )

        _chatMessages.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to (list + giftMessage))
        }

        // Floating animation queue
        _activeFloatingGifts.update { it + giftMessage }
        scope.launch {
            delay(4000)
            _activeFloatingGifts.update { list -> list.filter { it.id != giftMessage.id } }
        }

        // Update room score & PK state
        _rooms.update { roomsList ->
            roomsList.map { r ->
                if (r.id == roomId) {
                    val updatedPk = if (r.pkState.isActive) {
                        r.pkState.copy(myScore = r.pkState.myScore + totalCost)
                    } else r.pkState
                    val updatedTeam = if (r.teamState.isActive) {
                        r.teamState.copy(myTeamScore = r.teamState.myTeamScore + totalCost)
                    } else r.teamState

                    r.copy(
                        likesCount = r.likesCount + (totalCost * 2),
                        pkState = updatedPk,
                        teamState = updatedTeam
                    )
                } else r
            }
        }

        _currentRoom.update { current ->
            if (current?.id == roomId) {
                val updatedPk = if (current.pkState.isActive) {
                    current.pkState.copy(myScore = current.pkState.myScore + totalCost)
                } else current.pkState
                val updatedTeam = if (current.teamState.isActive) {
                    current.teamState.copy(myTeamScore = current.teamState.myTeamScore + totalCost)
                } else current.teamState
                current.copy(
                    likesCount = current.likesCount + (totalCost * 2),
                    pkState = updatedPk,
                    teamState = updatedTeam
                )
            } else current
        }
    }

    fun sendLike(roomId: String) {
        _rooms.update { list ->
            list.map { r -> if (r.id == roomId) r.copy(likesCount = r.likesCount + 1) else r }
        }
        _currentRoom.update { current ->
            if (current?.id == roomId) current.copy(likesCount = current.likesCount + 1) else current
        }
    }

    fun toggleChatEnabled(roomId: String, enabled: Boolean) {
        _rooms.update { list ->
            list.map { r -> if (r.id == roomId) r.copy(enableChat = enabled) else r }
        }
        _currentRoom.update { current ->
            if (current?.id == roomId) current.copy(enableChat = enabled) else current
        }
        sendSystemMessage(roomId, if (enabled) "Host enabled the chat" else "Host paused the chat")
    }

    fun requestToPresent(roomId: String, seatId: Int = -1) {
        _participants.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to list.map { p ->
                if (p.identity == currentUserIdentity) p.copy(isReqToPresent = true, isRequestedToCall = true)
                else p
            })
        }
        sendSystemMessage(roomId, "$currentUserName raised hand to join stage ✋")
    }

    fun cancelRequestToPresent(roomId: String) {
        _participants.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to list.map { p ->
                if (p.identity == currentUserIdentity) p.copy(isReqToPresent = false, isRequestedToCall = false)
                else p
            })
        }
    }

    fun inviteParticipantToStage(roomId: String, participantIdentity: String, targetSeatId: Int = -1) {
        val room = _rooms.value.find { it.id == roomId } ?: return
        val participant = (_participants.value[roomId] ?: emptyList()).find { it.identity == participantIdentity } ?: return

        val availableSeat = if (targetSeatId != -1) {
            room.seats.find { it.id == targetSeatId && !it.occupied && !it.locked }
        } else {
            room.seats.find { !it.occupied && !it.locked }
        }

        if (availableSeat != null) {
            val updatedSeats = room.seats.map { s ->
                if (s.id == availableSeat.id) {
                    s.copy(
                        occupied = true,
                        assignedParticipant = participantIdentity,
                        participantName = participant.name,
                        avatarEmoji = participant.avatar,
                        role = "SPEAKER"
                    )
                } else s
            }

            _rooms.update { list ->
                list.map { r -> if (r.id == roomId) r.copy(seats = updatedSeats) else r }
            }
            _currentRoom.update { current ->
                if (current?.id == roomId) current.copy(seats = updatedSeats) else current
            }

            _participants.update { map ->
                val list = map[roomId] ?: emptyList()
                map + (roomId to list.map { p ->
                    if (p.identity == participantIdentity) {
                        p.copy(
                            role = ParticipantRole.STAGE_SPEAKER,
                            seatId = availableSeat.id,
                            isReqToPresent = false,
                            isRequestedToCall = false
                        )
                    } else p
                })
            }

            sendSystemMessage(roomId, "${participant.name} joined stage seat #${availableSeat.id} 🎤")
        }
    }

    fun removeParticipantFromStage(roomId: String, participantIdentity: String) {
        val room = _rooms.value.find { it.id == roomId } ?: return
        val updatedSeats = room.seats.map { s ->
            if (s.assignedParticipant == participantIdentity) {
                s.copy(occupied = false, assignedParticipant = null, participantName = null, avatarEmoji = "👤", role = "GUEST")
            } else s
        }

        _rooms.update { list ->
            list.map { r -> if (r.id == roomId) r.copy(seats = updatedSeats) else r }
        }
        _currentRoom.update { current ->
            if (current?.id == roomId) current.copy(seats = updatedSeats) else current
        }

        _participants.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to list.map { p ->
                if (p.identity == participantIdentity) {
                    p.copy(role = ParticipantRole.VIEWER, seatId = -1)
                } else p
            })
        }
    }

    fun toggleSeatLock(roomId: String, seatId: Int) {
        val room = _rooms.value.find { it.id == roomId } ?: return
        val updatedSeats = room.seats.map { s ->
            if (s.id == seatId) s.copy(locked = !s.locked) else s
        }
        _rooms.update { list ->
            list.map { r -> if (r.id == roomId) r.copy(seats = updatedSeats) else r }
        }
        _currentRoom.update { current ->
            if (current?.id == roomId) current.copy(seats = updatedSeats) else current
        }
    }

    fun makeAdmin(roomId: String, participantIdentity: String) {
        _participants.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to list.map { p ->
                if (p.identity == participantIdentity) p.copy(role = ParticipantRole.ADMIN) else p
            })
        }
        sendSystemMessage(roomId, "Participant made room moderator 🛡️")
    }

    fun removeAdmin(roomId: String, participantIdentity: String) {
        _participants.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to list.map { p ->
                if (p.identity == participantIdentity) p.copy(role = ParticipantRole.VIEWER) else p
            })
        }
    }

    fun muteParticipantAudio(roomId: String, participantIdentity: String, muted: Boolean) {
        _participants.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to list.map { p ->
                if (p.identity == participantIdentity) p.copy(isMutedAudio = muted) else p
            })
        }
    }

    fun blockParticipant(roomId: String, participantIdentity: String) {
        _participants.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to list.map { p ->
                if (p.identity == participantIdentity) p.copy(isBlocked = true) else p
            })
        }
        sendSystemMessage(roomId, "Participant blocked from room")
    }

    fun setFilter(filter: BeautifyFilter) {
        _currentFilter.value = filter
    }

    fun toggleMic() { _isMicMuted.value = !_isMicMuted.value }
    fun toggleVideo() { _isVideoMuted.value = !_isVideoMuted.value }
    fun flipCamera() { _isFrontCamera.value = !_isFrontCamera.value }
    fun playSfx(sfx: String) { _lastPlayedSfx.value = sfx }

    private fun sendSystemMessage(roomId: String, text: String) {
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderIdentity = "system",
            senderName = "System",
            text = text,
            type = MessageType.SYSTEM
        )
        _chatMessages.update { map ->
            val list = map[roomId] ?: emptyList()
            map + (roomId to (list + msg))
        }
    }

    private fun startLiveSimulation() {
        scope.launch {
            val randomSenders = listOf(
                Pair("Aurora_Glow", "✨"),
                Pair("CyberKnight", "⚡"),
                Pair("VibeMaster", "🎧"),
                Pair("PixelNova", "👾"),
                Pair("DragonSlayer", "🐉"),
                Pair("TokyoDreamer", "🌸")
            )

            val randomComments = listOf(
                "Incredible vibe right here! 💖",
                "Let's win this battle!! 🚀",
                "Audio is so soothing ☕",
                "Send more gifts everyone!!",
                "Can you play that synth track again? 🔥",
                "Greeting from Tokyo! 🗾",
                "This 60fps feed is buttery smooth ✨",
                "Team Alpha for the win! 🛡️"
            )

            while (true) {
                delay(3500)
                val current = _currentRoom.value
                if (current != null && current.isLive && current.enableChat) {
                    val sender = randomSenders.random()
                    val isGiftEvent = (1..5).random() == 1

                    if (isGiftEvent) {
                        val gift = PredefinedGifts.ALL_GIFTS.take(5).random()
                        val count = listOf(1, 3, 5, 10).random()
                        val msg = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            senderIdentity = "sim_${sender.first}",
                            senderName = sender.first,
                            senderAvatar = sender.second,
                            text = "Sent $count x ${gift.name} ${gift.iconEmoji}!",
                            type = MessageType.GIFT,
                            gift = gift,
                            giftCount = count
                        )
                        _chatMessages.update { map ->
                            val list = map[current.id] ?: emptyList()
                            map + (current.id to (list + msg))
                        }
                        if (current.pkState.isActive) {
                            val rivalDelta = (100..400).random()
                            _currentRoom.update { r ->
                                r?.copy(
                                    pkState = r.pkState.copy(
                                        myScore = r.pkState.myScore + (gift.coinCost * count),
                                        targetScore = r.pkState.targetScore + rivalDelta
                                    )
                                )
                            }
                        }
                    } else {
                        val comment = randomComments.random()
                        val msg = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            senderIdentity = "sim_${sender.first}",
                            senderName = sender.first,
                            senderAvatar = sender.second,
                            text = comment,
                            type = MessageType.TEXT
                        )
                        _chatMessages.update { map ->
                            val list = map[current.id] ?: emptyList()
                            map + (current.id to (list + msg))
                        }
                    }

                    _currentRoom.update { r ->
                        r?.copy(
                            viewerCount = r.viewerCount + (-2..5).random(),
                            likesCount = r.likesCount + (1..8).random()
                        )
                    }
                }
            }
        }
    }
}
