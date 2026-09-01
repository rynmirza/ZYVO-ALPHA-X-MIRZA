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

    fun loginWithGoogle(displayName: String, email: String, avatarEmoji: String) {
        val username = email.substringBefore("@").replace(".", "_")
        _currentUserProfile.update { current ->
            current.copy(
                displayName = displayName,
                username = username,
                avatarEmoji = avatarEmoji,
                badges = listOf("Google User", "Verified Creator", "VIP 7")
            )
        }
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // Current User Profile
    private val _currentUserProfile = MutableStateFlow(
        UserProfile(
            userId = "user_me",
            username = "alex_vance",
            displayName = "Alex Vance",
            avatarEmoji = "🚀",
            coverGradientIndex = 0,
            bio = "Official Zyvo Streamer & Gaming Enthusiast. Streaming Cyberpunk & Synthwave daily! ⚡",
            gender = "Male",
            location = "San Francisco, CA 🇺🇸",
            userLevel = 14,
            userXp = 3850,
            nextLevelXp = 5000,
            wealthLevel = 9,
            hostLevel = 18,
            vipTier = VipTier.VIP_3,
            vipExpiresTimestamp = System.currentTimeMillis() + (30L * 24 * 3600 * 1000), // 30 days
            followersCount = 1420,
            followingCount = 28,
            likesCount = 28900,
            diamondsEarnedTotal = 34250,
            giftsReceivedTotal = 480,
            liveStreamsCount = 52,
            badges = listOf("Verified Creator", "VIP 3", "Synth DJ", "PK Master"),
            isLiveNow = false
        )
    )
    val currentUserProfile: StateFlow<UserProfile> = _currentUserProfile.asStateFlow()

    // All Users Registry
    private val _userProfiles = MutableStateFlow<Map<String, UserProfile>>(emptyMap())
    val userProfiles: StateFlow<Map<String, UserProfile>> = _userProfiles.asStateFlow()

    // Wallet balances
    private val _userCoinBalance = MutableStateFlow(15450)
    val userCoinBalance: StateFlow<Int> = _userCoinBalance.asStateFlow()

    private val _userBeansBalance = MutableStateFlow(34250) // 34,250 Beans = $342.50 USD
    val userBeansBalance: StateFlow<Int> = _userBeansBalance.asStateFlow()

    // Transaction History
    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    // Following & Blocked User IDs
    private val _followingUserIds = MutableStateFlow<Set<String>>(setOf("dj_kai", "pixel_queen", "marcus_voice"))
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
            ),
            "dj_kai" to UserProfile(
                userId = "dj_kai",
                username = "kai_sterling",
                displayName = "Kai Sterling",
                avatarEmoji = "🎧",
                coverGradientIndex = 1,
                bio = "Electronic Synthwave DJ & Producer. Resident DJ on Zyvo Live! 🎶",
                gender = "Male",
                location = "Berlin, Germany 🇩🇪",
                userLevel = 28,
                wealthLevel = 19,
                hostLevel = 32,
                vipTier = VipTier.VIP_5,
                followersCount = 18900,
                followingCount = 142,
                likesCount = 142000,
                diamondsEarnedTotal = 284000,
                badges = listOf("Top Broadcaster", "VIP 5", "Music Guru"),
                isFollowedByCurrentUser = true,
                isLiveNow = true,
                currentRoomId = "room_single_101"
            ),
            "pixel_queen" to UserProfile(
                userId = "pixel_queen",
                username = "pixel_queen",
                displayName = "Elena 'PixelQueen'",
                avatarEmoji = "🎮",
                coverGradientIndex = 2,
                bio = "Pro FPS Gamer & Streamer. Host of the Daily Esports Stage 👾",
                gender = "Female",
                location = "Seoul, South Korea 🇰🇷",
                userLevel = 35,
                wealthLevel = 25,
                hostLevel = 40,
                vipTier = VipTier.SVIP_2,
                followersCount = 42500,
                followingCount = 89,
                likesCount = 389000,
                diamondsEarnedTotal = 690000,
                badges = listOf("Super Broadcaster", "SVIP 2", "Esports Champion"),
                isFollowedByCurrentUser = true,
                isLiveNow = true,
                currentRoomId = "room_multi_202"
            ),
            "marcus_voice" to UserProfile(
                userId = "marcus_voice",
                username = "marcus_vance",
                displayName = "Marcus Vance",
                avatarEmoji = "☕",
                coverGradientIndex = 3,
                bio = "Host of Midnight Coffee Audio Stage. Storyteller & Vocal Artist 🎙️",
                gender = "Male",
                location = "London, UK 🇬🇧",
                userLevel = 22,
                wealthLevel = 14,
                hostLevel = 26,
                vipTier = VipTier.VIP_2,
                followersCount = 9800,
                followingCount = 65,
                likesCount = 76000,
                diamondsEarnedTotal = 145000,
                badges = listOf("Voice Artist", "VIP 2", "Coffee Host"),
                isFollowedByCurrentUser = true,
                isLiveNow = true,
                currentRoomId = "room_audio_303"
            ),
            "apex_arenas" to UserProfile(
                userId = "apex_arenas",
                username = "apex_arenas",
                displayName = "Apex Arenas",
                avatarEmoji = "⚔️",
                coverGradientIndex = 4,
                bio = "Undefeated 1v1 PK Arena Broadcaster. Challenge me if you dare 💥",
                gender = "Male",
                location = "Austin, TX 🇺🇸",
                userLevel = 31,
                wealthLevel = 30,
                hostLevel = 38,
                vipTier = VipTier.SVIP_1,
                followersCount = 31000,
                followingCount = 110,
                likesCount = 290000,
                diamondsEarnedTotal = 520000,
                badges = listOf("PK Legend", "SVIP 1", "War Titan"),
                isFollowedByCurrentUser = false,
                isLiveNow = true,
                currentRoomId = "room_pk_404"
            ),
            "team_crimson" to UserProfile(
                userId = "team_crimson",
                username = "team_crimson",
                displayName = "Team Crimson",
                avatarEmoji = "🛡️",
                coverGradientIndex = 0,
                bio = "3v3 Team Battle Championship Stage. Squad up and battle! 🔥",
                gender = "Group",
                location = "Global Arena 🌍",
                userLevel = 45,
                wealthLevel = 42,
                hostLevel = 50,
                vipTier = VipTier.SVIP_3,
                followersCount = 88000,
                followingCount = 12,
                likesCount = 950000,
                diamondsEarnedTotal = 1850000,
                badges = listOf("Hall of Fame", "SVIP 3", "Team Champion"),
                isFollowedByCurrentUser = false,
                isLiveNow = true,
                currentRoomId = "room_team_505"
            ),
            "luna_star" to UserProfile(
                userId = "luna_star",
                username = "luna_star",
                displayName = "Luna Star ✨",
                avatarEmoji = "🌟",
                bio = "Cosmic giver & top donor! Supporting creators worldwide 💖",
                userLevel = 48,
                wealthLevel = 55,
                vipTier = VipTier.SVIP_3,
                followersCount = 15400,
                followingCount = 320,
                likesCount = 120000
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
            roomType = RoomType.SINGLE_LIVE,
            category = "Top Host",
            tags = listOf("TopHost", "SVIP7", "Lv89", "Queen", "Official"),
            viewerCount = 98400,
            likesCount = 1650000,
            enableChat = true
        )

        val singleRoom = LiveRoom(
            id = "room_single_101",
            title = "🔥 Cyberpunk Beats & Chill DJ Session",
            description = "Live electronic synthwave set with real-time audio reactive visuals and community Q&A!",
            creatorIdentity = "dj_kai",
            hostName = "Kai Sterling",
            hostAvatar = "🎧",
            roomType = RoomType.SINGLE_LIVE,
            category = "Music",
            tags = listOf("Synthwave", "DJ", "Chill", "Electronic"),
            viewerCount = 4280,
            likesCount = 28400,
            enableChat = true
        )

        val multiRoom = LiveRoom(
            id = "room_multi_202",
            title = "👥 Gaming Squad Watchparty & Community Stage",
            description = "Top 5 streamers discussing the latest esports tournament. Open guest slots on stage!",
            creatorIdentity = "pixel_queen",
            hostName = "Elena 'PixelQueen'",
            hostAvatar = "🎮",
            roomType = RoomType.MULTI_GUEST,
            category = "Gaming",
            tags = listOf("Esports", "MultiGuest", "Gaming", "Talk"),
            viewerCount = 3190,
            likesCount = 19200,
            seats = listOf(
                Seat(id = 1, occupied = true, assignedParticipant = "pixel_queen", participantName = "Elena (Host)", avatarEmoji = "🎮", role = "HOST"),
                Seat(id = 2, occupied = true, assignedParticipant = "ghost_rider", participantName = "GhostRider", avatarEmoji = "🏍️", role = "SPEAKER"),
                Seat(id = 3, occupied = true, assignedParticipant = "neon_samurai", participantName = "NeonSamurai", avatarEmoji = "⚔️", role = "SPEAKER"),
                Seat(id = 4, occupied = false, locked = false),
                Seat(id = 5, occupied = false, locked = true)
            )
        )

        val audioRoom = LiveRoom(
            id = "room_audio_303",
            title = "🎙️ Midnight Coffee: Deep Talks & Open Mic",
            description = "Grab a cup of warm tea or coffee. Request a mic slot to share your stories or music!",
            creatorIdentity = "marcus_voice",
            hostName = "Marcus Vance",
            hostAvatar = "☕",
            roomType = RoomType.AUDIO_STAGE,
            category = "Podcast",
            tags = listOf("AudioOnly", "Chill", "Podcast", "OpenMic"),
            viewerCount = 1850,
            likesCount = 12400,
            seats = listOf(
                Seat(id = 1, occupied = true, assignedParticipant = "marcus_voice", participantName = "Marcus (Host)", avatarEmoji = "☕", role = "HOST"),
                Seat(id = 2, occupied = true, assignedParticipant = "luna_voice", participantName = "Luna", avatarEmoji = "🌙", role = "SPEAKER"),
                Seat(id = 3, occupied = true, assignedParticipant = "leo_jazz", participantName = "Leo Jazz", avatarEmoji = "🎷", role = "SPEAKER"),
                Seat(id = 4, occupied = false, locked = false),
                Seat(id = 5, occupied = false, locked = false),
                Seat(id = 6, occupied = false, locked = false)
            )
        )

        val pkRoom = LiveRoom(
            id = "room_pk_404",
            title = "⚔️ HIGH STAKES 1v1 PK BATTLE: Apex vs Shadow",
            description = "1v1 Gift Battle Arena! Host sending legendary dragon combos to claim victory!",
            creatorIdentity = "apex_arenas",
            hostName = "Apex Arenas",
            hostAvatar = "⚔️",
            roomType = RoomType.PK_BATTLE,
            category = "PK Arena",
            tags = listOf("PK", "1v1", "Competition", "HighStakes"),
            viewerCount = 6840,
            likesCount = 59200,
            pkState = PkState(
                isActive = true,
                targetHostName = "Shadow Knight",
                targetHostAvatar = "🐺",
                myScore = 14500,
                targetScore = 12800,
                remainingSeconds = 168
            )
        )

        val teamRoom = LiveRoom(
            id = "room_team_505",
            title = "🛡️ 3v3 TEAM WAR CHAMPIONSHIP: Alpha vs Bravo",
            description = "Squad up for the ultimate 3v3 team gift battle showdown!",
            creatorIdentity = "team_crimson",
            hostName = "Team Crimson",
            hostAvatar = "🛡️",
            roomType = RoomType.TEAM_MODE,
            category = "Team Battle",
            tags = listOf("TeamBattle", "3v3", "Tournament"),
            viewerCount = 8920,
            likesCount = 84000,
            teamState = TeamState(
                isActive = true,
                teamName = "Team Alpha 🔴",
                enemyTeamName = "Team Bravo 🔵",
                myTeamScore = 48200,
                enemyTeamScore = 41500,
                myTeamMembers = listOf("user_me", "dj_kai", "marcus_voice"),
                enemyTeamMembers = listOf("shadow_k", "vortex_v", "blaze_b")
            )
        )

        _rooms.value = listOf(ceoRoom, alphaRoom, ansharahRoom, singleRoom, multiRoom, audioRoom, pkRoom, teamRoom)

        // Pre-fill initial chat messages
        _chatMessages.value = mapOf(
            "room_ceo_999" to listOf(
                ChatMessage("c01", "system", "ZYVO HQ", senderAvatar = "👑", text = "👑 WELCOME TO CEO RAYAN MIRZA OFFICIAL KEYNOTE LIVE! VIP 9 ACTIVE.", type = MessageType.SYSTEM),
                ChatMessage("c02", "vip_fan", "Lord_Vanguard", "💎", "Glory to CEO Rayan Mirza! Sent 50x Golden Dragons! 🐉"),
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
            ),
            "room_single_101" to listOf(
                ChatMessage("c1", "system", "System", senderAvatar = "📢", text = "Welcome to Kai's Synthwave Live Room! 🎉", type = MessageType.SYSTEM),
                ChatMessage("c2", "user_1", "SynthFan_99", "🎧", "That drop was insane!! 🔥"),
                ChatMessage("c3", "user_2", "Viper_X", "🌸", "Sending love from Tokyo 🇯🇵")
            ),
            "room_multi_202" to listOf(
                ChatMessage("c4", "system", "System", senderAvatar = "📢", text = "Welcome to Multi-Guest Stage! Raise hand to speak.", type = MessageType.SYSTEM),
                ChatMessage("c5", "ghost_rider", "GhostRider", "🏍️", "Who thinks Team Alpha takes the trophy?")
            )
        )

        // Pre-fill participants
        _participants.value = mapOf(
            "room_single_101" to listOf(
                Participant("dj_kai", "Kai Sterling", "🎧", role = ParticipantRole.HOST),
                Participant("user_me", "Alex Vance (You)", "🚀", role = ParticipantRole.VIEWER)
            ),
            "room_multi_202" to listOf(
                Participant("pixel_queen", "Elena 'PixelQueen'", "🎮", role = ParticipantRole.HOST, seatId = 1),
                Participant("ghost_rider", "GhostRider", "🏍️", role = ParticipantRole.STAGE_SPEAKER, seatId = 2),
                Participant("neon_samurai", "NeonSamurai", "⚔️", role = ParticipantRole.STAGE_SPEAKER, seatId = 3),
                Participant("user_me", "Alex Vance (You)", "🚀", role = ParticipantRole.VIEWER)
            )
        )
    }

    private fun initializeInitialDms() {
        val initialConversations = listOf(
            ConversationSummary(
                peerUserId = "dj_kai",
                peerDisplayName = "Kai Sterling",
                peerUsername = "@kai_sterling",
                peerAvatarEmoji = "🎧",
                peerVipTier = VipTier.VIP_5,
                lastMessageText = "Thanks for the Golden Dragon gift! Catch you on stream tomorrow 🔥",
                lastMessageTime = "14:20",
                unreadCount = 1,
                isPeerLive = true
            ),
            ConversationSummary(
                peerUserId = "pixel_queen",
                peerDisplayName = "Elena 'PixelQueen'",
                peerUsername = "@pixel_queen",
                peerAvatarEmoji = "🎮",
                peerVipTier = VipTier.SVIP_2,
                lastMessageText = "Do you want to join our guest stage in the next match?",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isPeerLive = true
            )
        )
        _conversations.value = initialConversations

        _directMessages.value = mapOf(
            "dj_kai" to listOf(
                DirectMessage("m1", "user_me", "Alex Vance", "🚀", "Awesome DJ set today Kai!", "14:15", true),
                DirectMessage("m2", "dj_kai", "Kai Sterling", "🎧", "Thanks for the Golden Dragon gift! Catch you on stream tomorrow 🔥", "14:20", false)
            ),
            "pixel_queen" to listOf(
                DirectMessage("m3", "pixel_queen", "Elena 'PixelQueen'", "🎮", "Do you want to join our guest stage in the next match?", "Yesterday", false)
            )
        )
    }

    // === USER PROFILE & ACTIONS ===

    fun getUserProfile(userId: String): UserProfile {
        if (userId == currentUserIdentity || userId == "user_me") {
            return _currentUserProfile.value
        }
        return _userProfiles.value[userId] ?: UserProfile(
            userId = userId,
            username = "user_$userId",
            displayName = "User $userId",
            avatarEmoji = "👤"
        )
    }

    fun updateProfile(displayName: String, bio: String, gender: String, location: String, avatarEmoji: String) {
        _currentUserProfile.update { profile ->
            profile.copy(
                displayName = displayName.ifBlank { profile.displayName },
                bio = bio,
                gender = gender,
                location = location,
                avatarEmoji = avatarEmoji
            )
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

        val newRoom = LiveRoom(
            id = newRoomId,
            title = title,
            creatorIdentity = currentUserIdentity,
            hostName = currentUserName,
            hostAvatar = currentUserAvatar,
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
