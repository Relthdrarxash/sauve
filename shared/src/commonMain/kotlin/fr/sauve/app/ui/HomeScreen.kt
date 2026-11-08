package fr.sauve.app.ui

import fr.sauve.app.platform.LocalSauvePlatform
import fr.sauve.app.i18n.tr
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sauve.app.data.EmergencyCategory
import fr.sauve.app.ui.theme.EmergencyBlue
import fr.sauve.app.ui.theme.EmergencyCrimson
import fr.sauve.app.ui.theme.EmergencyOrange
import fr.sauve.app.ui.theme.EmergencyRed

@Composable
fun HomeScreen(
    onSelectCategory: (EmergencyCategory) -> Unit,
    onOpenGuide: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTraining: () -> Unit = {},
) {
    val platform = LocalSauvePlatform.current
    val reviewReminder = platform.reviewReminder
    var reviewOn by remember { mutableStateOf(reviewReminder?.isEnabled() == true) }

    fun callNumber(num: String) = platform.dial(num)

    fun sendSms(num: String) = platform.sms(num)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // En-tête : Titre & Numéros d'appel express
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Sauve",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = tr("Premiers secours anti-panique", "Anti-panic first aid"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Bouton discret révision PSC1
            OutlinedButton(
                onClick = onOpenGuide,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Text(tr("Fiches PSC1", "First aid sheets"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bandeau d'appels d'urgence directs
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    tr("APPELS D'URGENCE (1 TAP)", "EMERGENCY CALLS (1 TAP)"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    EmergencyCallButton(
                        label = tr("15 SAMU", "15 Medical"),
                        color = EmergencyRed,
                        modifier = Modifier.weight(1f),
                        onClick = { callNumber("15") },
                    )
                    EmergencyCallButton(
                        label = tr("18 Pompiers", "18 Fire"),
                        color = EmergencyOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { callNumber("18") },
                    )
                    EmergencyCallButton(
                        label = tr("112 Europe", "112 Europe"),
                        color = EmergencyBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { callNumber("112") },
                    )
                    EmergencyCallButton(
                        label = tr("114 SMS", "114 SMS"),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.weight(0.9f),
                        onClick = { sendSms("114") },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = tr("QUELLE EST L'URGENCE ?", "WHAT IS THE EMERGENCY?"),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, start = 4.dp),
        )

        // 4 GROS BOUTONS D'URGENCE VITALE (ANTI-PANIQUE)
        EmergencyHeroButton(
            category = EmergencyCategory.CARDIAC_ARREST,
            badge = "110 BPM",
            backgroundColor = EmergencyRed,
            onClick = { onSelectCategory(EmergencyCategory.CARDIAC_ARREST) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmergencyHeroButton(
            category = EmergencyCategory.CHOKING,
            badge = tr("5 TAPES / HEIMLICH", "5 BLOWS / HEIMLICH"),
            backgroundColor = EmergencyOrange,
            onClick = { onSelectCategory(EmergencyCategory.CHOKING) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmergencyHeroButton(
            category = EmergencyCategory.BLEEDING,
            badge = tr("COMPRESSION CONTINUE", "CONSTANT PRESSURE"),
            backgroundColor = EmergencyCrimson,
            onClick = { onSelectCategory(EmergencyCategory.BLEEDING) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmergencyHeroButton(
            category = EmergencyCategory.RECOVERY_AND_BURNS,
            badge = tr("PLS · 15 MIN EAU", "RECOVERY · 15 MIN WATER"),
            backgroundColor = EmergencyBlue,
            onClick = { onSelectCategory(EmergencyCategory.RECOVERY_AND_BURNS) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hors urgence : s'entraîner, pour que les gestes soient là le jour J.
        OutlinedButton(
            onClick = onOpenTraining,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
        ) {
            Text(tr("S'entraîner au massage cardiaque (2 min)", "Practise chest compressions (2 min)"), fontWeight = FontWeight.SemiBold)
        }
        if (reviewReminder != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .toggleable(value = reviewOn, role = Role.Switch) { on ->
                        reviewReminder.setEnabled(on) { reviewOn = it }
                    }
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    tr("Me proposer une révision tous les 3 mois", "Suggest a refresher every 3 months"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
                Switch(checked = reviewOn, onCheckedChange = null)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Pied de page discret
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                tr("Sauve • 100% hors-ligne & éthique • ", "Sauve • 100% offline & ethical • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                tr("À propos", "About"),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onOpenAbout() },
            )
        }
    }
}

@Composable
private fun EmergencyCallButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(10.dp),
        // 56 dp et texte sur deux lignes : cible facile à toucher sous le stress, libellé jamais coupé.
        modifier = modifier.heightIn(min = 56.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp),
    ) {
        Text(
            label,
            fontSize = 13.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
private fun EmergencyHeroButton(
    category: EmergencyCategory,
    badge: String,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp),
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.8.sp,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    category.title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    lineHeight = 24.sp,
                )
                Text(
                    category.subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }

            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "${category.actionVerb} →",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}
