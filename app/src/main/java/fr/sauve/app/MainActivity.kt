package fr.sauve.app

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import fr.sauve.app.data.EmergencyCategory
import fr.sauve.app.engine.HapticMetronome
import fr.sauve.app.engine.SpeechEngine
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
        /** Extra des raccourcis d'appli, du widget et du rappel de révision. */
        const val EXTRA = "fr.sauve.SCREEN"

        fun from(intent: Intent?): Screen? =
            intent?.getStringExtra(EXTRA)?.let { name -> entries.firstOrNull { it.name == name } }
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var metronome: HapticMetronome
    private lateinit var speechEngine: SpeechEngine

    /** Écran demandé par un raccourci ou le widget alors que l'appli est déjà ouverte. */
    private var requestedScreen by mutableStateOf<Screen?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Garde l'écran allumé pendant l'intervention d'urgence (indispensable sous panique)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        metronome = HapticMetronome(this)
        speechEngine = SpeechEngine(this)
        val initialScreen = (if (savedInstanceState == null) Screen.from(intent) else null) ?: Screen.HOME

        setContent {
            SauveTheme(darkTheme = true) {
                // Conservé si Android ferme l'appli pendant l'appel au 15 : on revient au même geste.
                var currentScreen by rememberSaveable { mutableStateOf(initialScreen) }

                LaunchedEffect(requestedScreen) {
                    requestedScreen?.let {
                        metronome.stop()
                        speechEngine.stop()
                        currentScreen = it
                        requestedScreen = null
                    }
                }

                BackHandler(enabled = currentScreen != Screen.HOME) {
                    metronome.stop()
                    speechEngine.stop()
                    currentScreen = Screen.HOME
                }

                when (currentScreen) {
                    Screen.HOME -> HomeScreen(
                        onSelectCategory = { category ->
                            currentScreen = when (category) {
                                EmergencyCategory.CARDIAC_ARREST -> Screen.CARDIAC
                                EmergencyCategory.CHOKING -> Screen.CHOKING
                                EmergencyCategory.BLEEDING -> Screen.BLEEDING
                                EmergencyCategory.RECOVERY_AND_BURNS -> Screen.RECOVERY
                            }
                        },
                        onOpenGuide = { currentScreen = Screen.GUIDE },
                        onOpenTraining = { currentScreen = Screen.TRAINING },
                        onOpenAbout = { currentScreen = Screen.ABOUT },
                    )

                    Screen.CARDIAC -> CardiacScreen(
                        metronome = metronome,
                        speechEngine = speechEngine,
                        onBack = { currentScreen = Screen.HOME },
                        onNavigateToPls = { currentScreen = Screen.RECOVERY },
                    )

                    Screen.TRAINING -> CardiacScreen(
                        metronome = metronome,
                        speechEngine = speechEngine,
                        onBack = { currentScreen = Screen.HOME },
                        onNavigateToPls = { currentScreen = Screen.RECOVERY },
                        training = true,
                    )

                    Screen.CHOKING -> ChokingScreen(
                        metronome = metronome,
                        speechEngine = speechEngine,
                        onBack = { currentScreen = Screen.HOME },
                        onVictimUnconscious = { currentScreen = Screen.CARDIAC },
                    )

                    Screen.BLEEDING -> BleedingScreen(
                        speechEngine = speechEngine,
                        onBack = { currentScreen = Screen.HOME },
                    )

                    Screen.RECOVERY -> RecoveryScreen(
                        metronome = metronome,
                        speechEngine = speechEngine,
                        onBack = { currentScreen = Screen.HOME },
                    )

                    Screen.GUIDE -> GuideScreen(
                        onBack = { currentScreen = Screen.HOME },
                    )

                    Screen.ABOUT -> AboutScreen(
                        onBack = { currentScreen = Screen.HOME },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Screen.from(intent)?.let { requestedScreen = it }
    }

    override fun onDestroy() {
        super.onDestroy()
        metronome.stop()
        speechEngine.release()
    }
}
