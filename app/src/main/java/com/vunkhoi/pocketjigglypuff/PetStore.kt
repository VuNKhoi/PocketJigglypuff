package com.vunkhoi.pocketjigglypuff

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.petDataStore by preferencesDataStore(name = "pet_state")

private object Keys {
    val pets = intPreferencesKey("tap_count") // preserves the first prototype's counter
    val feeds = intPreferencesKey("feeds")
    val plays = intPreferencesKey("plays")
    val songs = intPreferencesKey("songs")
    val naps = intPreferencesKey("naps")
    val stars = intPreferencesKey("stars")
    val happiness = intPreferencesKey("happiness")
    val fullness = intPreferencesKey("fullness")
    val energy = intPreferencesKey("energy")
    val sleeping = booleanPreferencesKey("sleeping")
    val scene = stringPreferencesKey("scene")
    val soundOn = booleanPreferencesKey("sound")
    val hapticsOn = booleanPreferencesKey("haptics")
    val updatedAt = longPreferencesKey("updated_at")
}

private fun Preferences.toPetState(): PetState = PetState(
    pets = this[Keys.pets] ?: 0,
    feeds = this[Keys.feeds] ?: 0,
    plays = this[Keys.plays] ?: 0,
    songs = this[Keys.songs] ?: 0,
    naps = this[Keys.naps] ?: 0,
    stars = this[Keys.stars] ?: 0,
    happiness = (this[Keys.happiness] ?: 75).coerceIn(0, 100),
    fullness = (this[Keys.fullness] ?: 75).coerceIn(0, 100),
    energy = (this[Keys.energy] ?: 85).coerceIn(0, 100),
    sleeping = this[Keys.sleeping] ?: false,
    scene = PetScene.entries.firstOrNull { it.name == this[Keys.scene] } ?: PetScene.GARDEN,
    soundOn = this[Keys.soundOn] ?: true,
    hapticsOn = this[Keys.hapticsOn] ?: true,
    updatedAt = this[Keys.updatedAt] ?: 0L
)

private fun androidx.datastore.preferences.core.MutablePreferences.write(state: PetState) {
    this[Keys.pets] = state.pets
    this[Keys.feeds] = state.feeds
    this[Keys.plays] = state.plays
    this[Keys.songs] = state.songs
    this[Keys.naps] = state.naps
    this[Keys.stars] = state.stars
    this[Keys.happiness] = state.happiness
    this[Keys.fullness] = state.fullness
    this[Keys.energy] = state.energy
    this[Keys.sleeping] = state.sleeping
    this[Keys.scene] = state.scene.name
    this[Keys.soundOn] = state.soundOn
    this[Keys.hapticsOn] = state.hapticsOn
    this[Keys.updatedAt] = state.updatedAt
}

/** Single, atomic local source of truth. No network, jobs, or background timers. */
class PetStore(context: Context) {
    private val app = context.applicationContext

    val state: Flow<PetState> = app.petDataStore.data.map { preferences ->
        preferences.toPetState().advance(System.currentTimeMillis())
    }

    suspend fun act(action: PetAction) = update { it.perform(action, System.currentTimeMillis()) }

    suspend fun changeScene() = update {
        it.copy(scene = if (it.scene == PetScene.GARDEN) PetScene.BEDROOM else PetScene.GARDEN)
    }

    suspend fun setSound(enabled: Boolean) = update { it.copy(soundOn = enabled) }
    suspend fun setHaptics(enabled: Boolean) = update { it.copy(hapticsOn = enabled) }

    private suspend fun update(change: (PetState) -> PetState) {
        app.petDataStore.edit { preferences ->
            val now = System.currentTimeMillis()
            val next = change(preferences.toPetState().advance(now)).copy(updatedAt = now)
            preferences.write(next)
        }
        PetWidgetProvider.requestUpdate(app)
    }
}
