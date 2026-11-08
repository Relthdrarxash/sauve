@file:OptIn(ExperimentalForeignApi::class)

package fr.sauve.app

import fr.sauve.app.engine.CprMetronome
import fr.sauve.app.i18n.isFrench
import fr.sauve.app.i18n.tr
import fr.sauve.app.platform.Metronome
import fr.sauve.app.platform.ReviewReminderPort
import fr.sauve.app.platform.SauvePlatform
import fr.sauve.app.platform.Speech
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.MainScope
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.Foundation.NSURL
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/** Ce que les écrans communs demandent à l'iPhone : voix, vibration, téléphone, rappel. */
class IosSauvePlatform : SauvePlatform {

    private val haptic = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)

    private val cpr = CprMetronome(MainScope(), {
        haptic.prepare()
        haptic.impactOccurred()
    })

    override val metronome: Metronome get() = cpr
    override val speech: Speech = IosSpeech()
    override val reviewReminder: ReviewReminderPort = IosReviewReminder()

    override fun dial(number: String) = open("tel:$number")

    override fun sms(number: String) = open("sms:$number")

    // Pas de fiche App Store pour les autres applis de la suite pour l'instant.
    override val canOpenStore: Boolean = false

    override fun openStore(appId: String) {}

    private fun open(address: String) {
        val url = NSURL.URLWithString(address) ?: return
        // Sans téléphonie (iPad, simulateur), iOS ne fait simplement rien.
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
    }

    fun release() {
        cpr.stop()
        speech.stop()
    }
}

/** Guidage vocal hors-ligne avec les voix intégrées à iOS. */
private class IosSpeech : Speech {
    private val synthesizer = AVSpeechSynthesizer()

    init {
        // La voix doit s'entendre même avec le bouton silencieux activé : c'est une urgence.
        AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback, null)
    }

    override fun speak(text: String, flush: Boolean) {
        if (flush) synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
        val utterance = AVSpeechUtterance(string = text)
        utterance.voice = AVSpeechSynthesisVoice.voiceWithLanguage(if (isFrench()) "fr-FR" else "en-US")
        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }
}

/** Rappel de révision tous les 3 mois, par une notification locale répétée. */
private class IosReviewReminder : ReviewReminderPort {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override fun isEnabled(): Boolean = defaults.boolForKey(KEY_ENABLED)

    override fun setEnabled(enabled: Boolean, onResult: (Boolean) -> Unit) {
        if (!enabled) {
            center.removePendingNotificationRequestsWithIdentifiers(listOf(IDENTIFIER))
            defaults.setBool(false, forKey = KEY_ENABLED)
            onResult(false)
            return
        }
        center.requestAuthorizationWithOptions(UNAuthorizationOptionAlert or UNAuthorizationOptionSound) { granted, _ ->
            if (granted) schedule()
            defaults.setBool(granted, forKey = KEY_ENABLED)
            dispatch_async(dispatch_get_main_queue()) { onResult(granted) }
        }
    }

    private fun schedule() {
        val content = UNMutableNotificationContent()
        content.title = tr("Deux minutes pour réviser ?", "Two minutes to practise?")
        content.body = tr(
            "Les gestes s'oublient sans pratique. Refaites le rythme du massage cardiaque sur un coussin.",
            "First aid skills fade without practice. Run through the chest compression rhythm on a cushion.",
        )
        content.sound = UNNotificationSound.defaultSound
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(INTERVAL_DAYS * 24.0 * 3600.0, repeats = true)
        val request = UNNotificationRequest.requestWithIdentifier(IDENTIFIER, content, trigger)
        center.addNotificationRequest(request, withCompletionHandler = null)
    }

    private companion object {
        const val IDENTIFIER = "revision"
        const val KEY_ENABLED = "review_reminder"
        const val INTERVAL_DAYS = 90.0
    }
}
