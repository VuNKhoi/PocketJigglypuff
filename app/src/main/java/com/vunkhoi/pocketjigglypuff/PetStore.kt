package com.vunkhoi.pocketjigglypuff

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.petDataStore by preferencesDataStore(name = "pet_state")
private val tapCountKey = intPreferencesKey("tap_count")

/** Only local pet data, kept across app restarts and screen transitions. */
class PetStore(context: Context) {
    private val appContext = context.applicationContext

    val tapCount: Flow<Int> = appContext.petDataStore.data.map { preferences ->
        preferences[tapCountKey] ?: 0
    }

    suspend fun recordTap() {
        appContext.petDataStore.edit { preferences ->
            preferences[tapCountKey] = (preferences[tapCountKey] ?: 0) + 1
        }
    }
}
