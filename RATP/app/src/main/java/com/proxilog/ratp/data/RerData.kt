package com.proxilog.ratp.data

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class RerLine(
    val id: String,
    val color: Color,
    // Gares du tronc commun, dans l'ordre de la direction 1 vers la direction 2
    val stations: List<String>,
    val direction1: Direction,
    val direction2: Direction,
)

data class Direction(
    val label: String,
    val termini: List<String>,
    val missionCodes: List<String>,
)

data class Departure(
    val mission: String,
    val destination: String,
    val minutes: Int,
    val platform: String,
)

object RerData {

    val lines = listOf(
        RerLine(
            id = "A",
            color = Color(0xFFE3051C),
            stations = listOf(
                "Nanterre-Préfecture",
                "La Défense",
                "Charles de Gaulle – Étoile",
                "Auber",
                "Châtelet – Les Halles",
                "Gare de Lyon",
                "Nation",
                "Vincennes",
            ),
            direction1 = Direction(
                label = "Ouest",
                termini = listOf("Saint-Germain-en-Laye", "Cergy-le-Haut", "Poissy"),
                missionCodes = listOf("ZEUS", "QIKI", "TEDI", "UPAC"),
            ),
            direction2 = Direction(
                label = "Est",
                termini = listOf("Boissy-Saint-Léger", "Marne-la-Vallée Chessy"),
                missionCodes = listOf("NELY", "BOBI", "QUVA", "ZARA"),
            ),
        ),
        RerLine(
            id = "B",
            color = Color(0xFF5291CE),
            stations = listOf(
                "La Plaine – Stade de France",
                "Gare du Nord",
                "Châtelet – Les Halles",
                "Saint-Michel – Notre-Dame",
                "Luxembourg",
                "Port-Royal",
                "Denfert-Rochereau",
                "Cité Universitaire",
                "Bourg-la-Reine",
            ),
            direction1 = Direction(
                label = "Nord",
                termini = listOf("Aéroport CDG 2", "Mitry-Claye"),
                missionCodes = listOf("EPAF", "KFIN", "ICAR", "EKLI"),
            ),
            direction2 = Direction(
                label = "Sud",
                termini = listOf("Saint-Rémy-lès-Chevreuse", "Robinson", "Massy-Palaiseau"),
                missionCodes = listOf("SOIR", "PIST", "KALI", "PAPA"),
            ),
        ),
    )

    /** Prochains départs fictifs, stables pour un couple gare / direction. */
    fun departures(line: RerLine, station: String, direction: Direction, count: Int = 5): List<Departure> {
        val random = Random("${line.id}|$station|${direction.label}".hashCode())
        var minutes = random.nextInt(0, 4)
        return List(count) {
            val departure = Departure(
                mission = direction.missionCodes.random(random),
                destination = direction.termini.random(random),
                minutes = minutes,
                platform = if (direction == line.direction1) "1" else "2",
            )
            minutes += random.nextInt(3, 9)
            departure
        }
    }
}
