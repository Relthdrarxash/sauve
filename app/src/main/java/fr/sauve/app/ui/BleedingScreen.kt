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
    var compressionSeconds by remember { mutableLongStateOf(0L) }
    var showForeignBodyDialog by remember { mutableStateOf(false) }
    var showTourniquetDialog by remember { mutableStateOf(false) }
    var tourniquetTime by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isTimerActive) {
        speechEngine.speak("Appuyez immédiatement très fort sur la plaie avec la paume. Ne relâchez jamais.")
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
                Text("← Accueil", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            }
            Text("Hémorragie grave", color = EmergencyCrimson, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                    "🩸 COMPRESSION DIRECTE",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = EmergencyCrimson,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Appuyez très fort avec la paume de la main ou un tissu propre directement sur la plaie.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "⚠️ NE RELÂCHEZ JAMAIS LA PRESSION, même pour regarder si le saignement a diminué.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmergencyOrange,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Chronomètre de compression
                Text(
                    "Temps de maintien :",
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
                        "⏰ Garrot posé à : $time",
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
                    Text("📞 Appeler le 15 (SAMU en haut-parleur)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    Text("1. Allongez la victime pour éviter le malaise de choc.", style = MaterialTheme.typography.bodyMedium)
                    Text("2. Couvrez-la pour prévenir l'hypothermie.", style = MaterialTheme.typography.bodyMedium)
                    Text("3. Ne lui donnez RIEN à boire ni à manger.", style = MaterialTheme.typography.bodyMedium)
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
                        Text("Objet planté", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = { showTourniquetDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text("Poser un garrot", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (showForeignBodyDialog) {
            AlertDialog(
                onDismissRequest = { showForeignBodyDialog = false },
                title = { Text("⚠️ Corps étranger dans la plaie", fontWeight = FontWeight.Bold, color = EmergencyOrange) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• NE RETIREZ JAMAIS l'objet (couteau, éclat de verre, morceau de métal). Il fait actuellement office de bouchon hémostatique.")
                        Text("• Comprimez fermement avec vos mains de part et d'autre de l'objet sans appuyer dessus.")
                        Text("• Alertez le 15 immédiatement.")
                    }
                },
                confirmButton = {
                    Button(onClick = { showForeignBodyDialog = false }) {
                        Text("Bien compris")
                    }
                },
            )
        }

        if (showTourniquetDialog) {
            AlertDialog(
                onDismissRequest = { showTourniquetDialog = false },
                title = { Text("Poser un garrot d'urgence", fontWeight = FontWeight.Bold, color = EmergencyCrimson) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Le garrot est réservé aux cas extrêmes (membre arraché, blessés multiples impossibles à comprimer manuellement).")
                        Text("1. Placer un lien large (ceinture, écharpe) au-dessus de la plaie, entre la blessure et le cœur.")
                        Text("2. Serrer très fort jusqu'à arrêt complet du saignement.")
                        Text("3. NE JAMAIS LE DESSERRER.")
                        Text("4. Noter impérativement l'heure de pose.")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                            tourniquetTime = now
                            speechEngine.speak("Garrot noté à $now. Ne le desserrez sous aucun prétexte.")
                            showTourniquetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyCrimson),
                    ) {
                        Text("Enregistrer l'heure actuelle")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTourniquetDialog = false }) {
                        Text("Annuler")
                    }
                },
            )
        }
    }
}
