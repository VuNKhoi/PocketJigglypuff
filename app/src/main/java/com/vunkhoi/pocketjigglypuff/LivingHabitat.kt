package com.vunkhoi.pocketjigglypuff

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.math.roundToInt

/**
 * All movement is transient UI animation: it does not consume pet needs,
 * run a service, or write to disk. Stops when the activity is not visible.
 */
@Composable
fun LivingHabitat(
    scene: PetScene,
    nighttime: Boolean,
    sleeping: Boolean,
    mood: PetMood,
    reaction: PetAction?,
    reactionId: Int,
    accessory: PetAccessory,
    onPet: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var visible by remember(lifecycleOwner) {
        mutableStateOf(
            lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        )
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, _ ->
            visible = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val wandering = remember { Animatable(0f) }
    var idleEmote by remember { mutableStateOf("") }

    LaunchedEffect(scene, sleeping, reactionId, visible) {
        if (!visible) {
            idleEmote = ""
            return@LaunchedEffect
        }
        if (sleeping) {
            idleEmote = ""
            wandering.animateTo(if (scene == PetScene.BEDROOM) -0.65f else 0.30f, tween(900))
            return@LaunchedEffect
        }
        if (reactionId > 0) {
            idleEmote = ""
            delay(1700) // let the user's reaction finish first
        }
        while (true) {
            delay(Random.nextLong(650L, 1650L))
            val destination = Random.nextFloat() * 1.6f - 0.8f
            wandering.animateTo(
                destination,
                animationSpec = tween(
                    durationMillis = Random.nextInt(1400, 2450),
                    easing = FastOutSlowInEasing
                )
            )
            idleEmote = when (Random.nextInt(8)) {
                0 -> "♫"
                1 -> "♡"
                2 -> "✦"
                else -> ""
            }
            delay(Random.nextLong(950L, 2300L))
            idleEmote = ""
        }
    }

    val colors = when {
        nighttime && scene == PetScene.GARDEN ->
            listOf(Color(0xFF423A67), Color(0xFF83709D))
        nighttime -> listOf(Color(0xFF675875), Color(0xFFB18EAC))
        scene == PetScene.GARDEN -> listOf(Color(0xFFBDEBDF), Color(0xFFFFE6ED))
        else -> listOf(Color(0xFFFFEACD), Color(0xFFFFE0DE))
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(colors))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            // A fold/resize transition can temporarily report an empty surface.
            if (size.width <= 1f || size.height <= 1f) return@Canvas
            if (scene == PetScene.BEDROOM) drawCozyRoom(nighttime)
            else drawGardenClearing(nighttime)
        }

        // Size uses *available scene height*, not full display height, and
        // horizontal travel is bounded to keep the pet entirely on-screen.
        val petSize = minOf(maxWidth * 0.73f, maxHeight * 0.61f)
        val maxTravel = (maxWidth - petSize).coerceAtLeast(0.dp) * 0.43f

        // The lambda offset is calculated in layout, not composition. Reading
        // Animatable in composition at 60-120 Hz needlessly rebuilt the stage.
        // Also dispose infinite idle animations when the activity stops.
        if (visible && petSize > 1.dp) {
            PetArt(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset {
                        IntOffset(
                            x = (maxTravel.toPx() * wandering.value).roundToInt(),
                            y = (-maxHeight * 0.09f).roundToPx()
                        )
                    }
                    .size(petSize),
                mood = mood,
                reaction = reaction,
                reactionId = reactionId,
                accessory = accessory,
                onPet = onPet
            )
        }

        if (!sleeping && reaction == null && idleEmote.isNotBlank()) {
            Text(
                text = idleEmote,
                modifier = Modifier.align(Alignment.TopCenter).offset(y = 12.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (nighttime) Color.White else Color(0xFF8B5D80)
            )
        }
    }
}

