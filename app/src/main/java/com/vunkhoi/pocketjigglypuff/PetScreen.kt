package com.vunkhoi.pocketjigglypuff

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

private val purple = Color(0xFF63445F)
private val plum = Color(0xFF986C89)
private val cream = Color(0xFFFFF5FA)

@Composable
fun PetScreen() {
    val context = LocalContext.current.applicationContext
    val view = LocalView.current
    val store = remember(context) { PetStore(context) }
    val pet by store.state.collectAsStateWithLifecycle(initialValue = PetState())
    val scope = rememberCoroutineScope()
    var reaction by remember { mutableStateOf<PetAction?>(null) }
    var reactionId by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Tap your little friend!") }
    var showSettings by remember { mutableStateOf(false) }
    var showCollection by remember { mutableStateOf(false) }
    // Safe by default: never persist opt-in graphics after a reported Razr shutdown.
    var animatedHabitat by remember { mutableStateOf(false) }

    fun respond(action: PetAction) {
        val choice = if (pet.sleeping && action != PetAction.WAKE) PetAction.WAKE else action
        scope.launch { store.act(choice) }
        if (pet.soundOn) scope.launch { PetFeedback.play(choice) }
        if (pet.hapticsOn) view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        reaction = choice
        reactionId += 1
        message = when (choice) {
            PetAction.PET -> listOf("You're my favorite! ♡", "That tickles!", "So much love!")[reactionId % 3]
            PetAction.FEED -> "Yum! Berry delicious! 🍓"
            PetAction.PLAY -> "Wheee! Let's play!"
            PetAction.SING -> "La la la~ ♫"
            PetAction.SLEEP -> "Sweet dreams, little puff."
            PetAction.WAKE -> "Morning, sunshine! ☀"
        }
    }
    LaunchedEffect(reactionId) {
        if (reactionId > 0) {
            delay(1500)
            reaction = null
        }
    }
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val nighttime = hour >= 19 || hour < 7

    MaterialTheme {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        if (nighttime) listOf(Color(0xFF342A59),Color(0xFF78609C))
                        else if (pet.scene == PetScene.GARDEN)
                            listOf(Color(0xFFF5E7FF),Color(0xFFFFE4F0))
                        else listOf(Color(0xFFFFF0DA),Color(0xFFFFD9E8))
                    )
                )
                .padding(WindowInsets.safeDrawing.asPaddingValues())
        ) {
            // A square cover display and a tall inner display need different spacing.
            // In particular, never assume that four fixed-width buttons fit.
            val narrow = maxWidth < 330.dp
            val compact = maxHeight < 530.dp || maxWidth < 380.dp
            val shortDisplay = maxHeight < 330.dp
            val label = if (nighttime) Color.White else purple
            val subtle = if (nighttime) Color(0xFFE8DDF2) else plum

            Column(
                modifier = Modifier.fillMaxHeight()
                    .widthIn(max=480.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal=if (compact) 10.dp else 20.dp,vertical=if (shortDisplay) 4.dp else 8.dp),
                horizontalAlignment=Alignment.CenterHorizontally,
                verticalArrangement=Arrangement.SpaceBetween
            ) {
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    verticalAlignment=Alignment.CenterVertically,
                    horizontalArrangement=Arrangement.SpaceBetween
                ) {
                    Column(modifier=Modifier.weight(1f)) {
                        Text(if (narrow) "Pocket Puff" else "PocketJigglypuff",
                            fontWeight=FontWeight.Bold,color=label,
                            maxLines=1,overflow=TextOverflow.Ellipsis,
                            fontSize=if (narrow) 16.sp else if (compact) 18.sp else 24.sp)
                        Text("✦ ${pet.stars} stars  •  ${pet.interactions} moments",
                            modifier=Modifier.clickable { showCollection = true },
                            maxLines=1,overflow=TextOverflow.Ellipsis,
                            color=subtle,fontSize=if (compact) 10.sp else 13.sp)
                    }
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        IconButton(
                            modifier=Modifier.size(if (compact) 40.dp else 48.dp),
                            onClick={ scope.launch { store.changeScene() } }
                        ) {
                            Text(if (pet.scene == PetScene.GARDEN) "🌷" else "🛏",fontSize=21.sp)
                        }
                        IconButton(
                            modifier=Modifier.size(if (compact) 40.dp else 48.dp),
                            onClick={ showSettings = true }
                        ) {
                            Text("⚙",color=label,fontSize=25.sp)
                        }
                    }
                }

                Box(
                    modifier=Modifier.fillMaxWidth().weight(1f)
                        .padding(vertical=if (shortDisplay) 2.dp else 6.dp)
                ) {
                    if (animatedHabitat) {
                        LivingHabitat(
                            scene=pet.scene,
                            nighttime=nighttime,
                            sleeping=pet.sleeping,
                            mood=pet.mood,
                            reaction=reaction,
                            reactionId=reactionId,
                            accessory=pet.accessory,
                            onPet={ respond(PetAction.PET) }
                        )
                    } else {
                        Box(
                            modifier=Modifier.fillMaxSize()
                                .background(
                                    if (nighttime) Color(0xFF76658A)
                                    else Color(0xFFFFD9E8),
                                    RoundedCornerShape(24.dp)
                                ),
                            contentAlignment=Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment=Alignment.CenterHorizontally,
                                verticalArrangement=Arrangement.Center,
                                modifier=Modifier.clickable { respond(PetAction.PET) }
                            ) {
                                Text(
                                    if (pet.sleeping) "ᶻ ᶻ ᶻ" else "♡",
                                    fontSize=22.sp,color=Color(0xFF90587B)
                                )
                                Text("◕ ᴗ ◕",fontSize=if (compact) 45.sp else 72.sp,
                                    color=Color(0xFF985F83))
                                Text(
                                    "Safe graphics mode",
                                    fontSize=11.sp,
                                    color=if (nighttime) Color.White else purple
                                )
                            }
                        }
                    }
                }

                Text(
                    text=if (reaction != null) message else when (pet.mood) {
                        PetMood.ASLEEP -> "Shhh... having sweet dreams."
                        PetMood.SLEEPY -> "A cozy nap sounds nice."
                        PetMood.HUNGRY -> "A tiny snack would be lovely!"
                        PetMood.HAPPY -> "Hi! Tap me for a hug ♡"
                        PetMood.CONTENT -> "What should we do today?"
                    },
                    color=label,fontWeight=FontWeight.SemiBold,textAlign=TextAlign.Center,
                    maxLines=1,overflow=TextOverflow.Ellipsis,
                    fontSize=if (compact) 12.sp else 18.sp
                )

                Row(
                    modifier=Modifier.fillMaxWidth().padding(vertical=if (compact) 5.dp else 14.dp),
                    horizontalArrangement=Arrangement.SpaceEvenly
                ) {
                    NeedMeter("♡",pet.happiness,Color(0xFFEA759F),subtle,compact,Modifier.weight(1f))
                    NeedMeter("🍓",pet.fullness,Color(0xFFEBA05E),subtle,compact,Modifier.weight(1f))
                    NeedMeter("⚡",pet.energy,Color(0xFF70B2B0),subtle,compact,Modifier.weight(1f))
                }
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement=Arrangement.SpaceEvenly,
                    verticalAlignment=Alignment.CenterVertically
                ) {
                    ActionTile("🍓","Feed",compact,nighttime,pet.sleeping,
                        Modifier.weight(1f).padding(horizontal=2.dp)) { respond(PetAction.FEED) }
                    ActionTile("🎾","Play",compact,nighttime,pet.sleeping,
                        Modifier.weight(1f).padding(horizontal=2.dp)) { respond(PetAction.PLAY) }
                    ActionTile("♫","Sing",compact,nighttime,pet.sleeping,
                        Modifier.weight(1f).padding(horizontal=2.dp)) { respond(PetAction.SING) }
                    ActionTile(
                        if (pet.sleeping) "☀" else "💤",
                        if (pet.sleeping) "Wake" else "Sleep",
                        compact,nighttime,false,
                        Modifier.weight(1f).padding(horizontal=2.dp)
                    ) { respond(if (pet.sleeping) PetAction.WAKE else PetAction.SLEEP) }
                }
            }
        }

        if (showSettings) {
            AlertDialog(
                onDismissRequest={ showSettings = false },
                title={ Text("Little comforts") },
                text={
                    Column(modifier=Modifier.verticalScroll(rememberScrollState())) {
                        Text("No alarms, no chores, just a happy pocket friend.")
                        TextButton(onClick={ scope.launch { store.setSound(!pet.soundOn) } }) {
                            Text("Sound: ${if (pet.soundOn) "On 🔊" else "Off 🔇"}")
                        }
                        TextButton(onClick={ scope.launch { store.setHaptics(!pet.hapticsOn) } }) {
                            Text("Haptics: ${if (pet.hapticsOn) "On" else "Off"}")
                        }
                        TextButton(onClick={ scope.launch { store.changeScene() } }) {
                            Text("Scene: ${if (pet.scene == PetScene.GARDEN) "Garden 🌷" else "Cozy room 🛏"}")
                        }
                        TextButton(onClick={ scope.launch { store.cycleAccessory() } }) {
                            Text("Accessory: " + when (pet.accessory) {
                                PetAccessory.NONE -> "None"
                                PetAccessory.BOW -> "Bow 🎀"
                                PetAccessory.FLOWER -> "Flower 🌸"
                                PetAccessory.STAR -> "Star ⭐"
                            })
                        }
                        Text(
                            "Razr diagnostic: animation is OFF on launch. " +
                                "Do not enable the habitat until the shutdown is understood."
                        )
                        TextButton(onClick={ animatedHabitat = !animatedHabitat }) {
                            Text(if (animatedHabitat) "Use safe graphics" else "Enable animated habitat (experimental)")
                        }
                    }
                },
                confirmButton={ TextButton(onClick={ showSettings = false }) { Text("Done") } }
            )
        }
        if (showCollection) {
            AlertDialog(
                onDismissRequest={ showCollection = false },
                title={ Text("Our little memories ✨") },
                text={
                    Column(modifier=Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(7.dp)) {
                        Text("✦ ${pet.stars} stars earned")
                        Text("♡ ${pet.pets} pets  •  🍓 ${pet.feeds} snacks")
                        Text("🎾 ${pet.plays} games  •  ♫ ${pet.songs} songs")
                        Text("💤 ${pet.naps} naps")
                        Spacer(Modifier.height(4.dp))
                        Text("Badges",fontWeight=FontWeight.Bold)
                        if (pet.achievements.isEmpty()) Text("Your first surprise is just a tap away!")
                        pet.achievements.forEach { Text("🏅 $it") }
                    }
                },
                confirmButton={ TextButton(onClick={ showCollection = false }) { Text("Back to my pet") } }
            )
        }
    }
}

