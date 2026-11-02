package com.vunkhoi.pocketjigglypuff

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/** No forced immersive or hidden system navigation, especially on foldable displays. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PetScreen() }
    }
}
