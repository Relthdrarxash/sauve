package fr.sauve.app.ui

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

enum class ChokingPhase {
    DIAGNOSIS,
    PARTIAL_OBSTRUCTION,
    BACK_BLOWS,
    ABDOMINAL_THRUSTS,
    SUCCESS_RESOLVED,
}

@Composable
fun ChokingScreen(
    metronome: HapticMetronome,
    speechEngine: SpeechEngine,
    onBack: () -> Unit,
    onVictimUnconscious: () -> Unit,
) {
    var profile by remember { mutableStateOf(VictimProfile.ADULT) }
    var phase by remember { mutableStateOf(ChokingPhase.DIAGNOSIS) }
    var currentActionCount by remember { mutableIntStateOf(1) }

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
        // En-tête retour et profil
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

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                VictimProfile.entries.forEach { p ->
                    FilterChip(
                        selected = profile == p,
                        onClick = { profile = p },
                        label = { Text(p.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmergencyOrange,
                            selectedLabelColor = Color.White,
                        ),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Alerte critique permanente : la victime perd connaissance
        if (phase != ChokingPhase.DIAGNOSIS && phase != ChokingPhase.PARTIAL_OBSTRUCTION && phase != ChokingPhase.SUCCESS_RESOLVED) {
            Button(
                onClick = onVictimUnconscious,
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("🚨 La victime s'effondre / Perte de connaissance", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (phase) {
            ChokingPhase.DIAGNOSIS -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak("La victime peut-elle parler ou tousser ?")
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Diagnostic Immédiat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyOrange,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "La victime peut-elle parler, crier ou tousser fort ?",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                phase = ChokingPhase.BACK_BLOWS
                                currentActionCount = 1
                                metronome.pulse()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        ) {
                            Text("NON (Aucun son, panique)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { phase = ChokingPhase.PARTIAL_OBSTRUCTION },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        ) {
                            Text("OUI (Elle tousse ou parle)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            ChokingPhase.PARTIAL_OBSTRUCTION -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak("Ne tapez pas dans le dos ! Encouragez la victime à continuer de tousser.")
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(2.dp, EmergencyGreen),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "⚠️ Obstruction Partielle",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyGreen,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "NE DONNEZ PAS DE CLAQUES DANS LE DOS.\n\nTaper dans le dos d'une personne qui tousse risque d'enfoncer le corps étranger et d'obstruer totalement ses poumons.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "👉 Dites-lui fermement : « Toussez fort ! Ne vous retenez pas ! »",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyGreen,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { phase = ChokingPhase.SUCCESS_RESOLVED },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        ) {
                            Text("L'objet est sorti / Respiration rétablie", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                phase = ChokingPhase.BACK_BLOWS
                                currentActionCount = 1
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("La situation s'aggrave (aucun son)")
                        }
                    }
                }
            }

            ChokingPhase.BACK_BLOWS -> {
                LaunchedEffect(currentActionCount) {
                    speechEngine.speak("Tape $currentActionCount dans le dos")
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(2.dp, EmergencyOrange),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Étape 1 : 5 Claques dans le dos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyOrange,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val instruction = when (profile) {
                            VictimProfile.ADULT, VictimProfile.CHILD ->
                                "Penchez la victime en avant. Donnez une claque sèche avec le plat de la main entre les deux omoplates."
                            VictimProfile.INFANT ->
                                "Placez le bébé à plat ventre sur votre avant-bras penché vers le bas. Donnez 5 tapes douces mais fermes entre ses omoplates."
                        }
                        Text(
                            instruction,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Compteur de claques
                        Text(
                            "$currentActionCount / 5",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = EmergencyOrange,
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                metronome.pulse()
                                if (currentActionCount < 5) {
                                    currentActionCount++
                                } else {
                                    currentActionCount = 1
                                    phase = ChokingPhase.ABDOMINAL_THRUSTS
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        ) {
                            Text(
                                if (currentActionCount < 5) "Claque donnée suivante (${currentActionCount + 1})" else "Passer aux compressions →",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { phase = ChokingPhase.SUCCESS_RESOLVED },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        ) {
                            Text("✓ L'objet est expulsé !", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            ChokingPhase.ABDOMINAL_THRUSTS -> {
                val thrustName = if (profile == VictimProfile.INFANT) "5 Compressions thoraciques (2 doigts)" else "5 Compressions Abdominales (Heimlich)"
                LaunchedEffect(currentActionCount) {
                    speechEngine.speak("Compression $currentActionCount")
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(2.dp, EmergencyOrange),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Étape 2 : $thrustName",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyOrange,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val instruction = when (profile) {
                            VictimProfile.ADULT, VictimProfile.CHILD ->
                                "Placez votre poing au creux de l'estomac (au-dessus du nombril). Tirez d'un coup sec vers l'arrière et vers le haut."
                            VictimProfile.INFANT ->
                                "Retournez le bébé sur le dos le long de votre avant-bras. Effectuez 5 compressions thoraciques avec 2 doigts sur le sternum (JAMAIS d'appui au ventre)."
                        }
                        Text(
                            instruction,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            "$currentActionCount / 5",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = EmergencyOrange,
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                metronome.pulse()
                                if (currentActionCount < 5) {
                                    currentActionCount++
                                } else {
                                    currentActionCount = 1
                                    phase = ChokingPhase.BACK_BLOWS // Cycle alternatif
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        ) {
                            Text(
                                if (currentActionCount < 5) "Compression suivante (${currentActionCount + 1})" else "Revenir aux 5 claques dans le dos →",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { phase = ChokingPhase.SUCCESS_RESOLVED },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        ) {
                            Text("✓ L'objet est expulsé !", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            ChokingPhase.SUCCESS_RESOLVED -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak("Bravo. Laissez la victime se reposer et surveillez sa respiration.")
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(2.dp, EmergencyGreen),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "✅ Voies Aériennes Libérées",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyGreen,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "La victime respire à nouveau. Gardez-la au repos complet et au calme.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Important : si des compressions abdominales ont été effectuées, faites consulter un médecin par précaution.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        ) {
                            Text("Retour à l'accueil", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
