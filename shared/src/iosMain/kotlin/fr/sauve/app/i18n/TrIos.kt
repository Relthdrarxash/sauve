package fr.sauve.app.i18n

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

/** Première langue préférée de l'iPhone (« fr-FR » donne « fr »). */
actual fun systemLanguage(): String =
    (NSLocale.preferredLanguages.firstOrNull() as? String)?.substringBefore("-") ?: "en"

actual fun currentTimeHHmm(): String {
    val formatter = NSDateFormatter()
    // Locale fixe : toujours sur 24 h, même si l'iPhone affiche l'heure sur 12 h.
    formatter.locale = NSLocale(localeIdentifier = "en_US_POSIX")
    formatter.dateFormat = "HH:mm"
    return formatter.stringFromDate(NSDate())
}
