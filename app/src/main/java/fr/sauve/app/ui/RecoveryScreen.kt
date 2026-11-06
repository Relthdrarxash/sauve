package fr.sauve.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sauve.app.engine.HapticMetronome
import fr.sauve.app.engine.SpeechEngine
import fr.sauve.app.ui.theme.EmergencyBlue
import fr.sauve.app.ui.theme.EmergencyGreen
import fr.sauve.app.ui.theme.EmergencyOrange
import fr.sauve.app.ui.theme.EmergencyRed
import kotlinx.coroutines.delay

enum class RecoverySubTopic {
    PLS,
    BURNS,
    STROKE_FAST,
}

@Composable
fun RecoveryScreen(
    metronome: HapticMetronome,
    speechEngine: SpeechEngine,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var topic by remember { mutableStateOf(RecoverySubTopic.PLS) }

    // Minuteur brûlure (15 minutes = 900 secondes)
    var burnSecondsRemaining by remember { mutableIntStateOf(900) }
    var isBurnTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isBurnTimerRunning, burnSecondsRemaining) {
        if (isBurnTimerRunning && burnSecondsRemaining > 0) {
            delay(1000L)
            burnSecondsRemaining--
            if (burnSecondsRemaining == 0) {
                isBurnTimerRunning = false
                metronome.pulse()
                speechEngine.speak("Quinze minutes écoulées. Évaluez la douleur et protégez la brûlure avec un linge propre.")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechEngine.stop()
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = {
                speechEngine.stop()
                onBack()
            }) {
                Text("← Accueil", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            }
            Text("Protéger & Surveiller", color = EmergencyBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Onglets du module
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FilterChip(
                selected = topic == RecoverySubTopic.PLS,
                onClick = {
                    topic = RecoverySubTopic.PLS
                    speechEngine.speak("Position Latérale de Sécurité. La victime est inconsciente mais respire.")
                },
                label = { Text("PLS (Inconscient)", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmergencyBlue, selectedLabelColor = Color.White),
            )
            FilterChip(
                selected = topic == RecoverySubTopic.BURNS,
                onClick = {
                    topic = RecoverySubTopic.BURNS
                    speechEngine.speak("Refroidissez la brûlure sous l'eau tempérée pendant quinze minutes.")
                },
                label = { Text("Brûlures (15 min)", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmergencyBlue, selectedLabelColor = Color.White),
            )
            FilterChip(
                selected = topic == RecoverySubTopic.STROKE_FAST,
                onClick = {
                    topic = RecoverySubTopic.STROKE_FAST
                    speechEngine.speak("Test rapide de l'AVC : Visage, Bras, Parole, Temps.")
                },
                label = { Text("Malaise / AVC", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmergencyBlue, selectedLabelColor = Color.White),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (topic) {
            RecoverySubTopic.PLS -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(2.dp, EmergencyBlue),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Position Latérale de Sécurité (PLS)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyBlue,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "Pour toute personne inconsciente qui respire normalement. Évite l'étouffement par la langue ou les vomissements.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Étapes claires
                        PlsStepItem("1. Bras en angle droit", "Placez le bras le plus proche de vous à angle droit (90°) paume vers le haut.")
                        PlsStepItem("2. Main contre la joue", "Amenez le dos de l'autre main contre l'oreille et la joue opposée de la victime.")
                        PlsStepItem("3. Pivoter le genou", "Saisissez la jambe opposée derrière le genou et tirez doucement pour faire rouler la victime vers vous.")
                        PlsStepItem("4. Ouvrir la bouche", "Ouvrez la bouche vers le sol et vérifiez la respiration en continu.")

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:15"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                        ) {
                            Text("Alerter le 15 (SAMU)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            RecoverySubTopic.BURNS -> {
                val mins = burnSecondsRemaining / 60
                val secs = burnSecondsRemaining % 60
                val timerStr = String.format("%02d:%02d", mins, secs)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(2.dp, EmergencyBlue),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Règle des 15 : Refroidir à l'eau",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyBlue,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Faites couler de l'eau tempérée (15 à 20°C) à 15 cm de distance sur la brûlure.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Compteur
                        Text(
                            timerStr,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isBurnTimerRunning) EmergencyBlue else MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { isBurnTimerRunning = !isBurnTimerRunning },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        ) {
                            Text(if (isBurnTimerRunning) "Mettre en pause" else "Démarrer les 15 minutes", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text("• Ne jamais mettre de glaçons (risque de gelure).", color = EmergencyOrange, fontWeight = FontWeight.Bold)
                            Text("• Ne mettre ni beurre, ni huile, ni dentifrice.", color = EmergencyOrange, fontWeight = FontWeight.Bold)
                            Text("• Ne percez jamais les cloques.", color = EmergencyOrange, fontWeight = FontWeight.Bold)
                            Text("• Retirez bagues et montres avant que la zone ne gonfle.")
                        }
                    }
                }
            }

            RecoverySubTopic.STROKE_FAST -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(2.dp, EmergencyBlue),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Signes d'un AVC (Méthode FAST)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyBlue,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        PlsStepItem("F (Visage / Face)", "Demandez de sourire : un côté de la bouche retombe-t-il ?")
                        PlsStepItem("A (Bras / Arms)", "Demandez de lever les deux bras : l'un retombe-t-il immédiatement ?")
                        PlsStepItem("S (Parole / Speech)", "Faites répéter une phrase simple : la parole est-elle confuse ou pâteuse ?")
                        PlsStepItem("T (Temps / Time)", "Si un seul signe est présent, notez l'heure exacte et appelez le 15 immédiatement.")

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:15"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        ) {
                            Text("Appeler le 15 (SAMU)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlsStepItem(title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .padding(12.dp),
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = EmergencyBlue, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
