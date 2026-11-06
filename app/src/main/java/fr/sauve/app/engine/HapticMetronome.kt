package fr.sauve.app.engine

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Métronome haptique haute précision pour cadencer les compressions thoraciques (massage cardiaque).
 * Recommandations ERC / PSC1 : 100 à 120 compressions par minute (par défaut 110 bpm).
 */
class HapticMetronome(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var job: Job? = null
    private var scope = CoroutineScope(Dispatchers.Default)

    /** Vibreur actif ou muet. */
    var isHapticEnabled: Boolean = true

    /**
     * Calcule l'intervalle en millisecondes entre deux compressions pour un tempo donné.
     * Ex : 110 bpm -> 60 000 / 110 = 545 ms.
     */
    fun intervalMs(bpm: Int): Long = (60_000L / bpm.coerceIn(80, 140))

    /**
     * Démarre le métronome avec le tempo donné (bpm) et un rappel à chaque impulsion.
     * @param bpm tempo en battements par minute (défaut 110)
     * @param onTick rappel exécuté à chaque battement avec le numéro de la compression
     */
    fun start(
        bpm: Int = 110,
        cycleLength: Int = 30,
        continuous: Boolean = false,
        onTick: (compressionIndex: Int, isBreathPhase: Boolean, totalElapsedSeconds: Long) -> Unit,
    ) {
        stop()
        val interval = intervalMs(bpm)
        job = scope.launch {
            var count = 0
            val startTime = System.currentTimeMillis()
            while (isActive) {
                count++
                val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000

                // Déclenche la vibration tactile brève
                if (isHapticEnabled) {
                    pulse()
                }

                if (!continuous && count % cycleLength == 0) {
                    // Fin d'un cycle de 30 compressions : 2 insufflations (pause de 4-5 secondes)
                    onTick(count, true, elapsedSeconds)
                    delay(4500L) // temps d'insufflation
                } else {
                    onTick(count, false, elapsedSeconds)
                    delay(interval)
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    val isRunning: Boolean
        get() = job?.isActive == true

    /** Produit une vibration nette et percutante de 60 ms. */
    fun pulse() {
        try {
            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(55, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(55)
                    }
                }
            }
        } catch (_: Exception) {
            // Tolérance silencieuse si l'appareil bloque la vibration
        }
    }
}
