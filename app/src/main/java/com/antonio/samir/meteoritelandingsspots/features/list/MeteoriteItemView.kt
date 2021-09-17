package com.antonio.samir.meteoritelandingsspots.features.list

data class MeteoriteItemView(
    val id: Int,
    val name: String? = null,
    val yearString: String? = null,
    val address: String? = null,
    val reclat: String? = null,
    val reclong: String? = null
)