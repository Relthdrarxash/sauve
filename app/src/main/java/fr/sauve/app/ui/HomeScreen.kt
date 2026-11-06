package fr.sauve.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
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
) {
    val context = LocalContext.current

    fun callNumber(num: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
        context.startActivity(intent)
    }

    fun sendSms(num: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$num"))
        context.startActivity(intent)
    }

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
                    text = "Premiers secours anti-panique",
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
                Text("Fiches PSC1", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                    "APPELS D'URGENCE (1 TAP)",
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
                        label = "15 SAMU",
                        color = EmergencyRed,
                        modifier = Modifier.weight(1f),
                        onClick = { callNumber("15") },
                    )
                    EmergencyCallButton(
                        label = "18 Pompiers",
                        color = EmergencyOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { callNumber("18") },
                    )
                    EmergencyCallButton(
                        label = "112 Europe",
                        color = EmergencyBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { callNumber("112") },
                    )
                    EmergencyCallButton(
                        label = "114 SMS",
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.weight(0.9f),
                        onClick = { sendSms("114") },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "QUELLE EST L'URGENCE ?",
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
            badge = "5 TAPES / HEIMLICH",
            backgroundColor = EmergencyOrange,
            onClick = { onSelectCategory(EmergencyCategory.CHOKING) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmergencyHeroButton(
            category = EmergencyCategory.BLEEDING,
            badge = "COMPRESSION CONTINUE",
            backgroundColor = EmergencyCrimson,
            onClick = { onSelectCategory(EmergencyCategory.BLEEDING) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmergencyHeroButton(
            category = EmergencyCategory.RECOVERY_AND_BURNS,
            badge = "PLS · 15 MIN EAU",
            backgroundColor = EmergencyBlue,
            onClick = { onSelectCategory(EmergencyCategory.RECOVERY_AND_BURNS) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Pied de page discret
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Sauve • 100% hors-ligne & éthique • ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "À propos",
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
        modifier = modifier.height(44.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp),
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
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
