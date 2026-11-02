# PocketJigglypuff

A tiny, offline-first cover-screen pet for **Motorola Razr Ultra (2025)** and **Samsung Galaxy Z Flip6**.

## Milestone 1 — first playable prototype

- Original pink placeholder creature drawn with Jetpack Compose Canvas (no copyrighted art assets in this public repo).
- Gentle idle bob/blink and a tap-to-bounce response.
- Tap count persisted locally with Jetpack DataStore.
- One responsive interface for the cover screen and open phone.
- No logins, ads, network permissions, or background service.

## Getting started

**Prerequisites:** Android Studio with JDK 17 and Android SDK 35. This starter uses Android Gradle Plugin 8.9.2, Gradle 8.11.1, and Kotlin 2.1.20.

1. Clone this repository and open its root folder with Android Studio.
2. Allow the Gradle project to sync; if prompted, configure Gradle 8.11.1.
3. Connect the phone with USB debugging or use an Android emulator, then run the `app` configuration.
4. Alternatively, after the first GitHub Actions build, download the **PocketJigglypuff-debug-apk** workflow artifact and install the APK for testing.

Command line with Gradle 8.11.1 already installed: `gradle :app:assembleDebug`.

**Note:** The initial repo omits Gradle wrapper binaries. Android Studio can sync using a configured Gradle distribution, and GitHub Actions installs the pinned Gradle version. A standard wrapper can be generated later with `gradle wrapper --gradle-version 8.11.1` and committed.

## Testing on the external displays

### Motorola Razr Ultra (2025)

1. Install and launch the app on the inner screen.
2. Go to **Settings > External display > App settings**.
3. Allow **PocketJigglypuff** on the external display and choose **Auto transition** or **Manual transition**.
4. Close the phone, unlock the external display, and open/continue PocketJigglypuff.

### Samsung Galaxy Z Flip6

1. Install the app normally.
2. From the **Galaxy Store**, install **Good Lock**, then its **MultiStar** module.
3. Go to **MultiStar > I ♡ Galaxy Foldable > Launcher Widget** and select PocketJigglypuff.
4. Enable the MultiStar Launcher widget under **Settings > Cover screen > Widgets** (names can vary by One UI version).
5. Close the phone, open the Launcher widget, and select PocketJigglypuff.

The Samsung launch path depends on the OS and Good Lock version; **it requires real-device validation** and is not guaranteed to work on every update. This first milestone is a **cover-screen app**, not a persistent native cover widget.

## Manual smoke test checklist

- [ ] App launches on Razr Ultra inner screen
- [ ] App launches on Razr Ultra external display
- [ ] App launches on Z Flip6 inner screen
- [ ] App launches through Z Flip6 MultiStar external display
- [ ] Creature gently bobs and occasionally blinks
- [ ] Tapping the creature produces a visible bounce and increments the count
- [ ] Count survives app restart
- [ ] Layout isn't clipped by corners, cameras, clock, or system UI
- [ ] Closing/opening device doesn't crash, freeze, or reset state
- [ ] Screen sleeps normally when not in use

## Next

Once cover-screen launching is verified on both phones: add petting, feeding, sleep, mood/energy, original audio, and richer character poses. Dedicated Samsung/Motorola cover widgets will be a separate milestone.

## Artwork note

Jigglypuff is a Pokémon character owned by its respective rights holders. This public starter repo uses an **original placeholder pet** and ships no ripped Pokémon art or music. No affiliation or endorsement is claimed.
