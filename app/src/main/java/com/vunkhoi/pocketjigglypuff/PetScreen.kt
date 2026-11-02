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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ink = Color(0xFF5B3F5B)

@Composable
fun PetScreen() {
    val context = LocalContext.current.applicationContext
    val store = remember(context) { PetStore(context) }
    val taps by store.tapCount.collectAsState(initial = 0)
    val scope = rememberCoroutineScope()

    MaterialTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF7FC), Color(0xFFFFDDEA))
                    )
                )
        ) {
            val compact = maxHeight < 520.dp
            val petSize = minOf(
                maxWidth * 0.72f,
                maxHeight * (if (compact) 0.55f else 0.40f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 18.dp,
                        vertical = if (compact) 10.dp else 28.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PocketJigglypuff",
                        color = ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compact) 19.sp else 26.sp
                    )
                    Text(
                        text = "Your tiny pocket friend",
                        color = Color(0xFF986C89),
                        fontSize = if (compact) 11.sp else 15.sp
                    )
                }

                PinkPet(
                    modifier = Modifier.size(petSize),
                    onPet = { scope.launch { store.recordTap() } }
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (taps == 0) "Say hi!" else "Happy to see you!",
                        color = ink,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (compact) 15.sp else 20.sp
                    )
                    Text(
                        text = "Pets: " + taps,
                        color = ink,
                        fontSize = if (compact) 13.sp else 17.sp
                    )
                    Text(
                        text = "Tap the little puff",
                        color = Color(0xFF986C89),
                        textAlign = TextAlign.Center,
                        fontSize = if (compact) 10.sp else 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PinkPet(
    modifier: Modifier = Modifier,
    onPet: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bounce = remember { Animatable(1f) }
    val transition = rememberInfiniteTransition(label = "idle_bob")
    val bob by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    var blinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2800)
            blinking = true
            delay(130)
            blinking = false
        }
    }
    val eyeOpen by animateFloatAsState(
        targetValue = if (blinking) 0.10f else 1f,
        animationSpec = tween(75),
        label = "blink"
    )

    fun react() {
        onPet()
        scope.launch {
            bounce.snapTo(0.90f)
            bounce.animateTo(1.10f, tween(150))
            bounce.animateTo(1f, spring(stiffness = Spring.StiffnessMediumLow))
        }
    }

    Canvas(
        modifier = modifier
            .graphicsLayer {
                scaleX = bounce.value
                scaleY = bounce.value
                translationY = bob
            }
            .pointerInput(Unit) { detectTapGestures { react() } }
            .semantics {
                contentDescription = "Pet the pink puff"
                onClick(label = "Pet the puff") {
                    react()
                    true
                }
            }
    ) {
        val u = size.minDimension / 100f
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Soft floor shadow.
        drawOval(
            color = Color(0x22B56F9B),
            topLeft = Offset(cx - 32f * u, cy + 35f * u),
            size = Size(64f * u, 11f * u)
        )

        // Original placeholder ears, intentionally not an imported Pokémon sprite.
        rotate(-22f, pivot = Offset(cx - 22f * u, cy - 23f * u)) {
            drawOval(
                color = Color(0xFFFFAFCF),
                topLeft = Offset(cx - 34f * u, cy - 45f * u),
                size = Size(23f * u, 30f * u)
            )
            drawOval(
                color = Color(0xFFE886B6),
                topLeft = Offset(cx - 28f * u, cy - 40f * u),
                size = Size(12f * u, 18f * u)
            )
        }
        rotate(22f, pivot = Offset(cx + 22f * u, cy - 23f * u)) {
            drawOval(
                color = Color(0xFFFFAFCF),
                topLeft = Offset(cx + 11f * u, cy - 45f * u),
                size = Size(23f * u, 30f * u)
            )
            drawOval(
                color = Color(0xFFE886B6),
                topLeft = Offset(cx + 16f * u, cy - 40f * u),
                size = Size(12f * u, 18f * u)
            )
        }

        drawCircle(
            color = Color(0xFFFFBFD8),
            radius = 35f * u,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = Color(0xFFFFD2E3),
            radius = 29f * u,
            center = Offset(cx - 4f * u, cy - 5f * u)
        )

        // Blinking eyes and tiny highlights.
        listOf(-13f, 13f).forEach { x ->
            drawOval(
                color = Color(0xFF317F83),
                topLeft = Offset(cx + (x - 6f) * u, cy - 8f * u),
                size = Size(12f * u, 15f * u * eyeOpen)
            )
            if (eyeOpen > 0.5f) {
                drawCircle(
                    color = Color.White,
                    radius = 2.8f * u,
                    center = Offset(cx + (x - 2f) * u, cy - 4f * u)
                )
            }
        }

        drawCircle(
            color = Color(0x66F57DAA),
            radius = 6f * u,
            center = Offset(cx - 23f * u, cy + 10f * u)
        )
        drawCircle(
            color = Color(0x66F57DAA),
            radius = 6f * u,
            center = Offset(cx + 23f * u, cy + 10f * u)
        )

        drawArc(
            color = ink,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - 5f * u, cy + 8f * u),
            size = Size(10f * u, 8f * u),
            style = Stroke(width = 2f * u, cap = StrokeCap.Round)
        )
    }
}
