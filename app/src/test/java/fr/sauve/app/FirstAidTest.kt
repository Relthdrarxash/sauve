package fr.sauve.app

import fr.sauve.app.data.EmergencyCategory
import fr.sauve.app.data.FirstAidData
import fr.sauve.app.data.VictimProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirstAidTest {

    @Test
    fun metronomeIntervalCalculations() {
        fun computeInterval(bpm: Int): Long = (60_000L / bpm.coerceIn(80, 140))

        // 110 bpm est le tempo idéal de l'ERC
        assertEquals(545L, computeInterval(110))
        assertEquals(600L, computeInterval(100))
        assertEquals(500L, computeInterval(120))

        // Borne de sécurité
        assertEquals(750L, computeInterval(50)) // bridé à 80 bpm (60_000 / 80 = 750)
        assertEquals(428L, computeInterval(200)) // bridé à 140 bpm (60_000 / 140 = 428)
    }

    @Test
    fun emergencyNumbersCompleteness() {
        val numbers = FirstAidData.EMERGENCY_NUMBERS
        assertTrue("Les numéros d'urgence doivent être au moins 4 (15, 18, 112, 114)", numbers.size >= 4)

        val map = numbers.associateBy { it.number }
        assertNotNull(map["15"])
        assertNotNull(map["18"])
        assertNotNull(map["112"])
        assertNotNull(map["114"])

        assertTrue(map["114"]!!.isSms)
    }

    @Test
    fun emergencyCategoriesIntegrity() {
        val categories = EmergencyCategory.entries
        assertEquals(4, categories.size)

        categories.forEach { cat ->
            assertTrue("Titre non vide", cat.title.isNotBlank())
            assertTrue("Sous-titre non vide", cat.subtitle.isNotBlank())
            assertTrue("Verbe d'action non vide", cat.actionVerb.isNotBlank())
        }
    }

    @Test
    fun guideTopicsValidity() {
        val guides = FirstAidData.GUIDE_TOPICS
        assertTrue(guides.isNotEmpty())

        guides.forEach { guide ->
            assertTrue(guide.title.isNotBlank())
            assertTrue("Chaque guide doit avoir au moins 2 règles clés", guide.keyRules.size >= 2)
            assertTrue("Chaque guide doit avoir au moins 1 erreur à éviter", guide.mistakesToAvoid.isNotEmpty())
        }
    }

    @Test
    fun victimProfileDistinction() {
        val adult = VictimProfile.ADULT
        val child = VictimProfile.CHILD
        val infant = VictimProfile.INFANT

        assertEquals("Adulte", adult.label)
        assertEquals("Enfant", child.label)
        assertEquals("Nourrisson", infant.label)
    }
}
