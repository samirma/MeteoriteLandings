package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeteoriteItemView(
    val id: String,
    val name: String,
    val yearString: String,
    val address: String = "",
    val distance: String,
    val isSelected: Boolean = false,
    val hasAddress: Boolean = true,
) : Parcelable

@Composable
fun MeteoriteItem(
    itemView: MeteoriteItemView,
    onItemClick: ((itemView: MeteoriteItemView) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(horizontal = 16.dp)
            .clickable {
                onItemClick?.invoke(itemView)
            },
        verticalAlignment = CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_map),
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center, // this will distribute the space evenly between the children
        ) {
            MainLabel(
                modifier = Modifier
                    .fillMaxWidth(),
                itemView = itemView
            )
            if (itemView.hasAddress) {
                Text(
                    color = ExtendedTheme.colors.textSecondary,
                    text = itemView.address,
                    maxLines = 2,
                    style = ExtendedTheme.typography.body2
                )
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .background(color = ExtendedTheme.colors.divider)
                    .padding(horizontal = 200.dp)
            )
        }
    }
}

@Composable
private fun MainLabel(
    modifier: Modifier,
    itemView: MeteoriteItemView
) {
    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically
    ) {
        Text(
            color = ExtendedTheme.colors.textPrimary,
            text = itemView.name,
            modifier = Modifier
                .wrapContentHeight(CenterVertically)
                .weight(1f),
            style = ExtendedTheme.typography.subtitle1
        )
        Text(
            color = ExtendedTheme.colors.textSecondary,
            text = itemView.distance,
            maxLines = 2,
            modifier = Modifier
                .padding(end = 4.dp),
            style = ExtendedTheme.typography.overline
        )
    }
}


@Preview("MeteoriteItem Dark")
@Composable
fun MeteoriteItemPreviewDark() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = true) {
        MeteoriteItem(
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


@Preview("MeteoriteItem Light")
@Composable
fun MeteoriteItemPreviewLight() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = false) {
        MeteoriteItem(
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

@Preview("MeteoriteItem Light no Adress")
@Composable
fun MeteoriteItemPreviewLightNoAdress() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = false) {
        MeteoriteItem(
            MeteoriteItemView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                distance = "distance $sample",
                address = "address $sample",
                hasAddress = false
            )
        ) {}
    }
}

