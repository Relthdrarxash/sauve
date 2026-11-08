package fr.sauve.app.ui

import fr.sauve.app.i18n.tr
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sauve.app.ui.theme.EmergencyGreen
import fr.sauve.app.ui.theme.EmergencyRed

@Composable
fun AboutScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun openPlayStore(pkg: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$pkg"))
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(webIntent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) {
                Text(tr("← Accueil", "← Home"), color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            }
            Text(tr("À propos", "About"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    "Sauve",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = EmergencyRed,
                )
                Text(
                    tr("Premiers secours anti-panique • Version 1.0.0", "Anti-panic first aid • Version 1.0.0"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    tr("Conçue pour guider les gestes de secours sans panique ni hésitation. Fonctionne 100 % hors-ligne, sans inscription, sans publicité, et ne collecte strictement aucune donnée personnelle.", "Designed to guide first aid without panic or hesitation. Works 100% offline, with no sign-up and no ads, and collects no personal data at all."),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(tr("• Aucune permission INTERNET (étanchéité absolue)", "• No INTERNET permission (fully sealed)"), fontWeight = FontWeight.Bold, color = EmergencyGreen, fontSize = 13.sp)
                    Text(tr("• Démarrage immédiat sans compte ni écran de veille", "• Starts instantly, no account, no splash screen"), fontSize = 13.sp)
                    Text(tr("• Métronome haptique haute précision à 110 bpm", "• High-precision haptic metronome at 110 bpm"), fontSize = 13.sp)
                    Text(tr("• Synthèse vocale pas-à-pas mains-libres", "• Hands-free, step-by-step voice guidance"), fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Suite Pilmil Studio
        Text(
            tr("La suite Pilmil Studio", "The Pilmil Studio suite"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            tr("Des biens communs numériques éthiques, libres et respectueux.", "Ethical, free and respectful digital commons."),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        CompanionAppCard(
            title = "Respire",
            description = tr("Déconnexion sereine & micro-pauses sans tracking", "Calm disconnection & micro-breaks, no tracking"),
            pkg = "fr.respire.app",
            onOpen = { openPlayStore("fr.respire.app") },
        )

        CompanionAppCard(
            title = "Lunaison",
            description = tr("Suivi de cycle intime 100 % confidentiel & hors-ligne", "100% private, offline cycle tracking"),
            pkg = "fr.lunaison",
            onOpen = { openPlayStore("fr.lunaison") },
        )

        CompanionAppCard(
            title = "Sommelle",
            description = tr("Journal du sommeil sans pub ni compte", "Sleep diary with no ads or account"),
            pkg = "fr.sommelle",
            onOpen = { openPlayStore("fr.sommelle") },
        )
    }
}

@Composable
private fun CompanionAppCard(
    title: String,
    description: String,
    pkg: String,
    onOpen: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(
                onClick = onOpen,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(tr("Voir", "View"), fontSize = 12.sp)
            }
        }
    }
}
