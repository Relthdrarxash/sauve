package fr.sauve.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sauve.app.data.VictimProfile
import fr.sauve.app.engine.HapticMetronome
import fr.sauve.app.engine.SpeechEngine
import fr.sauve.app.ui.theme.EmergencyGreen
import fr.sauve.app.ui.theme.EmergencyOrange
import fr.sauve.app.ui.theme.EmergencyRed

enum class CardiacStep {
    CHECK_CONSCIOUSNESS,
    CHECK_BREATHING,
    CALL_ALERT,
    CPR_ACTIVE,
}

@Composable
fun CardiacScreen(
    metronome: HapticMetronome,
    speechEngine: SpeechEngine,
    onBack: () -> Unit,
    onNavigateToPls: () -> Unit,
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(CardiacStep.CHECK_CONSCIOUSNESS) }
    var profile by remember { mutableStateOf(VictimProfile.ADULT) }
    var isContinuousMode by remember { mutableStateOf(true) } // Continu par défaut (recommandé grand public)
    var showDaeDialog by remember { mutableStateOf(false) }

    var compressionCount by remember { mutableIntStateOf(0) }
    var isBreathingPhase by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    // Arrêt du métronome quand on quitte l'écran
    DisposableEffect(Unit) {
        onDispose {
            metronome.stop()
            speechEngine.stop()
        }
    }

    // Gestion du métronome
    fun toggleCpr() {
        if (isRunning) {
            metronome.stop()
            speechEngine.stop()
            isRunning = false
        } else {
            isRunning = true
            speechEngine.speak("Massez au centre de la poitrine au rythme des vibrations")
            metronome.start(
                bpm = 110,
                cycleLength = 30,
                continuous = isContinuousMode,
            ) { count, breath, totalSeconds ->
                compressionCount = count
                isBreathingPhase = breath
                elapsedSeconds = totalSeconds

                // Voix de guidage
                if (breath) {
                    speechEngine.speak("2 insufflations !")
                } else if (!isContinuousMode && count % 30 == 1) {
                    speechEngine.speak("Reprenez le massage")
                } else if (count % 10 == 0 && count % 30 != 0) {
                    speechEngine.speak("$count")
                }

                // Alerte de relais toutes les 2 minutes (120 secondes)
                if (totalSeconds > 0 && totalSeconds % 120L == 0L) {
                    speechEngine.speak("Deux minutes écoulées : relayez-vous si vous êtes deux !")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // En-tête de retour et sélecteur de profil
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = {
                metronome.stop()
                speechEngine.stop()
                onBack()
            }) {
                Text("← Accueil", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            }

            // Boutons profil
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                VictimProfile.entries.forEach { p ->
                    FilterChip(
                        selected = profile == p,
                        onClick = { profile = p },
                        label = { Text(p.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmergencyRed,
                            selectedLabelColor = Color.White,
                        ),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (step) {
            CardiacStep.CHECK_CONSCIOUSNESS -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak("Secouez doucement les épaules. Demandez : m'entendez-vous ?")
                }
                EmergencyStepCard(
                    title = "1. Conscience de la victime",
                    instruction = "Secouez doucement les épaules et demandez fort : « M'entendez-vous ? Serrez-moi la main ! »",
                    primaryActionLabel = "Ne réagit pas (Inconscient)",
                    primaryActionColor = EmergencyRed,
                    onPrimaryAction = { step = CardiacStep.CHECK_BREATHING },
                    secondaryActionLabel = "Réagit / Parle",
                    onSecondaryAction = { onNavigateToPls() },
                )
            }

            CardiacStep.CHECK_BREATHING -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak("Basculez prudemment la tête en arrière. Regardez si la poitrine se soulève pendant dix secondes.")
                }
                EmergencyStepCard(
                    title = "2. Respiration (10 secondes max)",
                    instruction = "Basculez prudemment la tête en arrière pour ouvrir les voies respiratoires. Regardez et écoutez : la poitrine se soulève-t-elle normalement ?",
                    hint = "Attention : un râle bruyant ou gasps n'est PAS une respiration normale.",
                    primaryActionLabel = "Ne respire pas / Respiration anormale",
                    primaryActionColor = EmergencyRed,
                    onPrimaryAction = { step = CardiacStep.CALL_ALERT },
                    secondaryActionLabel = "Respire normalement",
                    onSecondaryAction = { onNavigateToPls() },
                )
            }

            CardiacStep.CALL_ALERT -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak("Alertez immédiatement les secours. Demandez un défibrillateur !")
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Alerte Urgente",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyRed,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Appelez le 15 ou le 112 en haut-parleur sans quitter la victime.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Précisez immédiatement :\n• Arrêt cardiaque\n• Votre adresse exacte\n• Envoyez un témoin chercher un DAE (défibrillateur)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:15"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text("Appeler le 15 (SAMU)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                step = CardiacStep.CPR_ACTIVE
                                toggleCpr()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text("Démarrer le massage cardiaque →", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            CardiacStep.CPR_ACTIVE -> {
                // Écran de massage cardiaque en direct avec métronome
                CprActiveScreen(
                    profile = profile,
                    metronome = metronome,
                    speechEngine = speechEngine,
                    isContinuousMode = isContinuousMode,
                    isRunning = isRunning,
                    compressionCount = compressionCount,
                    isBreathingPhase = isBreathingPhase,
                    elapsedSeconds = elapsedSeconds,
                    onToggleCpr = { toggleCpr() },
                    onToggleMode = { isContinuousMode = !isContinuousMode },
                    onOpenDaeGuide = { showDaeDialog = true },
                )
            }
        }

        if (showDaeDialog) {
            DaeGuideDialog(onDismiss = { showDaeDialog = false })
        }
    }
}

@Composable
private fun CprActiveScreen(
    profile: VictimProfile,
    metronome: HapticMetronome,
    speechEngine: SpeechEngine,
    isContinuousMode: Boolean,
    isRunning: Boolean,
    compressionCount: Int,
    isBreathingPhase: Boolean,
    elapsedSeconds: Long,
    onToggleCpr: () -> Unit,
    onToggleMode: () -> Unit,
    onOpenDaeGuide: () -> Unit,
) {
    val mins = elapsedSeconds / 60
    val secs = elapsedSeconds % 60
    val timerStr = String.format("%02d:%02d", mins, secs)

    // Pulse animation pour le disque de compression
    val scale = remember { Animatable(1f) }
    LaunchedEffect(compressionCount) {
        if (isRunning && !isBreathingPhase) {
            scale.animateTo(1.15f, tween(80, easing = LinearEasing))
            scale.animateTo(1.0f, tween(150, easing = LinearEasing))
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(2.dp, if (isRunning) EmergencyRed else MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Bandeau d'état et minuteur
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isBreathingPhase) "INSUFFLATIONS" else "110 BPM",
                    fontWeight = FontWeight.Bold,
                    color = if (isBreathingPhase) EmergencyOrange else EmergencyRed,
                    fontSize = 18.sp,
                )
                Text(
                    text = "Durée : $timerStr",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Disque visuel de battement au centre
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(scale.value)
                    .background(
                        color = if (isBreathingPhase) EmergencyOrange else if (isRunning) EmergencyRed else MaterialTheme.colorScheme.surface,
                        shape = CircleShape,
                    )
                    .border(4.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isBreathingPhase) {
                        Text("2", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("Insufflations", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text(
                            text = if (isContinuousMode) compressionCount.toString() else "${(compressionCount - 1) % 30 + 1}",
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isRunning) Color.White else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 64.sp,
                        )
                        Text(
                            text = if (!isContinuousMode) "sur 30" else "compressions",
                            fontSize = 13.sp,
                            color = if (isRunning) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Consignes de posture selon le profil
            val postureText = when (profile) {
                VictimProfile.ADULT -> "Bras tendus verticaux • Enfoncer de 5 à 6 cm • Relâcher complètement"
                VictimProfile.CHILD -> "1 seule main au centre • Enfoncer de 4 à 5 cm • Relâcher"
                VictimProfile.INFANT -> "2 doigts au centre • Enfoncer de 3 à 4 cm • Rythme doux"
            }
            Text(
                postureText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton principal Play / Pause
            Button(
                onClick = onToggleCpr,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.surface else EmergencyRed,
                    contentColor = if (isRunning) MaterialTheme.colorScheme.onSurface else Color.White,
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                border = if (isRunning) BorderStroke(2.dp, EmergencyRed) else null,
            ) {
                Text(
                    text = if (isRunning) "Mettre en pause" else "Lancer le métronome (110 bpm)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Raccourci DAE et bascule de mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onOpenDaeGuide,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("DAE disponible", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmergencyGreen)
                }
                OutlinedButton(
                    onClick = onToggleMode,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        if (isContinuousMode) "Mode : Continu" else "Mode : 30 : 2",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyStepCard(
    title: String,
    instruction: String,
    hint: String? = null,
    primaryActionLabel: String,
    primaryActionColor: Color,
    onPrimaryAction: () -> Unit,
    secondaryActionLabel: String,
    onSecondaryAction: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                instruction,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )
            hint?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = EmergencyOrange,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onPrimaryAction,
                colors = ButtonDefaults.buttonColors(containerColor = primaryActionColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
            ) {
                Text(primaryActionLabel, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onSecondaryAction,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(secondaryActionLabel, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DaeGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Consignes Défibrillateur (DAE)", fontWeight = FontWeight.Bold, color = EmergencyGreen)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("1. Allumez immédiatement l'appareil (bouton vert ou ouverture du capot).")
                Text("2. Écoutez ses consignes vocales : collez les électrodes sur la poitrine nue sèche selon les dessins.")
                Text("3. Pendant l'analyse et la délivrance du choc : NE TOUCHEZ PAS la victime.")
                Text("4. Reprenez le massage cardiaque immédiatement après le choc si le DAE le demande.")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen)) {
                Text("J'ai compris")
            }
        },
    )
}
