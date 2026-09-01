package com.example.zyvo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zyvo.model.Gift
import com.example.zyvo.model.GiftRarity
import com.example.zyvo.model.PredefinedGifts
import com.example.zyvo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftDialog(
    userCoinBalance: Int,
    onDismiss: () -> Unit,
    onSendGift: (Gift, Int) -> Unit
) {
    var selectedGift by remember { mutableStateOf<Gift?>(PredefinedGifts.ALL_GIFTS.firstOrNull()) }
    var selectedMultiplier by remember { mutableStateOf(1) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        scrimColor = OverlayDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header Row: Title + Coin Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎁", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Send Virtual Gift",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Coin Balance Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkCardElevated)
                        .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$userCoinBalance Coins",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gift Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(PredefinedGifts.ALL_GIFTS) { gift ->
                    val isSelected = selectedGift?.id == gift.id
                    val rarityColor = when (gift.rarity) {
                        GiftRarity.COMMON -> NeonCyan
                        GiftRarity.RARE -> NeonPurpleLight
                        GiftRarity.EPIC -> ElectricMagenta
                        GiftRarity.LEGENDARY -> GoldAccent
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) DarkCardElevated else DarkCard)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) rarityColor else OverlayLight,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedGift = gift }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = gift.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gift.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${gift.coinCost}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Send Action Row with Multipliers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Multiplier selector: 1x, 5x, 10x, 99x
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 5, 10, 99).forEach { count ->
                        val isMulSelected = selectedMultiplier == count
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isMulSelected) NeonPurple else DarkCardElevated)
                                .clickable { selectedMultiplier = count }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${count}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isMulSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }

                // Send Button
                Button(
                    onClick = {
                        val gift = selectedGift
                        if (gift != null) {
                            onSendGift(gift, selectedMultiplier)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.testTag("confirm_send_gift_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricMagenta,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Send (${(selectedGift?.coinCost ?: 0) * selectedMultiplier} 🪙)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
