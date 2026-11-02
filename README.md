# PocketJigglypuff

A tiny, offline-first Android virtual pet built for the **Motorola Razr Ultra (2025)** and **Samsung Galaxy Z Flip6** outer displays. One Kotlin/Jetpack Compose app, entirely on-device.

## v0.4 living habitats

- Original vector-drawn pink placeholder pet: gentle idle bob, blink, tilt, bounce, sleeping face and little reaction effects
- Pet by tapping; feed berries; play with a ball; sing; sleep and wake
- Happiness, fullness and energy needs with simple mood states
- Gentle offline progress, energy recovery when sleeping, **no death or penalty**
- Earn stars and seven small collectible achievement badges
- **Living habitats:** illustrated cozy room (window, cushion, house plant and wall art) and garden clearing (hills, tree, flowers and mushroom)
- **Autonomous behavior:** pet wanders around its habitat, pauses, displays occasional little thoughts, and moves toward a nap spot when asleep. The idle coroutine runs only while the activity is visible; no background wandering service
- Day/night versions of both scenes
- Four dress-up options: none, bow, flower and star, saved locally
- Sound and haptic feedback (both optional and saved)
- Local DataStore persistence for settings and all progress; v0.1 tap count preserved
- Small-screen adaptive layout with safe insets, flexible-width action buttons and meters, centered inner-screen content; no ads, trackers, accounts, network permissions, alarms or background service
- Optional static Android home-screen widget with **Pet** and **Open** actions, mood and stats
- Samsung Flex Window widget registration metadata (experimental, not physically tested)

**Note:** The main game is an ordinary Android activity. A separate static Android widget is included, with Samsung Flex Window registration metadata based on Samsung's developer documentation. Whether the Z Flip6 actually lists or hosts it is **unverified**; this is not an always-on animated companion. The game can also be launched through MultiStar.

## Install

Open [Android Debug APK GitHub Actions](../../actions/workflows/android.yml) and select the latest **successful** run on the feature branch. Under **Artifacts**, download `PocketJigglypuff-debug-apk`, extract the ZIP and install `app-debug.apk`.

For source builds: open repo in Android Studio with JDK 17 and Android SDK 35. CI uses Gradle 8.11.1, Android Gradle Plugin 8.9.2 and Kotlin 2.1.20. This repo does not currently commit a Gradle wrapper; Android Studio can use a configured distribution, or run `gradle :app:assembleDebug` if Gradle 8.11.1 is installed.

## Outer-screen launch

**Motorola Razr Ultra (2025):** Enable PocketJigglypuff in **Settings → External display → App settings**, then launch it on the external display.

**Samsung Galaxy Z Flip6:** Use **Good Lock → MultiStar → I ♡ Galaxy Foldable → Launcher Widget** to add PocketJigglypuff and place the launcher widget on the cover screen. Labels and availability can change with One UI/Good Lock versions.

## Deferred real-phone checklist

- [ ] Install and launch from Razr Ultra outer display
- [ ] Install and launch from Z Flip6 outer display with MultiStar
- [ ] Check whether native Pocket pet widget appears in Z Flip6 Settings → Cover screen → Widgets
- [ ] Add widget on normal home screen, test Pet button, Open button and mood refresh
- [ ] Inspect cutouts, curved corners, text sizes and four action buttons
- [ ] Pet, feed, play, sing, sleep and wake
- [ ] Leave sleeping across several minutes, check restored energy
- [ ] Close and reopen app; check stats, counters, mood and preferences persist
- [ ] Open and close the foldable while running; look for crashes and clipped UI
- [ ] Switch garden/bedroom, sound/haptics, and open badges
- [ ] Leave app open 20-30 seconds: pet should wander, pause and occasionally display a small emote
- [ ] Put pet to sleep: it should wander toward the room cushion and stop moving
- [ ] Check pet never leaves the visible stage on narrow cover displays
- [ ] Reopen after inactivity: idle behavior resumes without battery-draining background animation
- [ ] Confirm that sound/vibration and screen sleep behave normally

## Roadmap

After real phone feedback, fix layout, movement and launch problems. Later: path-aware prop interaction, additional original animated character poses, snack options, short minigames, custom decorations, and separate feasibility work on native cover-screen widgets.

## Rights

This public code ships original placeholder drawing and device-generated tones, not copied Pokémon sprite sheets or music. Pokémon/Jigglypuff belong to their respective rights holders and this is an unaffiliated hobby project.
