package fr.sauve.app.engine

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Vibration du métronome sur Android. La cadence elle-même est dans [CprMetronome] (module shared),
 * commune avec l'iPhone.
 */
class HapticPulse(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /** Vibration nette et percutante de 55 ms. */
    fun pulse() {
        try {
            vibrator?.takeIf { it.hasVibrator() }?.vibrate(VibrationEffect.createOneShot(55, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Exception) {
            // Tolérance silencieuse si l'appareil bloque la vibration
        }
    }
}
