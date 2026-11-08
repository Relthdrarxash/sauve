package fr.sauve.app.platform

import androidx.compose.runtime.staticCompositionLocalOf

/** Métronome du massage cardiaque : cadence, vibration, phases d'insufflation. */
interface Metronome {
    fun start(
        bpm: Int = 110,
        cycleLength: Int = 30,
        continuous: Boolean = false,
        onTick: (compressionIndex: Int, isBreathPhase: Boolean, totalElapsedSeconds: Long) -> Unit,
    )

    fun stop()

    /** Une vibration brève, pour confirmer un geste. */
    fun pulse()
}

/** Guidage vocal hors-ligne. */
interface Speech {
    fun speak(text: String, flush: Boolean = true)
    fun stop()
}

/** Rappel de révision tous les 3 mois, facultatif. */
interface ReviewReminderPort {
    fun isEnabled(): Boolean

    /** Active ou coupe le rappel ; demande l'autorisation de notifier si besoin. [onResult] reçoit l'état final. */
    fun setEnabled(enabled: Boolean, onResult: (Boolean) -> Unit)
}

/** Tout ce que les écrans communs demandent au système, sur Android comme sur iPhone. */
interface SauvePlatform {
    val metronome: Metronome
    val speech: Speech
    val reviewReminder: ReviewReminderPort?

    /** Ouvre le composeur avec le numéro, sans appeler à la place de la personne. */
    fun dial(number: String)

    /** Ouvre un SMS vers le numéro (114 pour les personnes sourdes ou malentendantes). */
    fun sms(number: String)

    /** Fiche d'une appli de la suite dans le magasin du système, si la plateforme en a une. */
    val canOpenStore: Boolean
    fun openStore(appId: String)
}

val LocalSauvePlatform = staticCompositionLocalOf<SauvePlatform> { error("SauvePlatform non fourni") }
