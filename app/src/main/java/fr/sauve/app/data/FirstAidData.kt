package fr.sauve.app.data

/**
 * Catégories des 4 urgences vitales majeures traitées par Sauve.
 */
enum class EmergencyCategory(
    val title: String,
    val subtitle: String,
    val actionVerb: String,
    val colorHex: Long,
) {
    CARDIAC_ARREST(
        title = "Arrêt Cardiaque",
        subtitle = "Inconscient & ne respire pas",
        actionVerb = "Masser",
        colorHex = 0xFFE53935, // Rouge vif alerte
    ),
    CHOKING(
        title = "Étouffement",
        subtitle = "Voies respiratoires bouchées",
        actionVerb = "Désobstruer",
        colorHex = 0xFFF57C00, // Orange chaud
    ),
    BLEEDING(
        title = "Hémorragie Grave",
        subtitle = "Saignement abondant continu",
        actionVerb = "Comprimer",
        colorHex = 0xFFC2185B, // Pourpre sang
    ),
    RECOVERY_AND_BURNS(
        title = "Inconscient (PLS) & Brûlure",
        subtitle = "Respire normalement ou brûlure",
        actionVerb = "Protéger",
        colorHex = 0xFF1976D2, // Bleu secours
    )
}

/**
 * Profil de la victime adapté aux recommandations médicales.
 */
enum class VictimProfile(val label: String, val ageHint: String) {
    ADULT("Adulte", "> 8 ans"),
    CHILD("Enfant", "1 à 8 ans"),
    INFANT("Nourrisson", "< 1 an")
}

/**
 * Numéros d'urgence vitale reconnus en France et en Europe.
 */
data class EmergencyNumber(
    val number: String,
    val label: String,
    val description: String,
    val isSms: Boolean = false,
)

object FirstAidData {

    val EMERGENCY_NUMBERS = listOf(
        EmergencyNumber("15", "15 · SAMU", "Urgences médicales vitales"),
        EmergencyNumber("18", "18 · Pompiers", "Secours à personnes et incendies"),
        EmergencyNumber("112", "112 · Europe", "Numéro d'urgence européen (tout opérateur, sans SIM)"),
        EmergencyNumber("114", "114 · SMS", "Urgence sourds et malentendants par SMS", isSms = true)
    )

    /**
     * Fiches pratiques pour la formation continue et la révision hors crise.
     */
    data class GuideTopic(
        val id: String,
        val title: String,
        val category: String,
        val summary: String,
        val keyRules: List<String>,
        val mistakesToAvoid: List<String>,
    )

