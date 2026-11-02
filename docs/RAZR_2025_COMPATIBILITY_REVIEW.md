# Razr Ultra (2025) compatibility investigation
Reviewed 2026-11-02. Status: **BLOCKED — do not run PocketJigglypuff on the affected Razr.**

## Incident
PocketJigglypuff launched successfully on a Samsung Galaxy S24. Repeated launch attempts on Motorola Razr Ultra (2025) reportedly caused the phone to shut down/restart and, on a retry, show an "unable to recover" message requiring multiple attempts to start. Exact OS build, screen mode, reboot/error text, and system logs are not yet available. **A passing GitHub APK build cannot verify phone safety.**

## Independent evidence
- Motorola support identifies unexpected reboots as potential app or firmware/device problems and recommends uninstalling recent suspect apps, checking updates, and safe mode. https://help.motorola.com/hc/5520/15/na/en-gb/CGjcb1208210742.html
- Independent January–February 2026 owner thread reports Razr Ultra 2025 freezes and reboots during normal app use, with no established culprit. Anecdotal, not proof for this incident: https://www.howardforums.com/threads/motorola-razr-ultra-2025-aka-razr-60-ultra-constantly-reboots-itself.1931817/
- Reports of Motorola secondary-display launcher failures: https://www.reddit.com/r/razr/comments/1v1oxwg/secondary_display_app_crashing/
- Razr Android 16 external-screen layout/fullscreen regressions: https://www.reddit.com/r/razr/comments/1txjeu2/razr_2025_since_upgrading_to_android_16_some_apps/
- Android configuration changes when folding or switching displays: https://developer.android.com/guide/topics/resources/runtime-changes
- A previously fixed Compose BoxWithConstraints animation/resizing issue (different symptoms from a device reboot): https://issuetracker.google.com/issues/343178423
- Android Compose animation performance, including deferring animated offsets to layout: https://developer.android.com/develop/ui/compose/animation/quick-guide
- Stack Overflow discussion of Compose Animatable + lambda offset: https://stackoverflow.com/questions/79590345/how-to-make-tween-animation-duration-dynamic-in-compose
- Android Rescue Party on repeated critical-process crashes: https://source.android.com/docs/core/tests/debug/rescue-party . This is a possible **class** of recovery behavior, NOT an established explanation.
- Non-reproduction bug-report collection: https://developer.android.com/studio/debug/bug-report

## Our code audit
| Component | Finding | Action |
| --- | --- | --- |
| Kotlin 2.1.20 / Compose BOM 2025.04.01 / Material3 | No specific documented device reboot signature identified | Keep under investigation, not exonerated |
| App launch / legacy immersive flags | Early versions hide status and navigation bars, potentially colliding with cover display | Removed in v0.4.1-safe |
| Compose Canvas room / garden | Lightweight vector drawing with no bitmap allocation, game engine or native GPU code, but does touch graphics stack | Disabled by default in diagnostic app; no Razr test |
| Wander Animatable / Modifier.offset | Prior animated Dp offset read in composition and could trigger avoidable per-frame recomposition | Changed to deferred layout-phase lambda offset |
| Foldable display resize | Transient zero-size drawing surface should not be assumed usable | Added Canvas and PetArt size guards |
| Continuous animations while backgrounded | Wander loop already cancels on lifecycle STOP; PetArt's infinite transitions could remain composed | PetArt now disposed while lifecycle is not STARTED |
| Preferences DataStore 1.1.7 | Local persistence, no long-running service; unlikely to shut down device | No change |
| ToneGenerator | Only invoked after explicit actions, not at startup | Not likely relevant to immediate-launch symptom |
| AppWidgetProvider / Samsung cover widget metadata | Android host/RemoteViews integration, independent of main activity | Needs device validation; not identified as cause |
| AndroidManifest | No internet, hardware-control or reboot permission | No such action exists in app |

## Safe resolution gate (not test instructions)
1. Uninstall triggering APK from Razr without opening it; back up phone data and use the Razr normally.
2. Determine whether unexpected restarts continue **without** the app.
3. If phone is stable, collect an existing Android bug report or diagnostic report **without relaunching the app**. Review for system_server, SystemUI, SurfaceFlinger, graphics driver, watchdog, recovery and app-process clues. Reports may contain private information; remove sensitive data before sharing.
4. Investigate on S24 and an Android foldable emulator or a disposable test device, not this daily-driver Razr.
5. Do not greenlight Razr builds until cause is better understood. Treat v0.4.2 as an unverified development diagnostic build, not a proven fix.

**No factory reset or forced crash reproduction is recommended.** Motorola service is appropriate if the phone continues failing without the app.
