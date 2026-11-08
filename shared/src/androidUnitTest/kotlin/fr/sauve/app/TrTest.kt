package fr.sauve.app

import fr.sauve.app.i18n.tr
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class TrTest {

    private val original = Locale.getDefault()

    @After
    fun restore() = Locale.setDefault(original)

    @Test
    fun frenchForFrenchSpeakers() {
        Locale.setDefault(Locale.CANADA_FRENCH)
        assertEquals("Masser", tr("Masser", "Compress"))
    }

    @Test
    fun englishForEveryoneElse() {
        Locale.setDefault(Locale.GERMAN)
        assertEquals("Compress", tr("Masser", "Compress"))
    }
}
