package fr.sauve.app.engine

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

/**
 * Moteur de synthèse vocale locale pour guider les secours mains-libres.
 * Fonctionne 100 % hors-ligne avec sélection automatique de la voix haute définition (non robotique).
 */
class SpeechEngine(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized: Boolean = false

    var isVoiceEnabled: Boolean = true

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.FRENCH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.getDefault()
            }

            // Attributs audio pour optimiser le rendu vocal et la clarté acoustique
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts?.setAudioAttributes(audioAttributes)

            // Sélectionne la meilleure voix locale naturelle (haute qualité / neuronale locale)
            selectBestNaturalVoice()

            // Débit naturel, fluide et posé (évite l'effet métallique des débits accélérés)
            tts?.setSpeechRate(0.98f)
            tts?.setPitch(1.0f)
            isInitialized = true
        }
    }

    /**
     * Recherche parmi les voix préinstallées sur l'appareil la voix française hors-ligne
     * offrant le niveau de qualité et de naturel le plus élevé (WaveNet/Neural local sur Pixel/Android).
     */
    private fun selectBestNaturalVoice() {
        try {
            val voices = tts?.voices ?: return
            val frenchVoices = voices.filter { voice ->
                voice.locale.language == Locale.FRENCH.language && !voice.isNetworkConnectionRequired
            }
            if (frenchVoices.isNotEmpty()) {
                val bestVoice = frenchVoices.maxWithOrNull(
                    compareBy<Voice> { it.quality }
                        .thenBy { if (it.name.contains("neural", ignoreCase = true) || it.name.contains("natural", ignoreCase = true)) 1 else 0 }
                        .thenByDescending { !it.name.contains("low", ignoreCase = true) }
                )
                bestVoice?.let { tts?.voice = it }
            }
        } catch (_: Exception) {
            // Repli transparent sur la voix par défaut en cas d'appareil restrictif
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
