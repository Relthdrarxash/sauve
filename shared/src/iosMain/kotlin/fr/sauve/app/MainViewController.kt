package fr.sauve.app

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import fr.sauve.app.i18n.tr
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

/** Écran demandé par un raccourci de l'icône, le widget ou un lien sauve://, avant ou après le lancement. */
private var requestedScreen by mutableStateOf<Screen?>(null)

/** Point d'entrée appelé par la coque SwiftUI (iosApp) : toute l'appli est dans le code commun. */
fun MainViewController(): UIViewController {
    // Garde l'écran allumé pendant l'intervention d'urgence (indispensable sous panique)
    UIApplication.sharedApplication.idleTimerDisabled = true

    return ComposeUIViewController {
        val platform = remember { IosSauvePlatform() }
        DisposableEffect(platform) { onDispose { platform.release() } }
        val navigator = rememberSauveNavigator()

        LaunchedEffect(requestedScreen) {
            requestedScreen?.let {
                navigator.go(it, platform)
                requestedScreen = null
            }
        }
        SauveApp(platform, navigator)
    }
}

/**
 * Cible d'un raccourci de l'icône, du widget ou d'un lien sauve:// : un nom d'écran
 * (« cardiac », « choking »...) ou « call » pour composer le numéro d'urgence.
 * iOS demande toujours confirmation avant d'appeler : l'appli n'appelle jamais à la place de la personne.
 */
fun openTarget(target: String) {
    if (target.equals("call", ignoreCase = true)) {
        val url = NSURL.URLWithString("tel:" + emergencyNumber()) ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
        return
    }
    Screen.fromName(target.uppercase())?.let { requestedScreen = it }
}

/** Le 15 en français, le 112 pour les autres langues : mêmes choix que le widget Android. */
fun emergencyNumber(): String = tr("15", "112")

/** Libellé d'un raccourci de l'icône (appui long), mêmes textes que sur Android. */
fun shortcutTitle(target: String): String = when (target) {
    "cardiac" -> tr("Arrêt cardiaque", "Cardiac arrest")
    "choking" -> tr("Étouffement", "Choking")
    "bleeding" -> tr("Hémorragie", "Severe bleeding")
    "recovery" -> tr("PLS et brûlure", "Recovery position, burns")
    else -> target
}
