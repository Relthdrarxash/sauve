package fr.sauve.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.sauve.app.data.EmergencyCategory
import fr.sauve.app.platform.LocalSauvePlatform
import fr.sauve.app.platform.SauvePlatform
import fr.sauve.app.ui.AboutScreen
import fr.sauve.app.ui.BleedingScreen
import fr.sauve.app.ui.CardiacScreen
import fr.sauve.app.ui.ChokingScreen
import fr.sauve.app.ui.GuideScreen
import fr.sauve.app.ui.HomeScreen
import fr.sauve.app.ui.RecoveryScreen
import fr.sauve.app.ui.theme.SauveTheme

enum class Screen {
    HOME,
    CARDIAC,
    CHOKING,
    BLEEDING,
    RECOVERY,
    GUIDE,
    ABOUT,

    /** Massage cardiaque à blanc, sur un coussin : réviser le rythme sans urgence. */
    TRAINING,
    ;

    companion object {
        /** Extra des raccourcis d'appli, du widget et du rappel de révision (Android). */
        const val EXTRA = "fr.sauve.SCREEN"

        fun fromName(name: String?): Screen? = entries.firstOrNull { it.name == name }
    }
}

/** Écran courant, conservé si le système ferme l'appli pendant l'appel au 15. */
class SauveNavigator(initial: Screen) {
    var current by mutableStateOf(initial)
        private set

    fun go(screen: Screen, platform: SauvePlatform) {
        platform.metronome.stop()
        platform.speech.stop()
        current = screen
    }
}

@Composable
fun rememberSauveNavigator(initial: Screen = Screen.HOME): SauveNavigator =
    rememberSaveable(
        saver = Saver(save = { it.current.name }, restore = { SauveNavigator(Screen.fromName(it) ?: initial) }),
    ) { SauveNavigator(initial) }

/** L'appli entière, identique sur Android et sur iPhone. */
@Composable
fun SauveApp(platform: SauvePlatform, navigator: SauveNavigator) {
    val metronome = platform.metronome
    val speech = platform.speech
    fun go(screen: Screen) = navigator.go(screen, platform)

    CompositionLocalProvider(LocalSauvePlatform provides platform) {
        SauveTheme(darkTheme = true) {
            // Le fond va jusqu'aux bords ; le contenu reste hors de l'encoche, de la barre d'état
            // et de la barre de navigation, sur iPhone comme sur Android 15 et plus (bord à bord imposé).
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                when (navigator.current) {
                    Screen.HOME -> HomeScreen(
                        onSelectCategory = { category ->
                            go(
                                when (category) {
                                    EmergencyCategory.CARDIAC_ARREST -> Screen.CARDIAC
                                    EmergencyCategory.CHOKING -> Screen.CHOKING
                                    EmergencyCategory.BLEEDING -> Screen.BLEEDING
                                    EmergencyCategory.RECOVERY_AND_BURNS -> Screen.RECOVERY
                                },
                            )
                        },
                        onOpenGuide = { go(Screen.GUIDE) },
                        onOpenAbout = { go(Screen.ABOUT) },
                        onOpenTraining = { go(Screen.TRAINING) },
                    )
                    Screen.CARDIAC -> CardiacScreen(
                        metronome = metronome,
                        speechEngine = speech,
                        onBack = { go(Screen.HOME) },
                        onNavigateToPls = { go(Screen.RECOVERY) },
                    )
                    Screen.TRAINING -> CardiacScreen(
                        metronome = metronome,
                        speechEngine = speech,
                        onBack = { go(Screen.HOME) },
                        onNavigateToPls = { go(Screen.RECOVERY) },
                        training = true,
                    )
                    Screen.CHOKING -> ChokingScreen(
                        metronome = metronome,
                        speechEngine = speech,
                        onBack = { go(Screen.HOME) },
                        onVictimUnconscious = { go(Screen.CARDIAC) },
                    )
                    Screen.BLEEDING -> BleedingScreen(
                        speechEngine = speech,
                        onBack = { go(Screen.HOME) },
                    )
                    Screen.RECOVERY -> RecoveryScreen(
                        metronome = metronome,
                        speechEngine = speech,
                        onBack = { go(Screen.HOME) },
                    )
                    Screen.GUIDE -> GuideScreen(onBack = { go(Screen.HOME) })
                    Screen.ABOUT -> AboutScreen(onBack = { go(Screen.HOME) })
                }
            }
        }
    }
}
