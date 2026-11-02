package com.vunkhoi.pocketjigglypuff

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val outline = Color(0xFF6D4166)

/** Original vector-drawn placeholder mascot, no copyrighted sprite files. */
@Composable
fun PetArt(
    modifier: Modifier,
    mood: PetMood,
    reaction: PetAction?,
    reactionId: Int,
    accessory: PetAccessory,
    onPet: () -> Unit
) {
    val sleeping = mood == PetMood.ASLEEP && reaction != PetAction.WAKE
    val scale = remember { Animatable(1f) }
    val idle = rememberInfiniteTransition(label = "idle")
    val bob by idle.animateFloat(
        initialValue = -3.5f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1550), RepeatMode.Reverse),
        label = "bob"
    )
    val tilt by idle.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(tween(1950), RepeatMode.Reverse),
        label = "tilt"
    )
    var blinking by remember { mutableStateOf(false) }
    LaunchedEffect(sleeping) {
        while (!sleeping) {
            delay(2600)
            blinking = true
            delay(130)
            blinking = false
        }
    }
    val eyelid by animateFloatAsState(
        targetValue = if (sleeping || blinking) 0.1f else 1f,
        animationSpec = tween(85),
        label = "eyelid"
    )
    LaunchedEffect(reactionId) {
        if (reactionId == 0) return@LaunchedEffect
        scale.snapTo(0.87f)
        scale.animateTo(1.12f, tween(150))
        scale.animateTo(1f, spring(stiffness = Spring.StiffnessMediumLow))
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationY = if (sleeping) 2f else bob
                    rotationZ = if (sleeping) -8f else tilt
                }
                .pointerInput(onPet) { detectTapGestures { onPet() } }
                .semantics {
                    contentDescription = if (sleeping) "Sleeping pet; tap to wake" else "Pet your tiny friend"
                    onClick(label = "Pet or wake") {
                        onPet()
                        true
                    }
                }
        ) {
            val u = size.minDimension / 100f
            val x = size.width / 2f
            val y = size.height / 2f

            drawOval(Color(0x22A35D94), Offset(x-32f*u,y+34f*u), Size(64f*u,12f*u))
            drawOval(Color(0xFFF297C6), Offset(x-26f*u,y+19f*u), Size(24f*u,18f*u))
            drawOval(Color(0xFFF297C6), Offset(x+2f*u,y+19f*u), Size(24f*u,18f*u))

            rotate(-24f,pivot=Offset(x-22f*u,y-23f*u)) {
                drawOval(Color(0xFFFFB5D5),Offset(x-35f*u,y-46f*u),Size(24f*u,31f*u))
                drawOval(Color(0xFFE98DB9),Offset(x-29f*u,y-40f*u),Size(12f*u,19f*u))
            }
            rotate(24f,pivot=Offset(x+22f*u,y-23f*u)) {
                drawOval(Color(0xFFFFB5D5),Offset(x+11f*u,y-46f*u),Size(24f*u,31f*u))
                drawOval(Color(0xFFE98DB9),Offset(x+17f*u,y-40f*u),Size(12f*u,19f*u))
            }

            drawCircle(Color(0xFFFFBAD7),36f*u,Offset(x,y))
            drawCircle(Color(0xFFFFCDE3),29f*u,Offset(x-4f*u,y-5f*u))
            drawOval(Color(0xFFFF9FCA),Offset(x-36f*u,y+3f*u),Size(15f*u,16f*u))
            drawOval(Color(0xFFFF9FCA),Offset(x+21f*u,y+3f*u),Size(15f*u,16f*u))

            listOf(-13f,13f).forEach { dx ->
                if (eyelid > 0.3f) {
                    drawOval(Color(0xFF3A909B),Offset(x+(dx-5f)*u,y-10f*u),Size(10f*u,16f*u*eyelid))
                    drawCircle(Color.White,2.6f*u,Offset(x+(dx-2.5f)*u,y-6.5f*u))
                } else {
                    drawArc(
                        color=outline,startAngle=10f,sweepAngle=160f,useCenter=false,
                        topLeft=Offset(x+(dx-6f)*u,y-2f*u),size=Size(12f*u,6f*u),
                        style=Stroke(1.7f*u,cap=StrokeCap.Round)
                    )
                }
            }
            drawCircle(Color(0x77F36CA4),6f*u,Offset(x-24f*u,y+8f*u))
            drawCircle(Color(0x77F36CA4),6f*u,Offset(x+24f*u,y+8f*u))
            if (!sleeping) {
                val singing = reaction == PetAction.SING
                drawArc(
                    color=outline,
                    startAngle=if (singing) 0f else 10f,
                    sweepAngle=if (singing) 360f else 160f,
                    useCenter=false,
                    topLeft=Offset(x-5f*u,y+7f*u),
                    size=Size(10f*u,if (singing) 10f*u else 7f*u),
                    style=Stroke(2f*u,cap=StrokeCap.Round)
                )
            } else {
                drawLine(outline,Offset(x-4f*u,y+12f*u),Offset(x+4f*u,y+12f*u),2f*u)
            }
        }
        val decoration = when (accessory) {
            PetAccessory.NONE -> ""
            PetAccessory.BOW -> "🎀"
            PetAccessory.FLOWER -> "🌸"
            PetAccessory.STAR -> "⭐"
        }
        if (decoration.isNotBlank()) {
            Text(decoration,modifier=Modifier.align(Alignment.TopCenter),fontSize=26.sp)
        }
        val particle = when (reaction) {
            PetAction.PET, PetAction.WAKE -> "💗"
            PetAction.FEED -> "🍓"
            PetAction.PLAY -> "✨"
            PetAction.SING -> "♫"
            PetAction.SLEEP -> "💤"
            null -> if (sleeping) "ᶻ ᶻ ᶻ" else ""
        }
        if (particle.isNotBlank()) {
            Text(particle,modifier=Modifier.align(Alignment.TopEnd),fontSize=25.sp)
        }
    }
}
