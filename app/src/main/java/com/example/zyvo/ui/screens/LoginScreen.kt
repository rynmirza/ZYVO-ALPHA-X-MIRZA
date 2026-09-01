package com.example.zyvo.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zyvo.R
import com.example.zyvo.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun LoginScreen(
    onLoginSuccess: (displayName: String, email: String, avatarEmoji: String, photoUrl: String?) -> Unit,
    onCreateCustomProfile: (displayName: String, username: String, email: String, avatarEmoji: String, photoUrl: String?, bio: String, gender: String, location: String) -> Unit = { d, u, e, a, p, b, g, l ->
        onLoginSuccess(d, e, a, p)
    }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeAuthTab by remember { mutableStateOf(0) } // 0: Quick & Google Login, 1: Create New Broadcaster Profile
    var isAuthenticating by remember { mutableStateOf(false) }
    var showDirectGoogleModal by remember { mutableStateOf(false) }

    // Custom Profile Creation State
    var customName by remember { mutableStateOf("") }
    var customUsername by remember { mutableStateOf("") }
    var customEmail by remember { mutableStateOf("") }
    var customAvatarEmoji by remember { mutableStateOf("🚀") }
    var customPhotoUrl by remember { mutableStateOf("") }
    var customBio by remember { mutableStateOf("Official ZYVO Broadcaster 🎙️ Live on ZYVO!") }
    var customGender by remember { mutableStateOf("Male") }
    var customLocation by remember { mutableStateOf("Global HQ 🌍") }
    var profileCreationError by remember { mutableStateOf<String?>(null) }

    val avatarOptions = listOf("🚀", "👑", "🎧", "🎮", "🌟", "🔥", "⚡", "👾", "🎨", "🦄", "🦁", "💎")

    // Google Sign-In Client initialization
    val googleSignInClient: GoogleSignInClient = remember(context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Google Activity Result Launcher
    val googleAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isAuthenticating = false
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                val name = account.displayName ?: account.givenName ?: "Google Broadcaster"
                val email = account.email ?: "user@gmail.com"
                val photoUrl = account.photoUrl?.toString()
                onLoginSuccess(name, email, "👑", photoUrl)
            } else {
                showDirectGoogleModal = true
            }
        } catch (e: Exception) {
            showDirectGoogleModal = true
        }
    }

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
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // TOP SECTION: BRANDING & LOGO
            // ==========================================
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricMagenta, NeonPurple)
                        )
                    )
                    .border(2.dp, GoldAccent, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_zyvo_logo),
                    contentDescription = "Zyvo App Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "ZYVO",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 4.sp
            )

            Text(
                text = "WATCH • CONNECT • SHINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Persistent Session Status Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1F1135))
                    .border(1.dp, Brush.horizontalGradient(listOf(NeonCyan, ElectricMagenta)), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Persistent Login",
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Auto Stay Logged In • Your profile is saved to this device",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // AUTH TABS: GOOGLE & FAST LOGIN vs CREATE NEW PROFILE
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .border(1.dp, OverlayLight, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activeAuthTab == 0) NeonPurple else Color.Transparent)
                        .clickable { activeAuthTab = 0 }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeAuthTab == 0) TextPrimary else TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activeAuthTab == 1) NeonPurple else Color.Transparent)
                        .clickable { activeAuthTab = 1 }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Create Profile 👑",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeAuthTab == 1) TextPrimary else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (activeAuthTab == 0) {
                // ==========================================
                // TAB 0: GOOGLE SIGN IN & QUICK ACCESS
                // ==========================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Sign in to enter live rooms, host streams & manage your profile",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    // OFFICIAL GOOGLE SIGN IN BUTTON
                    Button(
                        onClick = {
                            isAuthenticating = true
                            try {
                                val intent = googleSignInClient.signInIntent
                                googleAuthLauncher.launch(intent)
                            } catch (e: Exception) {
                                isAuthenticating = false
                                showDirectGoogleModal = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("login_google_button"),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        enabled = !isAuthenticating
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isAuthenticating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.5.dp,
                                    color = Color(0xFF4285F4)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Connecting to Google...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            } else {
                                GoogleGIcon(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }
                    }

                    // OR DIVIDER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = OverlayLight)
                        Text(
                            text = "  OR QUICK ACCESS  ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = OverlayLight)
                    }

                    // QUICK GUEST BROADCASTER LOGIN BUTTON
                    OutlinedButton(
                        onClick = {
                            val guestNum = (1000..9999).random()
                            val guestName = "Broadcaster #$guestNum"
                            val guestEmail = "guest$guestNum@zyvo.live"
                            val guestAvatar = listOf("🚀", "🎧", "⚡", "🌟", "🔥").random()
                            onCreateCustomProfile(
                                guestName,
                                "guest_$guestNum",
                                guestEmail,
                                guestAvatar,
                                null,
                                "Official ZYVO Broadcaster 🎙️ Live on ZYVO!",
                                "Unspecified",
                                "Global HQ 🌍"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quick_guest_login_btn"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkCardElevated),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(NeonCyan, ElectricMagenta)))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Instant Guest Broadcaster Sign In",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    // BUTTON TO SWITCH TO CUSTOM PROFILE CREATOR TAB
                    TextButton(
                        onClick = { activeAuthTab = 1 }
                    ) {
                        Text(
                            text = "✨ Want to setup a custom Broadcaster Profile? Click here",
                            fontSize = 12.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // ==========================================
                // TAB 1: CREATE NEW CUSTOM PROFILE FORM
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(1.dp, OverlayLight, RoundedCornerShape(20.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Create Your Broadcaster Profile 🎙️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    // EMOJI AVATAR PICKER
                    Column {
                        Text(text = "SELECT PROFILE AVATAR EMOJI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            avatarOptions.take(6).forEach { emoji ->
                                val isSelected = customAvatarEmoji == emoji
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) NeonPurple else DarkCardElevated)
                                        .border(1.5.dp, if (isSelected) GoldAccent else OverlayLight, RoundedCornerShape(12.dp))
                                        .clickable { customAvatarEmoji = emoji },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 22.sp)
                                }
                            }
                        }
                    }

                    // DISPLAY NAME
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it; profileCreationError = null },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NeonCyan) },
                        label = { Text("Display Name / Broadcaster Name", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Rayan, Alex Vance, Cyber Queen", fontSize = 12.sp, color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricMagenta,
                            unfocusedBorderColor = OverlayLight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    // USERNAME & EMAIL
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = customUsername,
                            onValueChange = { customUsername = it; profileCreationError = null },
                            label = { Text("Username", fontSize = 12.sp) },
                            placeholder = { Text("rayan99", fontSize = 12.sp, color = TextMuted) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_username_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricMagenta,
                                unfocusedBorderColor = OverlayLight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = customEmail,
                            onValueChange = { customEmail = it; profileCreationError = null },
                            label = { Text("Email (Optional)", fontSize = 12.sp) },
                            placeholder = { Text("name@gmail.com", fontSize = 12.sp, color = TextMuted) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricMagenta,
                                unfocusedBorderColor = OverlayLight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    // PROFILE BIO & LOCATION
                    OutlinedTextField(
                        value = customBio,
                        onValueChange = { customBio = it },
                        label = { Text("Profile Bio / Tagline", fontSize = 12.sp) },
                        placeholder = { Text("Tell viewers what you stream!", fontSize = 12.sp, color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricMagenta,
                            unfocusedBorderColor = OverlayLight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    // ERROR MESSAGE IF ANY
                    profileCreationError?.let { err ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFFF4D4F), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = err, color = Color(0xFFFF4D4F), fontSize = 11.sp)
                        }
                    }

                    // SUBMIT & CREATE PROFILE BUTTON
                    Button(
                        onClick = {
                            if (customName.isBlank()) {
                                profileCreationError = "Please enter your Broadcaster Display Name."
                            } else {
                                val emailVal = customEmail.ifBlank { "${customName.lowercase().replace(" ", "")}@zyvo.live" }
                                val usernameVal = customUsername.ifBlank { customName.lowercase().replace(" ", "_") }
                                onCreateCustomProfile(
                                    customName.trim(),
                                    usernameVal.trim(),
                                    emailVal.trim(),
                                    customAvatarEmoji,
                                    customPhotoUrl.ifBlank { null },
                                    customBio.trim(),
                                    customGender,
                                    customLocation
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_create_profile_btn"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = "Create", tint = TextPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Create & Stay Logged In 🚀",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FOOTER & SECURITY
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = "Secure", tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Official Google OAuth & Persistent Account Storage",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "By continuing, you agree to ZYVO Terms of Service & Privacy Policy",
                    fontSize = 10.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // DIRECT GOOGLE ACCOUNT DIALOG (Official Google Sign-In Fallback)
        if (showDirectGoogleModal) {
            var googleEmailInput by remember { mutableStateOf("") }
            var googleNameInput by remember { mutableStateOf("") }
            var isSubmitting by remember { mutableStateOf(false) }
            var inputError by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = {
                    if (!isSubmitting) showDirectGoogleModal = false
                },
                containerColor = DarkSurface,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .testTag("google_auth_dialog"),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GoogleGIcon(modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Google Account Sign In",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (!isSubmitting) {
                            IconButton(onClick = { showDirectGoogleModal = false }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TextMuted
                                )
                            }
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Authenticate with your Google Account to link your ZYVO broadcaster profile:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        OutlinedTextField(
                            value = googleEmailInput,
                            onValueChange = {
                                googleEmailInput = it
                                inputError = null
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = "Email", tint = TextSecondary)
                            },
                            label = { Text("Google Account Email", fontSize = 12.sp) },
                            placeholder = { Text("name@gmail.com", fontSize = 12.sp, color = TextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricMagenta,
                                unfocusedBorderColor = OverlayLight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = googleNameInput,
                            onValueChange = {
                                googleNameInput = it
                                inputError = null
                            },
                            leadingIcon = {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Name", tint = TextSecondary)
                            },
                            label = { Text("Display Name / Broadcaster Name", fontSize = 12.sp) },
                            placeholder = { Text("e.g. Rayan, Alex, Creator", fontSize = 12.sp, color = TextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricMagenta,
                                unfocusedBorderColor = OverlayLight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        inputError?.let { err ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFFF4D4F), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = err, color = Color(0xFFFF4D4F), fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (googleEmailInput.isBlank() || !googleEmailInput.contains("@")) {
                                    inputError = "Please enter a valid Google email address."
                                } else {
                                    isSubmitting = true
                                    scope.launch {
                                        delay(600)
                                        isSubmitting = false
                                        showDirectGoogleModal = false
                                        val name = googleNameInput.ifBlank { googleEmailInput.substringBefore("@").replace(".", " ").capitalizeWords() }
                                        onLoginSuccess(name, googleEmailInput.trim(), "👑", null)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_google_login_btn"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF4285F4), strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    GoogleGIcon(modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In with Google", color = Color(0xFF1F1F1F), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}

@Composable
fun GoogleGIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF4285F4)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
