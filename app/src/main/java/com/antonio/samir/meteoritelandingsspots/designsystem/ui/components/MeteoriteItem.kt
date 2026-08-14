package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

@Immutable
data class MeteoriteItemView(
    val id: Int,
    val name: String,
    val yearString: String,
    val address: String = "",
    val distance: String = "",
) {
    val hasAddress: Boolean get() = address.isNotBlank()
    val hasDistance: Boolean get() = distance.isNotBlank()
}

@Composable
fun MeteoriteItem(
    itemView: MeteoriteItemView,
    modifier: Modifier = Modifier,
    onItemClick: (itemView: MeteoriteItemView) -> Unit = {},
) {
    // One description for the whole row: TalkBack should read "Aachen, 1880, Germany, 12 km"
    // rather than stopping on four separate nodes.
    val description = buildList {
        add(itemView.name)
        add(itemView.yearString)
        if (itemView.hasAddress) add(itemView.address)
        if (itemView.hasDistance) add(itemView.distance)
    }.joinToString(", ")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onItemClick(itemView) }
            .clearAndSetSemantics { contentDescription = description },
    ) {
        Row(
            // defaultMinSize rather than a fixed height, so two-line addresses and large font
            // scales grow the row instead of clipping.
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 72.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Image, not Icon: these are multi-colour vectors and a tint flattens them into a
            // single-colour blob.
            Image(
                painter = painterResource(id = R.drawable.ic_map),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = itemView.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (itemView.hasAddress) {
                    Text(
                        text = itemView.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            // Distance above year in one right-aligned column: side by side they collide on
            // narrow screens once both are present.
            Column(horizontalAlignment = Alignment.End) {
                if (itemView.hasDistance) {
                    Text(
                        text = itemView.distance,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                    )
                }
                Text(
                    text = itemView.yearString,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

private val sampleItem = MeteoriteItemView(
    id = 1,
    name = "Aachen",
    yearString = "1880",
    address = "Aachen, Nordrhein-Westfalen, Germany",
    distance = "512 km",
)

@PreviewLightDark
@Composable
private fun MeteoriteItemPreview() {
    MeteoriteLandingsTheme {
        MeteoriteItem(sampleItem)
    }
}

@PreviewFontScale
@Composable
private fun MeteoriteItemFontScalePreview() {
    MeteoriteLandingsTheme {
        MeteoriteItem(sampleItem)
    }
}

@Preview(name = "No address")
@Composable
private fun MeteoriteItemNoAddressPreview() {
    MeteoriteLandingsTheme {
        MeteoriteItem(sampleItem.copy(address = "", distance = ""))
    }
}