@Composable
private fun NeedMeter(
    icon:String,value:Int,tint:Color,textColor:Color,compact:Boolean,modifier:Modifier
) {
    Column(modifier=modifier,horizontalAlignment=Alignment.CenterHorizontally) {
        Text("$icon $value",fontSize=if (compact) 11.sp else 13.sp,color=textColor)
        LinearProgressIndicator(
            progress={ value/100f },
            modifier=Modifier.fillMaxWidth(0.82f).height(5.dp),
            color=tint,trackColor=Color(0x44FFFFFF)
        )
    }
}

@Composable
private fun ActionTile(
    icon:String,label:String,compact:Boolean,night:Boolean,disabled:Boolean,
    modifier:Modifier,onClick:()->Unit
) {
    Column(
        modifier=modifier.height(if (compact) 54.dp else 72.dp).background(
            if (night) Color(0xFFFBDEEB).copy(alpha=if (disabled) 0.5f else 1f)
            else cream.copy(alpha=if (disabled) 0.55f else 0.92f),
            RoundedCornerShape(if (compact) 14.dp else 20.dp)
        ).clickable(enabled=!disabled,onClick=onClick).padding(4.dp),
        horizontalAlignment=Alignment.CenterHorizontally,
        verticalArrangement=Arrangement.Center
    ) {
        Text(icon,fontSize=if (compact) 20.sp else 27.sp,
            maxLines=1)
        Text(label,fontSize=if (compact) 10.sp else 13.sp,
            fontWeight=FontWeight.Bold,color=purple,maxLines=1)
    }
}

