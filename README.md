# Zyvo Live 🔮

**Zyvo Live** is a state-of-the-art, high-fidelity Android application built with modern **Jetpack Compose** and **Kotlin**. It delivers a premium, real-time creator sovereignty stage featuring interactive broadcasting screens, comparative PK battle arenas, multi-guest lounges, and high-fidelity audio stage simulations.

The entire experience is wrapped in a dark luxury celestial universe with a deep violet canvas, neon magenta highlights, and prestigious gold accents.

---

## 🌟 Core Architecture & Technical Specifications

- **UI Framework:** 100% Jetpack Compose with Material Design 3 (M3).
- **Architecture Pattern:** MVVM (Model-View-ViewModel) with structured unidirectional state flow using Kotlin Coroutines and `StateFlow`.
- **Image Loading:** Coil (`io.coil-kt:coil-compose`) for asynchronous, high-performance image caching and rendering.
- **Dependency Management:** Centralized **Gradle Version Catalog** (`gradle/libs.versions.toml`) to ensure absolute build consistency and strict version compatibility.
- **Build System:** Gradle (Kotlin DSL) targetting **SDK 34 (Android 14)** with Java 17 toolchains.

---

## ✨ Features & Visual Highlights

### 🎨 1. High-Fidelity Cover Animations
- **Auroral Gradients:** Fully custom-rendered `AnimatedCoverBanner` using linear brushing gradients that shift diagonally on a loop.
- **Overlapping Avatars:** Circular profile avatars with dynamic borders (`ElectricMagenta` and `GoldAccent`) overlapping the animated banners to deliver a modern, high-contrast, edge-to-edge look.

### ⚔️ 2. Dynamic Stream Rooms & Broadcast Modes
- **Single Live Broadcaster:** Features clean video simulation overlays, active chat overlays, and full interactive support.
- **Executive PK Battles:** Supports dual real-time visual streaming cages. Users can tap to support Team Alpha or Team Beta, adjusting comparative scores and updating progress indicators dynamically.
- **Multi-Guest Lounges:** Integrates multi-video grid panels featuring profile avatars, names, and customized interactive status frames.
- **VIP Audio Stages:** Incorporates spatial seat grids where users can request locked, open, or VIP sound stage seats with mic-status indicators.

### 🔮 3. Unified Premium Gems Currency
- **Gems Integration:** Configured with our signature **Gems** visual asset across the application, replacing all generic placeholders with a highly polished gemstone logo.
- **Dynamic Gifting Core:** Supports simulated coin conversion to Gems. Dropping premium gifts in active stream rooms triggers visual particle bursts and updates the broadcaster's overall Gem collection in real time.

### 📱 4. Executive Navigation & Analytics Screens
- **Explore Lounge:** Displays active live rooms categorized by stream types via sleek filter pills.
- **Sovereign Wallet:** Allows creators to browse rechargeable packages, exchange coins, and track transactions.
- **Studio Analytics:** Tracks broad creator metrics including weekly hours streamed, fans reached, and historic Gem revenue with sleek visual summaries.
- **Creator Profile:** Displays the user's custom biography, SVIP membership tier progress, and active prestige accomplishments.

---

## 🏗️ Project Structure

```bash
/
├── app/
│   ├── build.gradle.kts           # App-level dependencies & compilation rules
│   └── src/main/
│       ├── AndroidManifest.xml    # Entry points and permissions (Camera, Mic, Internet)
│       ├── java/com/example/zyvo/
│       │   ├── MainActivity.kt    # Main App navigation host & screens
│       │   └── ui/theme/
│       │       └── Theme.kt       # Centered luxury theme declarations & palette
│       └── res/
│           └── values/
│               └── strings.xml    # Unified app resource strings
├── gradle/
│   └── libs.versions.toml         # Centralized dependencies and versions catalog
├── build.gradle.kts               # Project-level plugins register
├── settings.gradle.kts            # Project settings & module includes
└── README.md                      # Comprehensive project documentation
```

---

## 🛠️ Compilation and Setup

To build and compile the application successfully, use the following standard Gradle tasks:

1. **Clean Project Build:**
   ```bash
   gradle clean
   ```

2. **Assemble Debug Application Package (APK):**
   ```bash
   gradle :app:assembleDebug
   ```

3. **Verify Implementation Integrity:**
   ```bash
   gradle compileDebugKotlin
   ```
