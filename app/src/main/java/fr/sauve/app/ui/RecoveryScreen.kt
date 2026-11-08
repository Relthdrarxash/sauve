package fr.sauve.app.ui

import fr.sauve.app.i18n.tr
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
import androidx.compose.runtime.saveable.rememberSaveable
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
    var topic by rememberSaveable { mutableStateOf(RecoverySubTopic.PLS) }

    // Minuteur brûlure (15 minutes = 900 secondes)
    var burnSecondsRemaining by rememberSaveable { mutableIntStateOf(900) }
    var isBurnTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isBurnTimerRunning, burnSecondsRemaining) {
        if (isBurnTimerRunning && burnSecondsRemaining > 0) {
            delay(1000L)
            burnSecondsRemaining--
            if (burnSecondsRemaining == 0) {
                isBurnTimerRunning = false
                metronome.pulse()
                speechEngine.speak(tr("Quinze minutes écoulées. Évaluez la douleur et protégez la brûlure avec un linge propre.", "Fifteen minutes gone. Check the pain and protect the burn with a clean cloth."))
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
                Text(tr("← Accueil", "← Home"), color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            }
            Text(tr("Protéger & Surveiller", "Protect & monitor"), color = EmergencyBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                    speechEngine.speak(tr("Position Latérale de Sécurité. La victime est inconsciente mais respire.", "Recovery position. The person is unconscious but breathing."))
                },
                label = { Text(tr("PLS (Inconscient)", "Recovery position (unconscious)"), fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmergencyBlue, selectedLabelColor = Color.White),
            )
            FilterChip(
                selected = topic == RecoverySubTopic.BURNS,
                onClick = {
                    topic = RecoverySubTopic.BURNS
                    speechEngine.speak(tr("Refroidissez la brûlure sous l'eau tempérée pendant quinze minutes.", "Cool the burn under lukewarm water for fifteen minutes."))
                },
                label = { Text(tr("Brûlures (15 min)", "Burns (15 min)"), fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmergencyBlue, selectedLabelColor = Color.White),
            )
            FilterChip(
                selected = topic == RecoverySubTopic.STROKE_FAST,
                onClick = {
                    topic = RecoverySubTopic.STROKE_FAST
                    speechEngine.speak(tr("Test rapide de l'AVC : Visage, Bras, Parole, Temps.", "Quick stroke test: Face, Arms, Speech, Time."))
                },
                label = { Text(tr("Malaise / AVC", "Feeling unwell / stroke"), fontSize = 12.sp) },
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
                            tr("Position Latérale de Sécurité (PLS)", "Recovery position"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyBlue,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            tr("Pour toute personne inconsciente qui respire normalement. Évite l'étouffement par la langue ou les vomissements.", "For anyone unconscious who is breathing normally. Prevents choking on the tongue or on vomit."),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Étapes claires
                        PlsStepItem(tr("1. Bras en angle droit", "1. Arm at a right angle"), tr("Placez le bras le plus proche de vous à angle droit (90°) paume vers le haut.", "Place the arm nearest to you at a right angle (90°), palm up."))
                        PlsStepItem(tr("2. Main contre la joue", "2. Hand against the cheek"), tr("Amenez le dos de l'autre main contre l'oreille et la joue opposée de la victime.", "Bring the back of the other hand against the person's opposite ear and cheek."))
                        PlsStepItem(tr("3. Pivoter le genou", "3. Roll at the knee"), tr("Saisissez la jambe opposée derrière le genou et tirez doucement pour faire rouler la victime vers vous.", "Take the far leg behind the knee and pull gently to roll the person towards you."))
                        PlsStepItem(tr("4. Ouvrir la bouche", "4. Open the mouth"), tr("Ouvrez la bouche vers le sol et vérifiez la respiration en continu.", "Open the mouth facing the ground and keep checking their breathing."))

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
                            Text(tr("Alerter le 15 (SAMU)", "Call 15 (Medical)"), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                            tr("Règle des 15 : Refroidir à l'eau", "Rule of 15: cool with water"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyBlue,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            tr("Faites couler de l'eau tempérée (15 à 20°C) à 15 cm de distance sur la brûlure.", "Run lukewarm water (15 to 20°C) on the burn from 15 cm away."),
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
                            Text(if (isBurnTimerRunning) tr("Mettre en pause", "Pause") else tr("Démarrer les 15 minutes", "Start the 15 minutes"), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(tr("• Ne jamais mettre de glaçons (risque de gelure).", "• Never use ice (risk of frostbite)."), color = EmergencyOrange, fontWeight = FontWeight.Bold)
                            Text(tr("• Ne mettre ni beurre, ni huile, ni dentifrice.", "• No butter, oil or toothpaste."), color = EmergencyOrange, fontWeight = FontWeight.Bold)
                            Text(tr("• Ne percez jamais les cloques.", "• Never burst blisters."), color = EmergencyOrange, fontWeight = FontWeight.Bold)
                            Text(tr("• Retirez bagues et montres avant que la zone ne gonfle.", "• Remove rings and watches before the area swells."))
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
                            tr("Signes d'un AVC (Méthode FAST)", "Signs of a stroke (FAST)"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyBlue,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        PlsStepItem(tr("F (Visage / Face)", "F (Face)"), tr("Demandez de sourire : un côté de la bouche retombe-t-il ?", "Ask them to smile: does one side of the mouth droop?"))
                        PlsStepItem(tr("A (Bras / Arms)", "A (Arms)"), tr("Demandez de lever les deux bras : l'un retombe-t-il immédiatement ?", "Ask them to raise both arms: does one drop straight away?"))
                        PlsStepItem(tr("S (Parole / Speech)", "S (Speech)"), tr("Faites répéter une phrase simple : la parole est-elle confuse ou pâteuse ?", "Ask them to repeat a simple sentence: is their speech confused or slurred?"))
                        PlsStepItem(tr("T (Temps / Time)", "T (Time)"), tr("Si un seul signe est présent, notez l'heure exacte et appelez le 15 immédiatement.", "If even one sign is present, note the exact time and call 15 immediately."))

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
                            Text(tr("Appeler le 15 (SAMU)", "Call 15 (Medical)"), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
