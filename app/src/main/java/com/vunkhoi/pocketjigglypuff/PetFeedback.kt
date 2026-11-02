package com.vunkhoi.pocketjigglypuff

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/** Short generated notification-style chirps; no music files or audio permissions. */
object PetFeedback {
    suspend fun play(action: PetAction) = withContext(Dispatchers.Default) {
        try {
            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 28)
            try {
                val count = if (action == PetAction.SING) 3 else 1
                repeat(count) {
                    tone.startTone(
                        when (action) {
                            PetAction.SLEEP -> ToneGenerator.TONE_PROP_NACK
                            PetAction.WAKE -> ToneGenerator.TONE_PROP_ACK
                            else -> ToneGenerator.TONE_PROP_BEEP
                        },
                        if (action == PetAction.SING) 85 else 65
                    )
                    delay(if (action == PetAction.SING) 145L else 90L)
                }
            } finally {
                tone.release()
            }
        } catch (_: RuntimeException) {
            // Audio may be unavailable or disabled on a device; pet actions still work.
        }
    }
}
