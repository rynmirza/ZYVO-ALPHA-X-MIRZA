package com.example.zyvo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zyvo.data.ZyvoRepository
import com.example.zyvo.model.*
import kotlinx.coroutines.flow.*

class LiveStreamViewModel(
    private val repository: ZyvoRepository = ZyvoRepository()
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn

    val currentUserIdentity: String = repository.currentUserIdentity
    val currentUserName: String = repository.currentUserName
    val currentUserAvatar: String = repository.currentUserAvatar

    fun loginWithGoogle(displayName: String, email: String, avatarEmoji: String) {
        repository.loginWithGoogle(displayName, email, avatarEmoji)
    }

    fun logout() {
        repository.logout()
    }

    val currentUserProfile: StateFlow<UserProfile> = repository.currentUserProfile
    val userProfiles: StateFlow<Map<String, UserProfile>> = repository.userProfiles
    val userCoinBalance: StateFlow<Int> = repository.userCoinBalance
    val userBeansBalance: StateFlow<Int> = repository.userBeansBalance
    val walletTransactions: StateFlow<List<WalletTransaction>> = repository.walletTransactions

    val followingUserIds: StateFlow<Set<String>> = repository.followingUserIds
    val blockedUserIds: StateFlow<Set<String>> = repository.blockedUserIds
    val conversations: StateFlow<List<ConversationSummary>> = repository.conversations
    val directMessages: StateFlow<Map<String, List<DirectMessage>>> = repository.directMessages

    val rooms: StateFlow<List<LiveRoom>> = repository.rooms
    val currentRoom: StateFlow<LiveRoom?> = repository.currentRoom
    val activeFloatingGifts: StateFlow<List<ChatMessage>> = repository.activeFloatingGifts
    val currentFilter: StateFlow<BeautifyFilter> = repository.currentFilter
    val isMicMuted: StateFlow<Boolean> = repository.isMicMuted
    val isVideoMuted: StateFlow<Boolean> = repository.isVideoMuted
    val isFrontCamera: StateFlow<Boolean> = repository.isFrontCamera
    val lastPlayedSfx: StateFlow<String?> = repository.lastPlayedSfx

    // Navigation Tab: 0=Explore, 1=Following, 2=Rankings, 3=Profile/Wallet, 4=Analytics
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow<RoomType?>(null) // null = ALL
    val selectedCategory: StateFlow<RoomType?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Dialog & Sheet States
    private val _selectedUserProfile = MutableStateFlow<UserProfile?>(null)
    val selectedUserProfile: StateFlow<UserProfile?> = _selectedUserProfile.asStateFlow()

    private val _showVipStoreDialog = MutableStateFlow(false)
    val showVipStoreDialog: StateFlow<Boolean> = _showVipStoreDialog.asStateFlow()

    private val _showRechargeDialog = MutableStateFlow(false)
    val showRechargeDialog: StateFlow<Boolean> = _showRechargeDialog.asStateFlow()

    private val _showWithdrawalDialog = MutableStateFlow(false)
    val showWithdrawalDialog: StateFlow<Boolean> = _showWithdrawalDialog.asStateFlow()

    private val _showTransactionsDialog = MutableStateFlow(false)
    val showTransactionsDialog: StateFlow<Boolean> = _showTransactionsDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _activeDmPeerUserId = MutableStateFlow<String?>(null)
    val activeDmPeerUserId: StateFlow<String?> = _activeDmPeerUserId.asStateFlow()

    private val _showGiftDialog = MutableStateFlow(false)
    val showGiftDialog: StateFlow<Boolean> = _showGiftDialog.asStateFlow()

    private val _showParticipantsSheet = MutableStateFlow(false)
    val showParticipantsSheet: StateFlow<Boolean> = _showParticipantsSheet.asStateFlow()

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

    private val _showSoundboard = MutableStateFlow(false)
    val showSoundboard: StateFlow<Boolean> = _showSoundboard.asStateFlow()

    private val _showStreamStats = MutableStateFlow(false)
    val showStreamStats: StateFlow<Boolean> = _showStreamStats.asStateFlow()

    private val _showCreateRoomSheet = MutableStateFlow(false)
    val showCreateRoomSheet: StateFlow<Boolean> = _showCreateRoomSheet.asStateFlow()

    // Filtered rooms
    val filteredRooms: StateFlow<List<LiveRoom>> = combine(
        rooms,
        _selectedCategory,
        _searchQuery
    ) { allRooms, category, query ->
        allRooms.filter { room ->
            val matchesCategory = category == null || room.roomType == category
            val matchesQuery = query.isBlank() ||
                    room.title.contains(query, ignoreCase = true) ||
                    room.hostName.contains(query, ignoreCase = true) ||
                    room.tags.any { it.contains(query, ignoreCase = true) }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Followed Rooms
    val followedRooms: StateFlow<List<LiveRoom>> = combine(
        rooms,
        followingUserIds
    ) { allRooms, following ->
        allRooms.filter { following.contains(it.creatorIdentity) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat messages for current room
    val currentRoomChat: StateFlow<List<ChatMessage>> = combine(
        currentRoom,
        repository.chatMessages
    ) { room, chatMap ->
        if (room == null) emptyList()
        else chatMap[room.id] ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Participants for current room
    val currentRoomParticipants: StateFlow<List<Participant>> = combine(
        currentRoom,
        repository.participants
    ) { room, partMap ->
        if (room == null) emptyList()
        else partMap[room.id] ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSelectedCategory(category: RoomType?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // User Profile Actions
    fun openUserProfile(userId: String) {
        val profile = repository.getUserProfile(userId)
        _selectedUserProfile.value = profile
    }

    fun closeUserProfile() {
        _selectedUserProfile.value = null
    }

    fun followUser(userId: String) {
        repository.followUser(userId)
        if (_selectedUserProfile.value?.userId == userId) {
            _selectedUserProfile.value = repository.getUserProfile(userId)
        }
    }

    fun unfollowUser(userId: String) {
        repository.unfollowUser(userId)
        if (_selectedUserProfile.value?.userId == userId) {
            _selectedUserProfile.value = repository.getUserProfile(userId)
        }
    }

    fun blockUser(userId: String) {
        repository.blockUser(userId)
        closeUserProfile()
    }

    fun unblockUser(userId: String) {
        repository.unblockUser(userId)
    }

    fun reportUser(userId: String, reason: String) {
        repository.reportUser(userId, reason)
    }

    fun updateProfile(displayName: String, bio: String, gender: String, location: String, avatarEmoji: String) {
        repository.updateProfile(displayName, bio, gender, location, avatarEmoji)
    }

    // Wallet & Membership
    fun rechargeCoins(packageTitle: String, coinAmount: Int, bonusCoins: Int, usdPrice: Double, paymentMethod: String) {
        repository.rechargeCoins(packageTitle, coinAmount, bonusCoins, usdPrice, paymentMethod)
        _showRechargeDialog.value = false
    }

    fun buyVipPackage(tier: VipTier, months: Int, coinCost: Int) {
        repository.buyVipPackage(tier, months, coinCost)
        _showVipStoreDialog.value = false
    }

    fun requestWithdrawal(beansAmount: Int, payoutMethod: String, accountDetail: String) {
        repository.requestWithdrawal(beansAmount, payoutMethod, accountDetail)
        _showWithdrawalDialog.value = false
    }

    // Direct Messaging
    fun openDmChat(peerUserId: String) {
        _activeDmPeerUserId.value = peerUserId
        closeUserProfile()
    }

    fun closeDmChat() {
        _activeDmPeerUserId.value = null
    }

    fun sendDirectMessage(peerUserId: String, text: String, gift: Gift? = null) {
        repository.sendDirectMessage(peerUserId, text, gift)
    }

    // Room Actions
    fun joinRoom(roomId: String) {
        repository.joinRoom(roomId)
    }

    fun leaveRoom() {
        repository.leaveRoom()
        _showGiftDialog.value = false
        _showParticipantsSheet.value = false
        _showFilterSheet.value = false
        _showSoundboard.value = false
        _showStreamStats.value = false
    }

    fun createRoom(
        title: String,
        roomType: RoomType,
        category: String,
        tags: List<String>,
        isPrivate: Boolean = false,
        password: String? = null
    ) {
        repository.createRoom(title, roomType, category, tags, isPrivate, password)
        _showCreateRoomSheet.value = false
    }

    fun sendTextMessage(text: String, mention: String? = null) {
        val room = currentRoom.value ?: return
        if (text.isNotBlank()) {
            repository.sendTextMessage(room.id, text, mention)
        }
    }

    fun sendGift(gift: Gift, count: Int = 1) {
        val room = currentRoom.value ?: return
        repository.sendGift(room.id, gift, count)
    }

    fun sendLike() {
        val room = currentRoom.value ?: return
        repository.sendLike(room.id)
    }

    fun toggleChat(enabled: Boolean) {
        val room = currentRoom.value ?: return
        repository.toggleChatEnabled(room.id, enabled)
    }

    fun requestToPresent(seatId: Int = -1) {
        val room = currentRoom.value ?: return
        repository.requestToPresent(room.id, seatId)
    }

    fun cancelRequestToPresent() {
        val room = currentRoom.value ?: return
        repository.cancelRequestToPresent(room.id)
    }

    fun inviteParticipantToStage(participantIdentity: String, seatId: Int = -1) {
        val room = currentRoom.value ?: return
        repository.inviteParticipantToStage(room.id, participantIdentity, seatId)
    }

    fun removeParticipantFromStage(participantIdentity: String) {
        val room = currentRoom.value ?: return
        repository.removeParticipantFromStage(room.id, participantIdentity)
    }

    fun toggleSeatLock(seatId: Int) {
        val room = currentRoom.value ?: return
        repository.toggleSeatLock(room.id, seatId)
    }

    fun makeAdmin(participantIdentity: String) {
        val room = currentRoom.value ?: return
        repository.makeAdmin(room.id, participantIdentity)
    }

    fun removeAdmin(participantIdentity: String) {
        val room = currentRoom.value ?: return
        repository.removeAdmin(room.id, participantIdentity)
    }

    fun muteParticipantAudio(participantIdentity: String, muted: Boolean) {
        val room = currentRoom.value ?: return
        repository.muteParticipantAudio(room.id, participantIdentity, muted)
    }

    fun blockParticipant(participantIdentity: String) {
        val room = currentRoom.value ?: return
        repository.blockParticipant(room.id, participantIdentity)
    }

    fun setFilter(filter: BeautifyFilter) { repository.setFilter(filter) }
    fun toggleMic() = repository.toggleMic()
    fun toggleVideo() = repository.toggleVideo()
    fun flipCamera() = repository.flipCamera()
    fun playSfx(sfx: String) = repository.playSfx(sfx)

    // Visibility Setters
    fun setShowVipStoreDialog(show: Boolean) { _showVipStoreDialog.value = show }
    fun setShowRechargeDialog(show: Boolean) { _showRechargeDialog.value = show }
    fun setShowWithdrawalDialog(show: Boolean) { _showWithdrawalDialog.value = show }
    fun setShowTransactionsDialog(show: Boolean) { _showTransactionsDialog.value = show }
    fun setShowSettingsDialog(show: Boolean) { _showSettingsDialog.value = show }
    fun setShowGiftDialog(show: Boolean) { _showGiftDialog.value = show }
    fun setShowParticipantsSheet(show: Boolean) { _showParticipantsSheet.value = show }
    fun setShowFilterSheet(show: Boolean) { _showFilterSheet.value = show }
    fun setShowSoundboard(show: Boolean) { _showSoundboard.value = show }
    fun setShowStreamStats(show: Boolean) { _showStreamStats.value = show }
    fun setShowCreateRoomSheet(show: Boolean) { _showCreateRoomSheet.value = show }
}
