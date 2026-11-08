package fr.sauve.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.sauve.app.resources.Res
import fr.sauve.app.resources.bleeding_pressure
import fr.sauve.app.resources.choking_back_blows
import fr.sauve.app.resources.choking_heimlich
import fr.sauve.app.resources.choking_infant
import fr.sauve.app.resources.cpr_adult
import fr.sauve.app.resources.cpr_child
import fr.sauve.app.resources.cpr_infant
import fr.sauve.app.resources.recovery_position
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** Schémas de gestes disponibles (sources SVG dans design/illustrations). */
object Gestures {
    val CprAdult: DrawableResource get() = Res.drawable.cpr_adult
    val CprChild: DrawableResource get() = Res.drawable.cpr_child
    val CprInfant: DrawableResource get() = Res.drawable.cpr_infant
    val ChokingBackBlows: DrawableResource get() = Res.drawable.choking_back_blows
    val ChokingHeimlich: DrawableResource get() = Res.drawable.choking_heimlich
    val ChokingInfant: DrawableResource get() = Res.drawable.choking_infant
    val BleedingPressure: DrawableResource get() = Res.drawable.bleeding_pressure
    val RecoveryPosition: DrawableResource get() = Res.drawable.recovery_position
}

/**
 * Schéma d'un geste de secours : pleine largeur, ratio conservé, coins arrondis.
 * [description] est lue par les lecteurs d'écran (déjà traduite via tr()).
 * [aspectRatio] vaut 1,6 (320x200) ; 3,2 pour la planche PLS (640x200).
 */
@Composable
fun GestureIllustration(
    drawable: DrawableResource,
    description: String,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1.6f,
) {
    Image(
        painter = painterResource(drawable),
        contentDescription = description,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF181B1D))
            .aspectRatio(aspectRatio),
    )
}


