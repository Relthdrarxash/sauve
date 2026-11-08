package fr.sauve.app.i18n

/**
 * Texte en français ou en anglais selon la langue du téléphone (ou celle choisie pour l'appli).
 * Le français pour les francophones, l'anglais pour tous les autres : plus utile qu'un repli
 * en français pour une personne de passage en France.
 */
fun tr(fr: String, en: String): String = if (isFrench()) fr else en

fun isFrench(): Boolean = systemLanguage() == "fr"

/** Code de langue ISO 639-1 de l'interface (« fr », « en »...). */
expect fun systemLanguage(): String

/** Heure locale « HH:mm », pour noter l'heure de pose d'un garrot. */
expect fun currentTimeHHmm(): String

/** « 07:05 » à partir d'un nombre de secondes. */
fun minutesSeconds(totalSeconds: Long): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    return "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
}
