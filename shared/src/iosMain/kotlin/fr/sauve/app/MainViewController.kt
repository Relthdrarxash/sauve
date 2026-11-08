package fr.sauve.app

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

/** Point d'entrée appelé par la coque SwiftUI (iosApp) : toute l'appli est dans le code commun. */
fun MainViewController(): UIViewController {
    // Garde l'écran allumé pendant l'intervention d'urgence (indispensable sous panique)
    UIApplication.sharedApplication.idleTimerDisabled = true

    return ComposeUIViewController {
        val platform = remember { IosSauvePlatform() }
        DisposableEffect(platform) { onDispose { platform.release() } }
        SauveApp(platform, rememberSauveNavigator())
    }
}
