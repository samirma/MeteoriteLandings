import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteView


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MeteoriteDetail(
    itemView: MeteoriteView,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        LineDetail(R.drawable.ic_globe, itemView.address)
        LineDetail(R.drawable.ic_weight, itemView.mass)
        LineDetail(R.drawable.ic_type, itemView.type)
        LineDetail(R.drawable.ic_crash, itemView.yearString)
    }
}

@Composable
private fun LineDetail(
    @DrawableRes icon: Int,
    label: String
) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier
                .align(CenterVertically)
                .size(40.dp)
                .padding(end = 16.dp),
        )
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            color = ExtendedTheme.colors.textPrimary
        )
    }
}


@Preview("MeteoriteDetail Dark")
@Composable
fun MeteoriteDetailDark() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = true) {
        MeteoriteDetail(
            MeteoriteView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                type = "distance $sample",
                address = "address $sample",
                mass = "mass",
                reclat = 0.0,
                reclong = 0.0
            )
        )
    }
}


@Preview("MeteoriteDetail Light")
@Composable
fun MeteoriteDetailLight() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = false) {
        MeteoriteDetail(
            MeteoriteView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                type = "distance $sample",
                address = "address $sample",
                mass = "mass",
                reclat = 0.0,
                reclong = 0.0
            )
        )
    }
}

