package fr.sauve.app.i18n

import java.util.Locale

/**
 * Texte en français ou en anglais selon la langue du téléphone (ou celle choisie pour l'appli).
 * Le français pour les francophones, l'anglais pour tous les autres : plus utile qu'un repli
 * en français pour une personne de passage en France.
 */
fun tr(fr: String, en: String): String = if (isFrench()) fr else en

fun isFrench(): Boolean = Locale.getDefault().language == Locale.FRENCH.language
