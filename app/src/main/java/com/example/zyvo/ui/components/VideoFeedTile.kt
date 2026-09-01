package com.example.zyvo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zyvo.model.BeautifyFilter
import com.example.zyvo.ui.theme.*

@Composable
fun VideoFeedTile(
    modifier: Modifier = Modifier,
    hostName: String,
    avatarEmoji: String,
    isSpeaking: Boolean = true,
    isMuted: Boolean = false,
    isVideoOff: Boolean = false,
    filter: BeautifyFilter = BeautifyFilter.ORIGINAL,
    badgeText: String? = null,
    badgeColor: Color = NeonPurple
) {
    val infiniteTransition = rememberInfiniteTransition(label = "video_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient"
    )

    val filterOverlayColor = when (filter) {
        BeautifyFilter.ORIGINAL -> Color.Transparent
        BeautifyFilter.WARM_GLOW -> Color(0x33FF9F1C)
        BeautifyFilter.CYBERPUNK -> Color(0x339D4EDD)
        BeautifyFilter.VINTAGE_FILM -> Color(0x2E8B5E3C)
        BeautifyFilter.STUDIO_PRO -> Color(0x2200F5D4)
        BeautifyFilter.BLACK_WHITE -> Color(0x66000000)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
    ) {
        if (!isVideoOff) {
            // Simulated dynamic WebRTC Video Stream Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val center = Offset(width / 2f, height / 2f)

                // Background animated gradient
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF261545),
                            Color(0xFF130924),
                            Color(0xFF090412)
                        ),
                        center = center,
                        radius = (width.coerceAtLeast(height)) * 0.85f
                    )
                )

                // Atmospheric ambient lights
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(width * 0.3f, height * 0.3f),
                        radius = width * 0.5f * pulseScale
                    ),
                    radius = width * 0.5f * pulseScale,
                    center = Offset(width * 0.3f, height * 0.3f)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ElectricMagenta.copy(alpha = 0.2f), Color.Transparent),
                        center = Offset(width * 0.7f, height * 0.7f),
                        radius = width * 0.45f
                    ),
                    radius = width * 0.45f,
                    center = Offset(width * 0.7f, height * 0.7f)
                )

                // Speaking ripple wave rings
                if (isSpeaking) {
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.35f * (1.1f - pulseScale)),
                        radius = (width * 0.32f) * pulseScale,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = NeonPurple.copy(alpha = 0.2f),
                        radius = (width * 0.42f) * pulseScale,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // Central Video Subject Avatar / Silhouette
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(DarkCardElevated, DarkCard)
                        )
                    )
                    .border(
                        width = if (isSpeaking) 2.5.dp else 1.dp,
                        color = if (isSpeaking) NeonCyan else OverlayLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarEmoji,
                    fontSize = 42.sp
                )
            }
        } else {
            // Video Muted / Off State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DarkCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatarEmoji, fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VideocamOff,
                            contentDescription = "Camera Off",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Camera is off",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Beautify filter overlay
        if (filterOverlayColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(filterOverlayColor)
            )
        }

        // Top-left badge (e.g. HOST, GUEST 1, RIVAL)
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.85f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeText,
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom label & status bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, OverlayDark)
                    )
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = hostName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1
                )
                if (isSpeaking) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    )
                }
            }

            if (isMuted) {
                Icon(
                    imageVector = Icons.Default.MicOff,
                    contentDescription = "Muted",
                    tint = PkRed,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
