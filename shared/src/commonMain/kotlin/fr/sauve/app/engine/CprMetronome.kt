package fr.sauve.app.engine

import fr.sauve.app.platform.Metronome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Cadence du massage cardiaque, commune à Android et iOS ; seule la vibration est propre au système.
 *
 * Chaque battement est calé sur l'horloge depuis le départ, et non sur la fin du précédent :
 * la cadence ne dérive pas (à 110 bpm, 100 compressions prennent bien 54,5 s).
 * Hors mode continu, une pause d'insufflation de [breathPauseMs] suit chaque cycle de [cycleLength].
 */
class CprMetronome(
    private val scope: CoroutineScope,
    private val haptic: () -> Unit,
    private val clock: TimeSource = TimeSource.Monotonic,
    private val breathPauseMs: Long = 4_500L,
) : Metronome {

    private var job: Job? = null

    val isRunning: Boolean get() = job?.isActive == true

    override fun start(
        bpm: Int,
        cycleLength: Int,
        continuous: Boolean,
        onTick: (compressionIndex: Int, isBreathPhase: Boolean, totalElapsedSeconds: Long) -> Unit,
    ) {
        stop()
        val interval = intervalMs(bpm)
        job = scope.launch {
            val start: TimeMark = clock.markNow()
            var count = 0
            var nextAtMs = 0L
            while (isActive) {
                count++
                haptic()
                val elapsedSeconds = start.elapsedNow().inWholeSeconds
                val breath = !continuous && count % cycleLength == 0
                onTick(count, breath, elapsedSeconds)
                nextAtMs += if (breath) breathPauseMs else interval
                delay((nextAtMs - start.elapsedNow().inWholeMilliseconds).coerceAtLeast(0))
            }
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
    }

    override fun pulse() = haptic()

    companion object {
        /** 100 à 120 compressions par minute recommandées ; bornes de sécurité 80 à 140. */
        fun intervalMs(bpm: Int): Long = 60_000L / bpm.coerceIn(80, 140)
    }
}
