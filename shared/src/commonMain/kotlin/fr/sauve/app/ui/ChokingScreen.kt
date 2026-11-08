package fr.sauve.app.ui

import fr.sauve.app.i18n.tr
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sauve.app.data.VictimProfile
import fr.sauve.app.platform.Metronome
import fr.sauve.app.platform.Speech
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
    metronome: Metronome,
    speechEngine: Speech,
    onBack: () -> Unit,
    onVictimUnconscious: () -> Unit,
) {
    var profile by rememberSaveable { mutableStateOf(VictimProfile.ADULT) }
    var phase by rememberSaveable { mutableStateOf(ChokingPhase.DIAGNOSIS) }
    var currentActionCount by rememberSaveable { mutableIntStateOf(1) }

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
                Text(tr("← Accueil", "← Home"), color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
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
                Text(tr("La victime s'effondre / Perte de connaissance", "The person collapses / loses consciousness"), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (phase) {
            ChokingPhase.DIAGNOSIS -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak(tr("La victime peut-elle parler ou tousser ?", "Can the person speak or cough?"))
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
                            tr("Diagnostic Immédiat", "Immediate check"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyOrange,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            tr("La victime peut-elle parler, crier ou tousser fort ?", "Can the person speak, shout or cough strongly?"),
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
                            Text(tr("NON (Aucun son, panique)", "NO (no sound, panic)"), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { phase = ChokingPhase.PARTIAL_OBSTRUCTION },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        ) {
                            Text(tr("OUI (Elle tousse ou parle)", "YES (coughing or speaking)"), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            ChokingPhase.PARTIAL_OBSTRUCTION -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak(tr("Ne tapez pas dans le dos ! Encouragez la victime à continuer de tousser.", "Do not hit their back! Encourage them to keep coughing."))
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
                            tr("Obstruction Partielle", "Partial obstruction"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyGreen,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            tr("NE DONNEZ PAS DE CLAQUES DANS LE DOS.\n\nTaper dans le dos d'une personne qui tousse risque d'enfoncer le corps étranger et d'obstruer totalement ses poumons.", "DO NOT GIVE BACK BLOWS.\n\nHitting the back of someone who is coughing may push the object further in and block their airway completely."),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            tr("Consigne : « Toussez fort ! Ne vous retenez pas ! »", "Say: “Cough hard! Don't hold back!”"),
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
                            Text(tr("L'objet est sorti / Respiration rétablie", "The object is out / breathing again"), fontWeight = FontWeight.Bold)
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
                            Text(tr("La situation s'aggrave (aucun son)", "It is getting worse (no sound)"))
                        }
                    }
                }
            }

            ChokingPhase.BACK_BLOWS -> {
                LaunchedEffect(currentActionCount) {
                    speechEngine.speak(tr("Tape $currentActionCount dans le dos", "Back blow $currentActionCount"))
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
                            tr("Étape 1 : 5 Claques dans le dos", "Step 1: 5 back blows"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyOrange,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (profile == VictimProfile.INFANT) {
                            GestureIllustration(Gestures.ChokingInfant, tr("Schéma : nourrisson à plat ventre le long de l'avant-bras du sauveteur, tête plus basse que le corps, mâchoire soutenue par la main, tapes entre les omoplates.", "Diagram: baby face down along the rescuer's forearm, head lower than the body, jaw supported by the hand, back blows between the shoulder blades."))
                        } else {
                            GestureIllustration(Gestures.ChokingBackBlows, tr("Schéma : personne penchée en avant, soutenue au niveau de la poitrine par une main, l'autre main frappe entre les omoplates, flèche vers le bas.", "Diagram: person leaning forward, supported at the chest by one hand while the other hand strikes between the shoulder blades, arrow pointing down."))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val instruction = when (profile) {
                            VictimProfile.ADULT, VictimProfile.CHILD ->
                                tr("Penchez la victime en avant. Donnez une claque sèche avec le plat de la main entre les deux omoplates.", "Lean the person forward. Give a sharp blow with the flat of your hand between the shoulder blades.")
                            VictimProfile.INFANT ->
                                tr("Placez le bébé à plat ventre sur votre avant-bras penché vers le bas. Donnez 5 tapes douces mais fermes entre ses omoplates.", "Lay the baby face down along your forearm, tilted downwards. Give 5 gentle but firm blows between the shoulder blades.")
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
                                if (currentActionCount < 5) tr("Claque donnée suivante (${currentActionCount + 1})", "Blow given, next (${currentActionCount + 1})") else tr("Passer aux compressions →", "Go to thrusts →"),
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
                            Text(tr("L'objet est expulsé !", "The object is out!"), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            ChokingPhase.ABDOMINAL_THRUSTS -> {
                val thrustName = if (profile == VictimProfile.INFANT) tr("5 Compressions thoraciques (2 doigts)", "5 chest thrusts (2 fingers)") else tr("5 Compressions Abdominales (Heimlich)", "5 abdominal thrusts (Heimlich)")
                LaunchedEffect(currentActionCount) {
                    speechEngine.speak(tr("Compression $currentActionCount", "Thrust $currentActionCount"))
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
                            tr("Étape 2 : $thrustName", "Step 2: $thrustName"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyOrange,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (profile != VictimProfile.INFANT) {
                            GestureIllustration(Gestures.ChokingHeimlich, tr("Schéma : sauveteur derrière la victime debout, poing entre le nombril et le bas du sternum, l'autre main par-dessus, flèche vers l'intérieur et vers le haut.", "Diagram: rescuer standing behind the person, fist between the navel and the lower breastbone, other hand on top, arrow pointing inwards and upwards."))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        val instruction = when (profile) {
                            VictimProfile.ADULT, VictimProfile.CHILD ->
                                tr("Placez votre poing au creux de l'estomac (au-dessus du nombril). Tirez d'un coup sec vers l'arrière et vers le haut.", "Place your fist just above the navel, below the ribs. Pull sharply inwards and upwards.")
                            VictimProfile.INFANT ->
                                tr("Retournez le bébé sur le dos le long de votre avant-bras. Effectuez 5 compressions thoraciques avec 2 doigts sur le sternum (JAMAIS d'appui au ventre).", "Turn the baby onto their back along your forearm. Give 5 chest thrusts with 2 fingers on the breastbone (NEVER press on the belly).")
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
                                if (currentActionCount < 5) tr("Compression suivante (${currentActionCount + 1})", "Next thrust (${currentActionCount + 1})") else tr("Revenir aux 5 claques dans le dos →", "Back to 5 back blows →"),
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
                            Text(tr("L'objet est expulsé !", "The object is out!"), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            ChokingPhase.SUCCESS_RESOLVED -> {
                LaunchedEffect(Unit) {
                    speechEngine.speak(tr("Bravo. Laissez la victime se reposer et surveillez sa respiration.", "Well done. Let the person rest and keep watching their breathing."))
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
                            tr("Voies Aériennes Libérées", "Airway cleared"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyGreen,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            tr("La victime respire à nouveau. Gardez-la au repos complet et au calme.", "The person is breathing again. Keep them completely at rest and calm."),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            tr("Important : si des compressions abdominales ont été effectuées, faites consulter un médecin par précaution.", "Important: if abdominal thrusts were given, have them see a doctor as a precaution."),
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
                            Text(tr("Retour à l'accueil", "Back to home"), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
