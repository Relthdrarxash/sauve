package fr.sauve.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
}

class MainActivity : ComponentActivity() {

    private lateinit var metronome: HapticMetronome
    private lateinit var speechEngine: SpeechEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Garde l'écran allumé pendant l'intervention d'urgence (indispensable sous panique)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        metronome = HapticMetronome(this)
        speechEngine = SpeechEngine(this)

        setContent {
            SauveTheme(darkTheme = true) {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }

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
                        onOpenAbout = { currentScreen = Screen.ABOUT },
                    )

                    Screen.CARDIAC -> CardiacScreen(
                        metronome = metronome,
                        speechEngine = speechEngine,
                        onBack = { currentScreen = Screen.HOME },
                        onNavigateToPls = { currentScreen = Screen.RECOVERY },
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

    override fun onDestroy() {
        super.onDestroy()
        metronome.stop()
        speechEngine.release()
    }
}
