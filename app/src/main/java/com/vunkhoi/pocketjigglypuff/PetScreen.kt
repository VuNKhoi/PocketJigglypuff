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
import androidx.compose.runtime.collectAsState
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
    val pet by store.state.collectAsState(initial = PetState())
    val scope = rememberCoroutineScope()
    var reaction by remember { mutableStateOf<PetAction?>(null) }
    var reactionId by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Tap your little friend!") }
    var showSettings by remember { mutableStateOf(false) }
    var showCollection by remember { mutableStateOf(false) }

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
            SceneDecor(pet.scene,nighttime)
            val compact = maxHeight < 510.dp
            val petSize = minOf(
                maxWidth * (if (compact) 0.65f else 0.73f),
                maxHeight * (if (compact) 0.44f else 0.49f)
            )
            val label = if (nighttime) Color.White else purple
            val subtle = if (nighttime) Color(0xFFE8DDF2) else plum

            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal=if (compact) 13.dp else 20.dp,vertical=8.dp),
                horizontalAlignment=Alignment.CenterHorizontally,
                verticalArrangement=Arrangement.SpaceBetween
            ) {
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    verticalAlignment=Alignment.CenterVertically,
                    horizontalArrangement=Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("PocketJigglypuff",fontWeight=FontWeight.Bold,color=label,
                            fontSize=if (compact) 18.sp else 24.sp)
                        Text("✦ ${pet.stars} stars  •  ${pet.interactions} moments",
                            modifier=Modifier.clickable { showCollection = true },
                            color=subtle,fontSize=if (compact) 11.sp else 13.sp)
                    }
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        IconButton(onClick={ scope.launch { store.changeScene() } }) {
                            Text(if (pet.scene == PetScene.GARDEN) "🌷" else "🛏",fontSize=21.sp)
                        }
                        IconButton(onClick={ showSettings = true }) {
                            Text("⚙",color=label,fontSize=25.sp)
                        }
                    }
                }

                Box(
                    modifier=Modifier.fillMaxWidth().weight(1f),
                    contentAlignment=Alignment.Center
                ) {
                    PetArt(
                        modifier=Modifier.size(petSize),mood=pet.mood,
                        reaction=reaction,reactionId=reactionId,
                        accessory=pet.accessory,
                        onPet={ respond(PetAction.PET) }
                    )
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
                    maxLines=1,fontSize=if (compact) 13.sp else 18.sp
                )

                Row(
                    modifier=Modifier.fillMaxWidth().padding(vertical=if (compact) 5.dp else 14.dp),
                    horizontalArrangement=Arrangement.SpaceEvenly
                ) {
                    NeedMeter("♡",pet.happiness,Color(0xFFEA759F),subtle,compact)
                    NeedMeter("🍓",pet.fullness,Color(0xFFEBA05E),subtle,compact)
                    NeedMeter("⚡",pet.energy,Color(0xFF70B2B0),subtle,compact)
                }
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement=Arrangement.SpaceEvenly,
                    verticalAlignment=Alignment.CenterVertically
                ) {
                    ActionTile("🍓","Feed",compact,nighttime,pet.sleeping) { respond(PetAction.FEED) }
                    ActionTile("🎾","Play",compact,nighttime,pet.sleeping) { respond(PetAction.PLAY) }
                    ActionTile("♫","Sing",compact,nighttime,pet.sleeping) { respond(PetAction.SING) }
                    ActionTile(
                        if (pet.sleeping) "☀" else "💤",
                        if (pet.sleeping) "Wake" else "Sleep",
                        compact,nighttime,false
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
private fun NeedMeter(icon:String,value:Int,tint:Color,textColor:Color,compact:Boolean) {
    Column(horizontalAlignment=Alignment.CenterHorizontally) {
        Text("$icon $value",fontSize=if (compact) 11.sp else 13.sp,color=textColor)
        LinearProgressIndicator(
            progress={ value/100f },
            modifier=Modifier.width(if (compact) 72.dp else 96.dp).height(5.dp),
            color=tint,trackColor=Color(0x44FFFFFF)
        )
    }
}

@Composable
private fun ActionTile(
    icon:String,label:String,compact:Boolean,night:Boolean,disabled:Boolean,onClick:()->Unit
) {
    Column(
        modifier=Modifier.size(
            width=if (compact) 72.dp else 88.dp,
            height=if (compact) 56.dp else 72.dp
        ).background(
            if (night) Color(0xFFFBDEEB).copy(alpha=if (disabled) 0.5f else 1f)
            else cream.copy(alpha=if (disabled) 0.55f else 0.92f),
            RoundedCornerShape(if (compact) 14.dp else 20.dp)
        ).clickable(enabled=!disabled,onClick=onClick).padding(4.dp),
        horizontalAlignment=Alignment.CenterHorizontally,
        verticalArrangement=Arrangement.Center
    ) {
        Text(icon,fontSize=if (compact) 22.sp else 27.sp)
        Text(label,fontSize=if (compact) 11.sp else 13.sp,
            fontWeight=FontWeight.Bold,color=purple)
    }
}

@Composable
private fun SceneDecor(scene:PetScene,nighttime:Boolean) {
    Canvas(Modifier.fillMaxSize()) {
        val w=size.width
        val h=size.height
        if (nighttime) {
            repeat(18) { i ->
                val x=(i*71%97)/100f*w
                val y=(i*37%63)/100f*h
                drawCircle(Color(0x99FFFFFF),if (i%3==0) 2.1f else 1f,Offset(x,y))
            }
            drawCircle(Color(0x66FFF1BB),w*0.10f,Offset(w*0.85f,h*0.18f))
        } else if (scene==PetScene.GARDEN) {
            drawCircle(Color(0x88FFFFFF),w*0.13f,Offset(w*0.18f,h*0.22f))
            drawCircle(Color(0x66FFFFFF),w*0.11f,Offset(w*0.78f,h*0.16f))
        } else {
            drawCircle(Color(0x55FFF3B4),w*0.16f,Offset(w*0.80f,h*0.21f))
        }
    }
}
