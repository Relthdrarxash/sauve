package fr.sauve.app.i18n

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun systemLanguage(): String = Locale.getDefault().language

actual fun currentTimeHHmm(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
