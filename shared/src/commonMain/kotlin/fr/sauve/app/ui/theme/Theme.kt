package fr.sauve.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palette Urgence & Anti-Panique (contraste élevé, accessible sous stress)
val DarkGround = Color(0xFF0F1216)
val DarkSurface = Color(0xFF181C22)
val DarkSurfaceVariant = Color(0xFF222830)
val DarkBorder = Color(0xFF2E3642)
val LightText = Color(0xFFF2F4F7)
val MutedText = Color(0xFF98A2B3)

// Couleurs thématiques des 4 urgences vitales
val EmergencyRed = Color(0xFFE53935)
val EmergencyRedDark = Color(0xFFB71C1C)
val EmergencyOrange = Color(0xFFF57C00)
val EmergencyOrangeDark = Color(0xFFE65100)
val EmergencyCrimson = Color(0xFFC2185B)
val EmergencyCrimsonDark = Color(0xFF880E4F)
val EmergencyBlue = Color(0xFF1976D2)
val EmergencyBlueDark = Color(0xFF0D47A1)
val EmergencyGreen = Color(0xFF388E3C)

private val DarkColors = darkColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    primaryContainer = EmergencyRedDark,
    onPrimaryContainer = Color.White,
    secondary = EmergencyOrange,
    onSecondary = Color.White,
    secondaryContainer = EmergencyOrangeDark,
    onSecondaryContainer = Color.White,
    tertiary = EmergencyBlue,
    onTertiary = Color.White,
    background = DarkGround,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = MutedText,
    outline = DarkBorder,
)

private val LightColors = lightColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = EmergencyRedDark,
    secondary = EmergencyOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = EmergencyOrangeDark,
    tertiary = EmergencyBlue,
    onTertiary = Color.White,
    background = Color(0xFFFBFBFB),
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = Color(0xFF495057),
    outline = Color(0xFFD0D5DD),
)

@Composable
fun SauveTheme(
    darkTheme: Boolean = true, // Par défaut sombre pour contraste maximal & économie batterie OLED
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
