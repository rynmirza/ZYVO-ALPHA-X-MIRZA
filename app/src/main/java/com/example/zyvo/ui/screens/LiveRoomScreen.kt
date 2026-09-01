package com.example.zyvo.ui.screens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.zyvo.model.*
import com.example.zyvo.ui.components.*
import com.example.zyvo.ui.theme.*
import com.example.zyvo.ui.viewmodel.LiveStreamViewModel

@Composable
fun LiveRoomScreen(
    room: LiveRoom,
    viewModel: LiveStreamViewModel,
    onLeaveRoom: () -> Unit
) {
    val currentRoomChat by viewModel.currentRoomChat.collectAsState()
    val currentRoomParticipants by viewModel.currentRoomParticipants.collectAsState()
    val activeFloatingGifts by viewModel.activeFloatingGifts.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val isMicMuted by viewModel.isMicMuted.collectAsState()
    val isVideoMuted by viewModel.isVideoMuted.collectAsState()
    val userCoinBalance by viewModel.userCoinBalance.collectAsState()
    val currentUserProfile by viewModel.currentUserProfile.collectAsState()

    // Dialog states
    val showGiftDialog by viewModel.showGiftDialog.collectAsState()
    val showParticipantsSheet by viewModel.showParticipantsSheet.collectAsState()
    val showFilterSheet by viewModel.showFilterSheet.collectAsState()
    val showSoundboard by viewModel.showSoundboard.collectAsState()
    val showStreamStats by viewModel.showStreamStats.collectAsState()
    val showRoomCoverSheet by viewModel.showRoomCoverSheet.collectAsState()

    val isHost = room.creatorIdentity == viewModel.currentUserIdentity
    val currentUserParticipant = currentRoomParticipants.find { it.identity == viewModel.currentUserIdentity }
    val isHandRaised = currentUserParticipant?.isRequestedToCall == true || currentUserParticipant?.isReqToPresent == true

    Scaffold(
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                // Top Live Room Bar
                LiveRoomTopBar(
                    room = room,
                    isHost = isHost,
                    onLeave = onLeaveRoom,
                    onOpenParticipants = { viewModel.setShowParticipantsSheet(true) },
                    onOpenHostProfile = { viewModel.openUserProfile(room.creatorIdentity) },
                    onOpenRoomCover = { viewModel.setShowRoomCoverSheet(true) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mode-Specific Stage Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (room.roomType) {
                        RoomType.SINGLE_LIVE -> {
                            VideoFeedTile(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                hostName = room.hostName,
                                avatarEmoji = room.hostAvatar,
                                avatarUrl = room.hostAvatarUrl,
                                roomCoverUrl = room.roomCoverUrl,
                                coverStyle = room.coverStyle,
                                isSpeaking = true,
                                filter = currentFilter,
                                badgeText = "🔴 LIVE BROADCAST",
                                badgeColor = LiveRed
                            )
                        }
                        RoomType.MULTI_GUEST -> {
                            MultiGuestVideoGrid(
                                modifier = Modifier.fillMaxSize(),
                                seats = room.seats,
                                hostName = room.hostName,
                                hostAvatar = room.hostAvatar,
                                hostAvatarUrl = room.hostAvatarUrl,
                                hostCoverUrl = room.roomCoverUrl,
                                filter = currentFilter,
                                onSeatClick = { seat ->
                                    if (!seat.occupied && !seat.locked) {
                                        viewModel.inviteParticipantToStage(viewModel.currentUserIdentity, seat.id)
                                    } else if (isHost) {
                                        viewModel.toggleSeatLock(seat.id)
                                    }
                                }
                            )
                        }
                        RoomType.AUDIO_STAGE -> {
                            AudioStageSeats(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                seats = room.seats,
                                isHost = isHost,
                                onSeatClick = { seat ->
                                    if (!seat.occupied && !seat.locked) {
                                        viewModel.requestToPresent(seat.id)
                                    } else if (isHost) {
                                        viewModel.toggleSeatLock(seat.id)
                                    }
                                }
                            )
                        }
                        RoomType.PK_BATTLE -> {
                            PkBattleArena(
                                modifier = Modifier.fillMaxSize(),
                                pkState = room.pkState,
                                hostName = room.hostName,
                                hostAvatar = room.hostAvatar,
                                hostAvatarUrl = room.hostAvatarUrl,
                                hostCoverUrl = room.roomCoverUrl,
                                filter = currentFilter
                            )
                        }
                        RoomType.TEAM_MODE -> {
                            TeamBattleArena(
                                modifier = Modifier.fillMaxSize(),
                                teamState = room.teamState,
                                hostName = room.hostName,
                                hostAvatar = room.hostAvatar,
                                hostAvatarUrl = room.hostAvatarUrl,
                                hostCoverUrl = room.roomCoverUrl,
                                filter = currentFilter
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Real-time Chat & Gifting Overlay
                LiveChatOverlay(
                    modifier = Modifier.fillMaxWidth(),
                    messages = currentRoomChat,
                    floatingGifts = activeFloatingGifts,
                    enableChat = room.enableChat,
                    onSendMessage = { text -> viewModel.sendTextMessage(text) },
                    onSendLike = { viewModel.sendLike() },
                    onOpenGiftDialog = { viewModel.setShowGiftDialog(true) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Broadcaster & Viewer Toolbar
                BroadcasterToolbar(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    isHost = isHost,
                    isMicMuted = isMicMuted,
                    isVideoMuted = isVideoMuted,
                    onFlipCamera = { viewModel.flipCamera() },
                    onToggleMic = { viewModel.toggleMic() },
                    onToggleVideo = { viewModel.toggleVideo() },
                    onOpenFilters = { viewModel.setShowFilterSheet(true) },
                    onOpenSoundboard = { viewModel.setShowSoundboard(true) },
                    onOpenStats = { viewModel.setShowStreamStats(true) },
                    onOpenParticipants = { viewModel.setShowParticipantsSheet(true) },
                    onOpenRoomCover = { viewModel.setShowRoomCoverSheet(true) },
                    onRaiseHand = {
                        if (isHandRaised) viewModel.cancelRequestToPresent()
                        else viewModel.requestToPresent()
                    },
                    isHandRaised = isHandRaised
                )
            }

            // Bottom Sheets
            if (showRoomCoverSheet) {
                RoomCoverManagerDialog(
                    room = room,
                    currentUserProfile = currentUserProfile,
                    onDismiss = { viewModel.setShowRoomCoverSheet(false) },
                    onApplyRoomCover = { coverUrl, style ->
                        viewModel.updateRoomCover(room.id, coverUrl, style)
                    },
                    onUpdateBroadcasterProfilePic = { newPic ->
                        viewModel.updateBroadcasterProfilePic(room.creatorIdentity, newPic)
                    }
                )
            }

            if (showGiftDialog) {
                GiftDialog(
                    userCoinBalance = userCoinBalance,
                    onDismiss = { viewModel.setShowGiftDialog(false) },
                    onSendGift = { gift, count -> viewModel.sendGift(gift, count) }
                )
            }

            if (showParticipantsSheet) {
                ParticipantsSheet(
                    participants = currentRoomParticipants,
                    currentUserId = viewModel.currentUserIdentity,
                    isHostOrAdmin = isHost,
                    onDismiss = { viewModel.setShowParticipantsSheet(false) },
                    onInviteToStage = { id -> viewModel.inviteParticipantToStage(id) },
                    onRemoveFromStage = { id -> viewModel.removeParticipantFromStage(id) },
                    onMakeAdmin = { id -> viewModel.makeAdmin(id) },
                    onRemoveAdmin = { id -> viewModel.removeAdmin(id) },
                    onMuteAudio = { id, muted -> viewModel.muteParticipantAudio(id, muted) },
                    onBlockParticipant = { id -> viewModel.blockParticipant(id) }
                )
            }

            if (showFilterSheet) {
                BeautifyFilterSheet(
                    activeFilter = currentFilter,
                    onSelectFilter = { viewModel.setFilter(it) },
                    onDismiss = { viewModel.setShowFilterSheet(false) }
                )
            }

            if (showSoundboard) {
                AudioSoundboardDialog(
                    onDismiss = { viewModel.setShowSoundboard(false) },
                    onPlaySfx = { viewModel.playSfx(it) }
                )
            }

            if (showStreamStats) {
                StreamStatsDialog(
                    stats = room.streamStats,
                    onDismiss = { viewModel.setShowStreamStats(false) }
                )
            }
        }
    }
}

@Composable
fun LiveRoomTopBar(
    room: LiveRoom,
    isHost: Boolean,
    onLeave: () -> Unit,
    onOpenParticipants: () -> Unit,
    onOpenHostProfile: () -> Unit = {},
    onOpenRoomCover: () -> Unit = {}
) {
    val context = LocalContext.current
    val hostPic = room.roomCoverUrl ?: room.hostAvatarUrl

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Host Info Pill or Broadcast Status Pill
        if (isHost) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.horizontalGradient(listOf(LiveRed.copy(alpha = 0.85f), ElectricMagenta.copy(alpha = 0.85f))))
                    .border(1.dp, GoldAccent, RoundedCornerShape(24.dp))
                    .clickable { onOpenRoomCover() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!hostPic.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(hostPic)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Broadcaster Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "LIVE STUDIO 📸",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurface.copy(alpha = 0.9f))
                    .border(1.dp, OverlayLight, RoundedCornerShape(24.dp))
                    .clickable { onOpenHostProfile() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!hostPic.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(hostPic)
                                .crossfade(true)
                                .build(),
                            contentDescription = room.hostName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(1.dp, GoldAccent, CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeonPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = room.hostAvatar, fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = room.hostName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${room.likesCount} ❤️",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricMagenta,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Right Actions: Viewers Count + Close Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Viewers Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface.copy(alpha = 0.9f))
                    .clickable { onOpenParticipants() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Viewers",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${room.viewerCount}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // Close / End / Leave Stream Button
            if (isHost) {
                Button(
                    onClick = onLeave,
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("end_broadcast_button")
                ) {
                    Text(
                        text = "End Live",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                IconButton(
                    onClick = onLeave,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .testTag("leave_room_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Leave Stream",
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun TeamBattleArena(
    modifier: Modifier = Modifier,
    teamState: TeamState,
    hostName: String,
    hostAvatar: String,
    hostAvatarUrl: String? = null,
    hostCoverUrl: String? = null,
    filter: BeautifyFilter = BeautifyFilter.ORIGINAL
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        // Team Tug-of-War Score Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkBackground)
                .border(1.dp, OverlayLight, RoundedCornerShape(12.dp))
                .padding(6.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️ ${teamState.teamName} (${teamState.myTeamScore})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlue
                    )
                    Text(
                        text = "${teamState.enemyTeamScore} (${teamState.enemyTeamName}) ⚔️",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PkRed
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (teamState.myTeamScore.toFloat() / (teamState.myTeamScore + teamState.enemyTeamScore).coerceAtLeast(1)).coerceIn(0.1f, 0.9f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberBlue,
                    trackColor = PkRed
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Team Split Arena
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            VideoFeedTile(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                hostName = hostName,
                avatarEmoji = hostAvatar,
                avatarUrl = hostAvatarUrl,
                roomCoverUrl = hostCoverUrl,
                isSpeaking = true,
                badgeText = "MY SQUAD",
                badgeColor = CyberBlue,
                filter = filter
            )
            VideoFeedTile(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                hostName = "Titan Prime",
                avatarEmoji = "🛡️",
                isSpeaking = false,
                badgeText = "ENEMY SQUAD",
                badgeColor = PkRed
            )
        }
    }
}

