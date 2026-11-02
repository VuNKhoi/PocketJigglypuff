package com.vunkhoi.pocketjigglypuff

import kotlin.math.max

enum class PetAction { PET, FEED, PLAY, SING, SLEEP, WAKE }
enum class PetScene { GARDEN, BEDROOM }
enum class PetAccessory { NONE, BOW, FLOWER, STAR }
enum class PetMood { ASLEEP, SLEEPY, HUNGRY, HAPPY, CONTENT }

/**
 * Local, low-pressure virtual pet. Needs never reach dangerous values and there
 * is no failure state. Offline changes are calculated when the user comes back.
 */
data class PetState(
    val pets: Int = 0,
    val feeds: Int = 0,
    val plays: Int = 0,
    val songs: Int = 0,
    val naps: Int = 0,
    val stars: Int = 0,
    val happiness: Int = 75,
    val fullness: Int = 75,
    val energy: Int = 85,
    val sleeping: Boolean = false,
    val scene: PetScene = PetScene.GARDEN,
    val accessory: PetAccessory = PetAccessory.NONE,
    val soundOn: Boolean = true,
    val hapticsOn: Boolean = true,
    val updatedAt: Long = 0L
) {
    val mood: PetMood
        get() = when {
            sleeping -> PetMood.ASLEEP
            energy <= 27 -> PetMood.SLEEPY
            fullness <= 27 -> PetMood.HUNGRY
            happiness >= 75 -> PetMood.HAPPY
            else -> PetMood.CONTENT
        }

    val interactions: Int get() = pets + feeds + plays + songs + naps

    val achievements: List<String>
        get() = buildList {
            if (pets >= 1) add("First hello")
            if (pets >= 20) add("Best buddies")
            if (feeds >= 5) add("Snack time")
            if (plays >= 5) add("Playful puff")
            if (songs >= 5) add("Little singer")
            if (naps >= 3) add("Sweet dreams")
            if (interactions >= 50) add("Pocket companion")
        }

    fun advance(now: Long): PetState {
        if (updatedAt <= 0L || now <= updatedAt) return copy(updatedAt = now)
        val minutes = ((now - updatedAt) / 60_000L).coerceAtMost(7L * 24 * 60)
        if (minutes < 1) return this
        return copy(
            fullness = (fullness - minutes / 60).toInt().coerceIn(25, 100),
            happiness = (happiness - minutes / 90).toInt().coerceIn(40, 100),
            energy = if (sleeping) {
                (energy + minutes / 5).toInt().coerceIn(0, 100)
            } else {
                (energy - minutes / 45).toInt().coerceIn(20, 100)
            },
            updatedAt = now
        )
    }

    fun perform(action: PetAction, now: Long): PetState {
        val current = advance(now)
        val changed = when (action) {
            PetAction.PET -> current.copy(
                pets = current.pets + 1,
                happiness = (current.happiness + 4).coerceAtMost(100)
            )
            PetAction.FEED -> current.copy(
                feeds = current.feeds + 1,
                fullness = (current.fullness + 28).coerceAtMost(100),
                happiness = (current.happiness + 5).coerceAtMost(100),
                stars = current.stars + 1
            )
            PetAction.PLAY -> current.copy(
                plays = current.plays + 1,
                energy = max(15, current.energy - 12),
                fullness = max(20, current.fullness - 5),
                happiness = (current.happiness + 14).coerceAtMost(100),
                stars = current.stars + 2
            )
            PetAction.SING -> current.copy(
                songs = current.songs + 1,
                energy = max(15, current.energy - 6),
                happiness = (current.happiness + 10).coerceAtMost(100),
                stars = current.stars + 1
            )
            PetAction.SLEEP -> current.copy(
                naps = current.naps + 1,
                sleeping = true
            )
            PetAction.WAKE -> current.copy(sleeping = false)
        }
        return changed.copy(updatedAt = now)
    }
}
