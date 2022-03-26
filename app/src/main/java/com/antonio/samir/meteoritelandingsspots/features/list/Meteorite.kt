import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MeteoriteCell(
    itemView: MeteoriteItemView,
    onItemClick: ((itemView: MeteoriteItemView) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .height(84.dp)
            .padding(horizontal = 16.dp)
            .clickable {
                onItemClick?.invoke(itemView)
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_map),
            contentDescription = "",
            modifier = Modifier
                .align(CenterVertically)
                .size(40.dp)
        )
        Box(
            Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)

            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        verticalAlignment = CenterVertically
                    ) {
                        Text(
                            color = ExtendedTheme.colors.textPrimary,
                            text = itemView.name ?: "",
                            modifier = Modifier.wrapContentHeight(CenterVertically),
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            color = ExtendedTheme.colors.textSecondary,
                            text = itemView.distance ?: "800m",
                            maxLines = 2,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                                .wrapContentWidth(Alignment.End),
                            style = MaterialTheme.typography.overline
                        )
                    }
                    Text(
                        color = ExtendedTheme.colors.textSecondary,
                        text = itemView.address ?: "",
                        maxLines = 2,
                        style = MaterialTheme.typography.body2
                    )
                }

            }
            Image(
                painter = painterResource(id = R.drawable.divider),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 1.dp)
            )
        }
    }

}


@Preview("MeteoriteCell Dark")
@Composable
fun MeteoriteCellPreviewDark() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = true) {
        MeteoriteCell(
            MeteoriteItemView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                distance = "distance $sample",
                address = "address $sample"
            )
        ) {}
    }
}


@Preview("MeteoriteCell Light")
@Composable
fun MeteoriteCellPreviewLight() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = false) {
        MeteoriteCell(
            MeteoriteItemView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                distance = "distance $sample",
                address = "address $sample"
            )
        ) {}
    }
}