    val GUIDE_TOPICS = listOf(
        GuideTopic(
            id = "acr_dae",
            title = "Arrêt cardiaque & DAE",
            category = "Urgence absolue",
            summary = "Chaque minute sans massage cardiaque réduit les chances de survie de 10 %. L'objectif immédiat est d'oxygéner le cerveau.",
            keyRules = listOf(
                "Appeler le 15 ou le 112 dès le constat de l'absence de respiration.",
                "Placer le talon d'une main au centre de la poitrine, verrouiller la seconde main au-dessus.",
                "Enfoncer de 5 à 6 cm chez l'adulte (1/3 de l'épaisseur du thorax).",
                "Rythme : 100 à 120 compressions par minute sans interruption.",
                "Dès qu'un DAE arrive, l'allumer sans interrompre le massage et écouter ses consignes vocales."
            ),
            mistakesToAvoid = listOf(
                "Ne pas masser de peur de casser une côte : une côte cassée se répare, un cerveau non irrigué ne survit pas.",
                "Ne pas toucher la victime quand le DAE analyse ou délivre le choc."
            )
        ),
        GuideTopic(
            id = "etouffement",
            title = "Étouffement (Obstruction)",
            category = "Voies aériennes",
            summary = "Distinguer impérativement l'obstruction partielle (la personne tousse ou parle) de l'obstruction totale (aucun son, panique motrice).",
            keyRules = listOf(
                "Si la victime parle ou tousse fort : NE RIEN FAIRE, l'encourager à continuer de tousser.",
                "Si aucun son ne sort : pencher en avant, donner 5 claques sèches entre les omoplates.",
                "Si échec des claques : 5 compressions abdominales (Heimlich).",
                "Chez le nourrisson (< 1 an) : manœuvre de Mofenson sur l'avant-bras, JAMAIS de Heimlich au ventre."
            ),
            mistakesToAvoid = listOf(
                "Ne jamais taper dans le dos d'une personne qui tousse encore (risque d'enfoncer le corps étranger).",
                "Ne pas enfoncer les doigts à l'aveugle dans la gorge."
            )
        ),
        GuideTopic(
            id = "hemorragie",
            title = "Hémorragie externe grave",
            category = "Circulation",
            summary = "Un saignement abondant non stoppé peut entraîner la mort en quelques minutes par choc hypovolémique.",
            keyRules = listOf(
                "Comprimer directement et fortement la plaie avec la paume ou un linge propre.",
                "Maintenir la pression sans relâcher jusqu'à l'arrivée des secours.",
                "Allonger la victime pour éviter le malaise et la couvrir pour prévenir l'hypothermie.",
                "En cas de plaie avec corps étranger (verre, couteau) : comprimer de part et d'autre sans retirer l'objet."
            ),
            mistakesToAvoid = listOf(
                "Ne jamais relâcher la pression pour « vérifier si ça saigne encore ».",
                "Ne jamais donner à boire ni à manger à une victime qui saigne."
            )
        ),
        GuideTopic(
            id = "pls",
            title = "Position Latérale de Sécurité (PLS)",
            category = "Perte de connaissance",
            summary = "Pour toute victime inconsciente qui respire normalement. Évite l'étouffement par la langue ou par les vomissements.",
            keyRules = listOf(
                "Bascule prudente de la tête en arrière pour libérer les voies aériennes.",
                "Bras du côté du sauveteur en angle droit (90°).",
                "Amener le dos de l'autre main contre la joue de la victime.",
                "Attraper la jambe opposée au niveau du genou et la faire pivoter doucement vers soi.",
                "Ouvrir la bouche de la victime vers le sol pour l'écoulement des liquides."
            ),
            mistakesToAvoid = listOf(
                "Ne jamais laisser une personne inconsciente sur le dos.",
                "Surveiller la respiration en continu jusqu'à l'arrivée de l'ambulance."
            )
        ),
        GuideTopic(
            id = "brulures",
            title = "Brûlures thermiques",
            category = "Lésions & Plaies",
            summary = "La règle d'or pour stopper la progression thermique des tissus.",
            keyRules = listOf(
                "Refroidir immédiatement à l'eau courante tempérée (15 à 20°C).",
                "Maintenir à 15 cm de distance pendant au moins 15 minutes.",
                "Retirer les bijoux ou montres avant le gonflement.",
                "Alerter le 15 si la brûlure dépasse la taille de la paume de la main ou touche le visage."
            ),
            mistakesToAvoid = listOf(
                "Ne jamais appliquer de glaçons (risque d'aggraver la brûlure par le gel).",
                "Ne pas mettre de corps gras (huile, beurre, dentifrice).",
                "Ne jamais percer une cloque."
            )
        ),
        GuideTopic(
            id = "avc",
            title = "Malaise & AVC (Méthode FAST)",
            category = "Urgences neuro-vasculaires",
            summary = "En cas d'Accident Vasculaire Cérébral, chaque minute compte pour sauver les neurones (« Time is Brain »).",
            keyRules = listOf(
                "F (Face) : Demander de sourire. Le visage est-il asymétrique ?",
                "A (Arms) : Demander de lever les deux bras. L'un des bras retombe-t-il ?",
                "S (Speech) : Faire répéter une phrase simple. La parole est-elle confuse ou pâteuse ?",
                "T (Time) : Noter l'heure exacte des premiers symptômes et appeler le 15 immédiatement."
            ),
            mistakesToAvoid = listOf(
                "Ne pas faire coucher la victime à plat si elle a du mal à respirer : l'installer en position assise ou demi-assise.",
                "Ne donner aucun médicament (ni aspirine ni paracétamol) avant l'avis du médecin régulateur."
            )
        )
    )
}
