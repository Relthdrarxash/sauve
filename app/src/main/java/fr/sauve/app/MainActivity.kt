package fr.sauve.app

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.sauve.app.engine.CprMetronome
import fr.sauve.app.engine.HapticPulse
import fr.sauve.app.engine.SpeechEngine
import fr.sauve.app.platform.Metronome
import fr.sauve.app.platform.ReviewReminderPort
import fr.sauve.app.platform.SauvePlatform
import fr.sauve.app.platform.Speech
import fr.sauve.app.reminder.ReviewReminder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/** Coque Android : l'appli elle-même (écrans, navigation, contenus) est dans le module shared. */
class MainActivity : ComponentActivity(), SauvePlatform {

    private val scope = MainScope()
    private lateinit var speechEngine: SpeechEngine
    private lateinit var cpr: CprMetronome

    /** Écran demandé par un raccourci ou le widget alors que l'appli est déjà ouverte. */
    private var requestedScreen by mutableStateOf<Screen?>(null)

    private var pendingReviewResult: ((Boolean) -> Unit)? = null
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        ReviewReminder.setEnabled(this, granted)
        pendingReviewResult?.invoke(granted)
        pendingReviewResult = null
    }

    override val metronome: Metronome get() = cpr
    override val speech: Speech get() = speechEngine

    override val reviewReminder: ReviewReminderPort = object : ReviewReminderPort {
        override fun isEnabled(): Boolean = ReviewReminder.isEnabled(this@MainActivity)

        override fun setEnabled(enabled: Boolean, onResult: (Boolean) -> Unit) {
            if (enabled && !ReviewReminder.canNotify(this@MainActivity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pendingReviewResult = onResult
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                ReviewReminder.setEnabled(this@MainActivity, enabled)
                onResult(enabled)
            }
        }
    }

    override fun dial(number: String) = launch(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))

    override fun sms(number: String) = launch(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")))

    override val canOpenStore: Boolean = true

    override fun openStore(appId: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId")))
        } catch (_: ActivityNotFoundException) {
            launch(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appId")))
        }
    }

    private fun launch(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Pas d'appli pour ça (tablette sans téléphonie) : rien à faire.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        // Garde l'écran allumé pendant l'intervention d'urgence (indispensable sous panique)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        cpr = CprMetronome(scope, HapticPulse(this)::pulse)
        speechEngine = SpeechEngine(this)
        val initialScreen = (if (savedInstanceState == null) Screen.fromName(intent.getStringExtra(Screen.EXTRA)) else null) ?: Screen.HOME

        setContent {
            val navigator = rememberSauveNavigator(initialScreen)

            LaunchedEffect(requestedScreen) {
                requestedScreen?.let {
                    navigator.go(it, this@MainActivity)
                    requestedScreen = null
                }
            }
            BackHandler(enabled = navigator.current != Screen.HOME) {
                navigator.go(Screen.HOME, this@MainActivity)
            }
            SauveApp(this@MainActivity, navigator)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Screen.fromName(intent.getStringExtra(Screen.EXTRA))?.let { requestedScreen = it }
    }

    override fun onDestroy() {
        super.onDestroy()
        cpr.stop()
        speechEngine.release()
        scope.cancel()
    }
}
