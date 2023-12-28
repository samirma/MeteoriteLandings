package com.antonio.samir.meteoritelandingsspots.features.list.mapper

import android.location.Location
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.getDistanceFrom
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.getLocationText
import com.antonio.samir.meteoritelandingsspots.common.mapper.MapperBase
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.yearString
import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import javax.inject.Inject

class MeteoriteViewMapper @Inject constructor() :
    MapperBase<MeteoriteViewMapper.Input, MeteoriteItemView>() {

    override suspend fun action(input: Input): MeteoriteItemView {

        val meteorite = input.meteorite

        val address = meteorite.getLocationText(
            noAddress = ""
        )

        return MeteoriteItemView(
            id = "${meteorite.id}",
            name = meteorite.name ?: "---",
            yearString = meteorite.yearString ?: "---",
            address = address,
            hasAddress = address.isNotBlank(),
            distance = meteorite.getDistanceFrom(
                input.location
            ) ?: ""
        )
    }

    data class Input(val meteorite: Meteorite, val location: Location?)

}