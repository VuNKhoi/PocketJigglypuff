# PocketJigglypuff

A tiny, offline-first Android virtual pet built for the **Motorola Razr Ultra (2025)** and **Samsung Galaxy Z Flip6** outer displays. One Kotlin/Jetpack Compose app, entirely on-device.

## v0.2 interactive pet

- Original vector-drawn pink placeholder pet: gentle idle bob, blink, tilt, bounce, sleeping face and little reaction effects
- Pet by tapping; feed berries; play with a ball; sing; sleep and wake
- Happiness, fullness and energy needs with simple mood states
- Gentle offline progress, energy recovery when sleeping, **no death or penalty**
- Earn stars and seven small collectible achievement badges
- Garden and cozy-room scenes, automatically using night colors after dark
- Sound and haptic feedback (both optional and saved)
- Local DataStore persistence for settings and all progress; v0.1 tap count preserved
- Small-screen adaptive layout with safe insets, no ads, trackers, accounts, network permissions, alarms or background service

**Note:** This is an ordinary Android activity that can launch on an external display, **not** a native Samsung cover widget or always-on companion. Support for cover-screen launching requires checks on each target phone.

## Install

Open [Android Debug APK GitHub Actions](../../actions/workflows/android.yml) and select the latest **successful** run on the feature branch. Under **Artifacts**, download `PocketJigglypuff-debug-apk`, extract the ZIP and install `app-debug.apk`.

For source builds: open repo in Android Studio with JDK 17 and Android SDK 35. CI uses Gradle 8.11.1, Android Gradle Plugin 8.9.2 and Kotlin 2.1.20. This repo does not currently commit a Gradle wrapper; Android Studio can use a configured distribution, or run `gradle :app:assembleDebug` if Gradle 8.11.1 is installed.

## Outer-screen launch

**Motorola Razr Ultra (2025):** Enable PocketJigglypuff in **Settings → External display → App settings**, then launch it on the external display.

**Samsung Galaxy Z Flip6:** Use **Good Lock → MultiStar → I ♡ Galaxy Foldable → Launcher Widget** to add PocketJigglypuff and place the launcher widget on the cover screen. Labels and availability can change with One UI/Good Lock versions.

## Deferred real-phone checklist

- [ ] Install and launch from Razr Ultra outer display
- [ ] Install and launch from Z Flip6 outer display with MultiStar
- [ ] Inspect cutouts, curved corners, text sizes and four action buttons
- [ ] Pet, feed, play, sing, sleep and wake
- [ ] Leave sleeping across several minutes, check restored energy
- [ ] Close and reopen app; check stats, counters, mood and preferences persist
- [ ] Open and close the foldable while running; look for crashes and clipped UI
- [ ] Switch garden/bedroom, sound/haptics, and open badges
- [ ] Confirm that sound/vibration and screen sleep behave normally

## Roadmap

After real phone feedback, fix layout and launch problems. Later: additional original animated character poses, snack options, short minigames, custom decorations, and separate feasibility work on native cover-screen widgets.

## Rights

This public code ships original placeholder drawing and device-generated tones, not copied Pokémon sprite sheets or music. Pokémon/Jigglypuff belong to their respective rights holders and this is an unaffiliated hobby project.
