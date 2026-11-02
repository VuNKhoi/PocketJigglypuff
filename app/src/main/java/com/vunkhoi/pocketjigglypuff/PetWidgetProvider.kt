package com.vunkhoi.pocketjigglypuff

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Battery-friendly, static widget; Samsung cover support needs physical testing. */
class PetWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pet = PetStore(context).state.first()
                ids.forEach { id ->
                    val views = RemoteViews(context.packageName, R.layout.widget_pet)
                    views.setTextViewText(R.id.widget_mood, when (pet.mood) {
                        PetMood.ASLEEP -> "Shhh... sweet dreams 💤"
                        PetMood.SLEEPY -> "Feeling sleepy..."
                        PetMood.HUNGRY -> "A little snack? 🍓"
                        PetMood.HAPPY -> "So happy to see you! ♡"
                        PetMood.CONTENT -> "Hello, pocket friend!"
                    })
                    views.setTextViewText(
                        R.id.widget_stats,
                        "♡ " + pet.happiness + "  •  🍓 " + pet.fullness +
                            "  •  ⚡ " + pet.energy + "  •  ✦ " + pet.stars
                    )
                    views.setTextViewText(R.id.widget_face, if (pet.sleeping) "◕ ᴗ ◕ ᶻ" else "◕ ᴗ ◕")
                    val petIntent = Intent(context, PetWidgetProvider::class.java).apply {
                        action = ACTION_PET
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                    }
                    val petPending = PendingIntent.getBroadcast(
                        context, id, petIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val openPending = PendingIntent.getActivity(
                        context, id, Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_pet_button, petPending)
                    views.setOnClickPendingIntent(R.id.widget_open_button, openPending)
                    manager.updateAppWidget(id, views)
                }
            } finally {
                pending.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_PET) {
            val pending = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    PetStore(context).act(PetAction.PET)
                } finally {
                    pending.finish()
                }
            }
        }
    }

    companion object {
        private const val ACTION_PET = "com.vunkhoi.pocketjigglypuff.WIDGET_PET"

        /** Updates only after state changes; no polling, no background service. */
        fun requestUpdate(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, PetWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return
            val intent = Intent(context, PetWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(intent)
        }
    }
}
