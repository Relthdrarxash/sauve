package fr.sauve.app.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Moteur de synthèse vocale locale pour guider les secours mains-libres.
 * Fonctionne 100 % hors-ligne avec le moteur TTS natif d'Android.
 */
class SpeechEngine(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized: Boolean = false

    var isVoiceEnabled: Boolean = true

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.FRENCH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback sur la locale par défaut du téléphone
                tts?.language = Locale.getDefault()
            }
            tts?.setSpeechRate(1.05f) // Rythme clair et dynamique
            tts?.setPitch(1.0f)
            isInitialized = true
        }
    }

    /**
     * Lit une consigne à haute voix si la voix est activée.
     * @param text phrase à énoncer
     * @param flush si true, interrompt immédiatement la phrase précédente pour donner la nouvelle consigne
     */
    fun speak(text: String, flush: Boolean = true) {
        if (!isVoiceEnabled || !isInitialized) return
        val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, mode, null, text.hashCode().toString())
    }

    /** Arrête immédiatement toute parole en cours. */
    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    /** Libère les ressources TTS à la fermeture de l'application. */
    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
