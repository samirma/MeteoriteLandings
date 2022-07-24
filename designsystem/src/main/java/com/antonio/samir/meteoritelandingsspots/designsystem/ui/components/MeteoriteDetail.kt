import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Shimmer
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme


data class MeteoriteView(
    val id: String?,
    val name: String,
    val yearString: String,
    val address: String,
    val type: String,
    val mass: String,
    val reclat: Double,
    val reclong: Double,
    val hasAddress: Boolean = true
)

@Composable
fun MeteoriteDetail(
    meteoriteView: MeteoriteView,
    darkTheme: Boolean
) {
    MeteoriteLandingsTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                LineDetail(
                    icon = R.drawable.ic_globe,
                    label = meteoriteView.address,
                    showShimmer = !meteoriteView.hasAddress
                )
                LineDetail(R.drawable.ic_weight, meteoriteView.mass)
                LineDetail(R.drawable.ic_type, meteoriteView.type)
                LineDetail(R.drawable.ic_crash, meteoriteView.yearString)
            }
        }
    }
}

@Composable
private fun LineDetail(
    @DrawableRes icon: Int,
    label: String,
    showShimmer: Boolean = false
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
        if (showShimmer) {
            Shimmer(
                modifier = Modifier
                    .align(CenterVertically)
            )
        } else {
            Text(
                text = label,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body2,
                color = ExtendedTheme.colors.textPrimary
            )
        }
    }
}


@Preview("MeteoriteDetail Dark")
@Composable
fun MeteoriteDetailDark() {
    val sample = "test"
    MeteoriteDetail(
        meteoriteView = MeteoriteView(
            id = sample,
            name = "name $sample",
            yearString = "yearString $sample",
            type = "distance $sample",
            address = "address $sample",
            mass = "mass",
            reclat = 0.0,
            reclong = 0.0
        ),
        darkTheme = true
    )
}


@Preview("MeteoriteDetail Light")
@Composable
fun MeteoriteDetailLight() {
    val sample = "test"
    MeteoriteDetail(
        meteoriteView = MeteoriteView(
            id = sample,
            name = "name $sample",
            yearString = "yearString $sample",
            type = "distance $sample",
            address = "address $sample",
            mass = "mass",
            reclat = 0.0,
            reclong = 0.0
        ),
        darkTheme = false
    )
}


@Preview("MeteoriteDetail no Address Light")
@Composable
fun MeteoriteDetailNoAddressLight() {
    val sample = "test"
    MeteoriteDetail(
        meteoriteView = MeteoriteView(
            id = sample,
            name = "name $sample",
            yearString = "yearString $sample",
            type = "distance $sample",
            address = "address $sample",
            mass = "mass",
            reclat = 0.0,
            reclong = 0.0,
            hasAddress = false
        ),
        darkTheme = false
    )
}

