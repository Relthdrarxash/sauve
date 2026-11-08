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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import fr.sauve.app.engine.SpeechEngine
import fr.sauve.app.ui.theme.EmergencyCrimson
import fr.sauve.app.ui.theme.EmergencyGreen
import fr.sauve.app.ui.theme.EmergencyOrange
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BleedingScreen(
    speechEngine: SpeechEngine,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var isTimerActive by remember { mutableStateOf(true) }
    var compressionSeconds by rememberSaveable { mutableLongStateOf(0L) }
    var showForeignBodyDialog by remember { mutableStateOf(false) }
    var showTourniquetDialog by remember { mutableStateOf(false) }
    var tourniquetTime by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(isTimerActive) {
        speechEngine.speak(tr("Appuyez immédiatement très fort sur la plaie avec la paume. Ne relâchez jamais.", "Press very hard on the wound with your palm right now. Never let go."))
        while (isTimerActive) {
            delay(1000L)
            compressionSeconds++
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechEngine.stop()
        }
    }

    val mins = compressionSeconds / 60
    val secs = compressionSeconds % 60
    val timerStr = String.format("%02d:%02d", mins, secs)

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
            Text(tr("Hémorragie grave", "Severe bleeding"), color = EmergencyCrimson, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(2.dp, EmergencyCrimson),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    tr("COMPRESSION DIRECTE", "DIRECT PRESSURE"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = EmergencyCrimson,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    tr("Appuyez très fort avec la paume de la main ou un tissu propre directement sur la plaie.", "Press very hard with the palm of your hand or a clean cloth directly on the wound."),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    tr("Important : Ne relâchez jamais la pression, même pour regarder si le saignement a diminué.", "Important: never release the pressure, not even to see whether the bleeding has slowed."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmergencyOrange,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Chronomètre de compression
                Text(
                    tr("Temps de maintien :", "Time held:"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    timerStr,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                tourniquetTime?.let { time ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        tr("Garrot posé à : $time", "Tourniquet applied at: $time"),
                        fontWeight = FontWeight.Bold,
                        color = EmergencyOrange,
                        fontSize = 15.sp,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bouton Appel SAMU
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:15"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyCrimson),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                ) {
                    Text(tr("Appeler le 15 (SAMU en haut-parleur)", "Call 15 (Medical, on speaker)"), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Consignes réflexes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(tr("1. Allongez la victime pour éviter le malaise de choc.", "1. Lay the person down to prevent fainting from shock."), style = MaterialTheme.typography.bodyMedium)
                    Text(tr("2. Couvrez-la pour prévenir l'hypothermie.", "2. Cover them to prevent hypothermia."), style = MaterialTheme.typography.bodyMedium)
                    Text(tr("3. Ne lui donnez RIEN à boire ni à manger.", "3. Give them NOTHING to eat or drink."), style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Boutons cas particuliers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = { showForeignBodyDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(tr("Objet planté", "Object in wound"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = { showTourniquetDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(tr("Poser un garrot", "Apply a tourniquet"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (showForeignBodyDialog) {
            AlertDialog(
                onDismissRequest = { showForeignBodyDialog = false },
                title = { Text(tr("Corps étranger dans la plaie", "Object stuck in the wound"), fontWeight = FontWeight.Bold, color = EmergencyOrange) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(tr("• NE RETIREZ JAMAIS l'objet (couteau, éclat de verre, morceau de métal). Il fait actuellement office de bouchon hémostatique.", "• NEVER REMOVE the object (knife, glass, metal). It is currently plugging the wound."))
                        Text(tr("• Comprimez fermement avec vos mains de part et d'autre de l'objet sans appuyer dessus.", "• Press firmly with your hands on both sides of the object without pressing on it."))
                        Text(tr("• Alertez le 15 immédiatement.", "• Call 15 immediately."))
                    }
                },
                confirmButton = {
                    Button(onClick = { showForeignBodyDialog = false }) {
                        Text(tr("Bien compris", "Understood"))
                    }
                },
            )
        }

        if (showTourniquetDialog) {
            AlertDialog(
                onDismissRequest = { showTourniquetDialog = false },
                title = { Text(tr("Poser un garrot d'urgence", "Emergency tourniquet"), fontWeight = FontWeight.Bold, color = EmergencyCrimson) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(tr("Le garrot est réservé aux cas extrêmes (membre arraché, blessés multiples impossibles à comprimer manuellement).", "A tourniquet is only for extreme cases (severed limb, several casualties that cannot be compressed by hand)."))
                        Text(tr("1. Placer un lien large (ceinture, écharpe) au-dessus de la plaie, entre la blessure et le cœur.", "1. Place a wide strap (belt, scarf) above the wound, between the injury and the heart."))
                        Text(tr("2. Serrer très fort jusqu'à arrêt complet du saignement.", "2. Tighten very hard until the bleeding stops completely."))
                        Text(tr("3. NE JAMAIS LE DESSERRER.", "3. NEVER LOOSEN IT."))
                        Text(tr("4. Noter impérativement l'heure de pose.", "4. Always note the time it was applied."))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                            tourniquetTime = now
                            speechEngine.speak(tr("Garrot noté à $now. Ne le desserrez sous aucun prétexte.", "Tourniquet noted at $now. Do not loosen it under any circumstances."))
                            showTourniquetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyCrimson),
                    ) {
                        Text(tr("Enregistrer l'heure actuelle", "Save the current time"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTourniquetDialog = false }) {
                        Text(tr("Annuler", "Cancel"))
                    }
                },
            )
        }
    }
}