private fun DrawScope.drawCozyRoom(night: Boolean) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.75f

    // Soft wall panel and wooden floor.
    drawRect(
        color = if (night) Color(0xFF675170) else Color(0xFFFFEDD8),
        size = Size(w, floorY)
    )
    drawRect(
        color = if (night) Color(0xFF765B79) else Color(0xFFE9B8A3),
        topLeft = Offset(0f, floorY),
        size = Size(w, h - floorY)
    )
    drawLine(Color(0x66FFFFFF), Offset(0f, floorY), Offset(w, floorY), h * 0.012f)
    drawLine(
        if (night) Color(0x447D6583) else Color(0x448D6B5C),
        Offset(0f, h * 0.89f), Offset(w, h * 0.89f), 1.6f
    )

    // Window, sky and a moon/sun. Thick framing reads on a tiny cover display.
    val frameX = w * 0.11f
    val frameY = h * 0.10f
    val frameW = w * 0.27f
    val frameH = h * 0.36f
    drawRoundRect(
        color = Color(0xFFF7DFD4),
        topLeft = Offset(frameX - 4f, frameY - 4f),
        size = Size(frameW + 8f, frameH + 8f),
        cornerRadius = CornerRadius(12f)
    )
    drawRoundRect(
        color = if (night) Color(0xFF403E70) else Color(0xFFABDEEF),
        topLeft = Offset(frameX, frameY),
        size = Size(frameW, frameH),
        cornerRadius = CornerRadius(8f)
    )
    drawCircle(
        if (night) Color(0xFFFFEEB7) else Color(0xFFFFF5BE),
        radius = minOf(frameW, frameH) * 0.13f,
        center = Offset(frameX + frameW * 0.72f, frameY + frameH * 0.25f)
    )
    drawLine(
        Color(0xFFFBE8DF),
        Offset(frameX + frameW * 0.5f, frameY),
        Offset(frameX + frameW * 0.5f, frameY + frameH),
        3.3f
    )
    drawLine(
        Color(0xFFFBE8DF),
        Offset(frameX, frameY + frameH * 0.5f),
        Offset(frameX + frameW, frameY + frameH * 0.5f),
        3.3f
    )

    // Small framed illustration.
    drawRoundRect(
        Color(0xFFE0A5B6),
        topLeft = Offset(w * 0.71f, h * 0.15f),
        size = Size(w * 0.16f, h * 0.19f),
        cornerRadius = CornerRadius(6f)
    )
    drawCircle(Color(0xFFFFE4AF), w * 0.032f, Offset(w * 0.79f, h * 0.24f))

    // Cushion on the left; sleep moves toward it.
    drawOval(
        Color(0x553E3356),
        topLeft = Offset(w * 0.05f, h * 0.74f),
        size = Size(w * 0.37f, h * 0.11f)
    )
    drawRoundRect(
        Color(0xFFE7A4BF),
        topLeft = Offset(w * 0.07f, h * 0.70f),
        size = Size(w * 0.33f, h * 0.12f),
        cornerRadius = CornerRadius(h * 0.06f)
    )
    drawOval(
        Color(0xFFFFD3DF),
        topLeft = Offset(w * 0.11f, h * 0.72f),
        size = Size(w * 0.25f, h * 0.06f)
    )

    // House plant in a pot, right-hand side.
    drawRoundRect(
        Color(0xFFB87873),
        topLeft = Offset(w * 0.87f, h * 0.67f),
        size = Size(w * 0.08f, h * 0.14f),
        cornerRadius = CornerRadius(5f)
    )
    listOf(-0.045f, 0f, 0.045f).forEachIndexed { i, dx ->
        drawOval(
            Color(if (i == 1) 0xFF77B79A else 0xFF90C9A3),
            topLeft = Offset(w * (0.89f + dx), h * (0.52f + (i % 2) * 0.035f)),
            size = Size(w * 0.055f, h * 0.17f)
        )
    }
}

private fun DrawScope.drawGardenClearing(night: Boolean) {
    val w = size.width
    val h = size.height
    val grassY = h * 0.74f

    // Distant rolling hills and warm grassy ground.
    drawOval(
        color = if (night) Color(0xFF5B7791) else Color(0xFFB4D9BB),
        topLeft = Offset(-w * 0.23f, h * 0.40f),
        size = Size(w * 0.85f, h * 0.65f)
    )
    drawOval(
        color = if (night) Color(0xFF496B77) else Color(0xFFA0CFAF),
        topLeft = Offset(w * 0.40f, h * 0.43f),
        size = Size(w * 0.90f, h * 0.60f)
    )
    drawRect(
        if (night) Color(0xFF466E6A) else Color(0xFF89BE98),
        topLeft = Offset(0f, grassY),
        size = Size(w, h - grassY)
    )

    if (night) {
        repeat(9) { i ->
            val x = ((i * 31 + 9) % 95) / 100f * w
            val y = ((i * 19 + 7) % 49) / 100f * h
            drawCircle(Color(0xDDFFFFD5), if (i % 3 == 0) 2.4f else 1.3f, Offset(x, y))
        }
        drawCircle(Color(0xFFFFF0BD), w * 0.07f, Offset(w * 0.76f, h * 0.17f))
    } else {
        drawOval(Color(0xBFFFFFFF), Offset(w * 0.16f, h * 0.12f), Size(w * 0.19f, h * 0.09f))
        drawOval(Color(0xCCFFFFFF), Offset(w * 0.22f, h * 0.09f), Size(w * 0.20f, h * 0.13f))
        drawCircle(Color(0xFFFFF0AF), w * 0.065f, Offset(w * 0.78f, h * 0.16f))
    }

    // A friendly tree lives at the edge, leaving the central stage open.
    drawRoundRect(
        Color(0xFF9D7461),
        topLeft = Offset(w * 0.87f, h * 0.32f),
        size = Size(w * 0.08f, h * 0.45f),
        cornerRadius = CornerRadius(10f)
    )
    drawCircle(
        if (night) Color(0xFF548979) else Color(0xFF76B99A),
        w * 0.16f, Offset(w * 0.90f, h * 0.29f)
    )
    drawCircle(
        if (night) Color(0xFF6C9A89) else Color(0xFF91CEAD),
        w * 0.11f, Offset(w * 0.81f, h * 0.25f)
    )

    // Flowers and a mushroom near the edges to preserve a large walking lane.
    for ((fx, fy) in listOf(0.10f to 0.81f, 0.20f to 0.87f, 0.78f to 0.88f)) {
        val p = Offset(w * fx, h * fy)
        drawLine(Color(0xFF568E6C), Offset(p.x, p.y + 7f), Offset(p.x, p.y - 8f), 2.2f)
        repeat(5) { petal ->
            val angle = petal * 6.28318f / 5f
            drawCircle(
                if (night) Color(0xFFC0A3CB) else Color(0xFFF6AFBF),
                h * 0.014f,
                Offset(
                    p.x + kotlin.math.cos(angle) * h * 0.023f,
                    p.y - 8f + kotlin.math.sin(angle) * h * 0.023f
                )
            )
        }
        drawCircle(Color(0xFFFFD57D), h * 0.011f, Offset(p.x, p.y - 8f))
    }

    drawRoundRect(
        Color(0xFFF7E1C3),
        Offset(w * 0.31f, h * 0.83f),
        Size(w * 0.04f, h * 0.075f),
        CornerRadius(4f)
    )
    drawOval(
        Color(0xFFDB8DA4),
        Offset(w * 0.284f, h * 0.81f),
        Size(w * 0.095f, h * 0.055f)
    )
}
