package fr.sauve.app

import fr.sauve.app.engine.CprMetronome
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CprMetronomeTest {

    @Test
    fun cadenceDoesNotDrift() = runTest {
        var pulses = 0
        val ticks = mutableListOf<Long>()
        val metronome = CprMetronome(backgroundScope, { pulses++ }, testTimeSource)
        metronome.start(bpm = 110, cycleLength = 30, continuous = true) { _, _, _ -> ticks += testScheduler.currentTime }
        // 545 ms à 110 bpm : 101 battements sur 100 intervalles, sans retard cumulé.
        advanceTimeBy(545L * 100 + 1)
        metronome.stop()
        assertEquals(101, ticks.size)
        assertEquals(545L * 100, ticks.last())
        assertEquals(101, pulses)
    }

    @Test
    fun breathPauseAfterEachCycle() = runTest {
        val events = mutableListOf<Pair<Long, Boolean>>()
        val metronome = CprMetronome(backgroundScope, {}, testTimeSource, breathPauseMs = 4_500L)
        metronome.start(bpm = 110, cycleLength = 3, continuous = false) { _, breath, _ -> events += testScheduler.currentTime to breath }
        advanceTimeBy(1_090L + 4_500L + 1)
        metronome.stop()
        assertEquals(listOf(0L to false, 545L to false, 1_090L to true, 5_590L to false), events)
    }

    @Test
    fun tempoIsClampedToSafeBounds() {
        assertEquals(60_000L / 140, CprMetronome.intervalMs(300))
        assertEquals(60_000L / 80, CprMetronome.intervalMs(10))
    }
}
