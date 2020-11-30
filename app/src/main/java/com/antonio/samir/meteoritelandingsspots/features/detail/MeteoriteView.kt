package com.antonio.samir.meteoritelandingsspots.features.detail

data class MeteoriteView(
        val id: String?,
        val name: String?,
        val yearString: String?,
        val address: String?,
        val recclass: String?,
        val mass: String?,
        val reclat: Double,
        val reclong: Double,
        val hasAddress: Boolean
)