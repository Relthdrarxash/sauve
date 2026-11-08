package fr.sauve.app.ui

import fr.sauve.app.i18n.tr
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sauve.app.data.FirstAidData
import fr.sauve.app.ui.theme.EmergencyGreen
import fr.sauve.app.ui.theme.EmergencyOrange
import fr.sauve.app.ui.theme.EmergencyRed

@Composable
fun GuideScreen(
    onBack: () -> Unit,
) {
    var expandedTopicId by rememberSaveable { mutableStateOf<String?>(null) }

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
            Text(tr("Guide Réflexes PSC1", "First aid reflexes guide"), fontWeight = FontWeight.Bold, color = EmergencyGreen, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            tr("Fiches pratiques de révision", "Practical refresher sheets"),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            tr("Consultez ces guides à tête reposée pour mémoriser les réflexes vitaux.", "Read these guides calmly, ahead of time, to memorise the life-saving reflexes."),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        FirstAidData.GUIDE_TOPICS.forEach { topic ->
            val isExpanded = expandedTopicId == topic.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        expandedTopicId = if (isExpanded) null else topic.id
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                topic.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                topic.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            if (isExpanded) "▲" else "▼",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        topic.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            Text(
                                tr("Gestes clés :", "Key steps:"),
                                fontWeight = FontWeight.Bold,
                                color = EmergencyGreen,
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            topic.keyRules.forEach { rule ->
                                Text("• $rule", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                tr("Erreurs à éviter :", "Mistakes to avoid:"),
                                fontWeight = FontWeight.Bold,
                                color = EmergencyRed,
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            topic.mistakesToAvoid.forEach { mistake ->
                                Text("✗ $mistake", style = MaterialTheme.typography.bodySmall, color = EmergencyOrange, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
