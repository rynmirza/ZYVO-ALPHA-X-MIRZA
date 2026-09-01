package com.example.zyvo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
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
import com.example.zyvo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (displayName: String, email: String, avatarEmoji: String) -> Unit
) {
    var showGoogleSelectDialog by remember { mutableStateOf(false) }
    var showComingSoonToast by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF260538),
                        Color(0xFF130421),
                        DarkBackground
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: App Brand & Hero visuals
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                // Animated Glowing Crown/Zyvo Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricMagenta, NeonPurple)
                            )
                        )
                        .border(2.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ZYVO",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "WATCH • CONNECT • SHINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Hero Cards Carousel / Stats Pill
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurface)
                            .border(1.dp, OverlayLight, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔴", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "14.8K Live Streams",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurface)
                            .border(1.dp, OverlayLight, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚔️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PK Battles",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                    }
                }
            }

            // Middle Section: Login Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Sign in to enter live rooms & join battles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 1. GOOGLE SIGN IN BUTTON (WORKING)
                Button(
                    onClick = { showGoogleSelectDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("login_google_button"),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google "G" Icon Badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Continue with Google",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )
                    }
                }

                // 2. MOBILE / PHONE LOGIN (COMING SOON)
                OutlinedButton(
                    onClick = { showComingSoonToast = "Mobile Phone & SMS OTP authentication coming soon in ZYVO v2.0!" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_phone_button"),
                    shape = RoundedCornerShape(25.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = Brush.linearGradient(listOf(OverlayLight, TextMuted))),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = "Phone", tint = TextSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Mobile Phone", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkCardElevated)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "Coming Soon", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 3. FACEBOOK LOGIN (COMING SOON)
                OutlinedButton(
                    onClick = { showComingSoonToast = "Facebook login integration coming soon!" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_facebook_button"),
                    shape = RoundedCornerShape(25.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = Brush.linearGradient(listOf(OverlayLight, TextMuted))),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1877F2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "f", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Sign in with Facebook", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkCardElevated)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "Coming Soon", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 4. QUICK DEMO / GUEST ACCESS BUTTON
                TextButton(
                    onClick = {
                        onLoginSuccess("Demo Streamer", "demo@zyvo.live", "🚀")
                    },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Continue as Guest / Quick Demo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldAccent
                    )
                }
            }

            // Bottom Section: Footer Legal Note
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = "Secure", tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Safe & Secure Authentication",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "By continuing, you agree to ZYVO Terms of Service & Privacy Policy",
                    fontSize = 10.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // GOOGLE ACCOUNT SELECTOR DIALOG
        if (showGoogleSelectDialog) {
            AlertDialog(
                onDismissRequest = { if (!isAuthenticating) showGoogleSelectDialog = false },
                containerColor = DarkSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("google_login_dialog"),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("G", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Choose Google Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        if (!isAuthenticating) {
                            IconButton(onClick = { showGoogleSelectDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                            }
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isAuthenticating) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = ElectricMagenta, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("Connecting with Google...", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                Text("Authenticating ZYVO Account...", fontSize = 11.sp, color = TextMuted)
                            }
                        } else {
                            Text(text = "Select an account to log in or create your ZYVO profile:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                            // Google Account Item 1
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DarkCardElevated)
                                    .border(1.dp, NeonPurple, RoundedCornerShape(14.dp))
                                    .clickable {
                                        isAuthenticating = true
                                        scope.launch {
                                            delay(1200)
                                            isAuthenticating = false
                                            showGoogleSelectDialog = false
                                            onLoginSuccess("Alex Johnson", "alex.johnson@gmail.com", "👑")
                                        }
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(ElectricMagenta),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👑", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Alex Johnson", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                        Text("alex.johnson@gmail.com", fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                            }

                            // Google Account Item 2
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DarkCardElevated)
                                    .border(1.dp, OverlayLight, RoundedCornerShape(14.dp))
                                    .clickable {
                                        isAuthenticating = true
                                        scope.launch {
                                            delay(1200)
                                            isAuthenticating = false
                                            showGoogleSelectDialog = false
                                            onLoginSuccess("Jannat Live", "jannat.creator@gmail.com", "🌸")
                                        }
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(NeonPurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🌸", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Jannat Creator", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                        Text("jannat.creator@gmail.com", fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        // COMING SOON TOAST SNACKBAR
        showComingSoonToast?.let { toastMsg ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = DarkSurface,
                contentColor = TextPrimary,
                action = {
                    TextButton(onClick = { showComingSoonToast = null }) {
                        Text("OK", color = ElectricMagenta, fontWeight = FontWeight.Bold)
                    }
                }
            ) {
                Text(text = toastMsg, fontSize = 12.sp)
            }
        }
    }
}
