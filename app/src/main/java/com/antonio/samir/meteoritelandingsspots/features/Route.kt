package com.antonio.samir.meteoritelandingsspots.features

object Route {

    // Define the route for the list screen.
    const val LIST = "main"

    // Define the template for the detail screen route.
    private const val DETAIL_TEMPLATE = "detail/%s"

    // Define the argument key for the app ID.
    const val METEORITE_ID_ARG = "id"

    // Define the route for the detail screen.
    val DETAIL = String.format(DETAIL_TEMPLATE, "{$METEORITE_ID_ARG}")

    // Function to get the detail screen URL by ID.
    fun getDetailUrlById(appID: String) = String.format(DETAIL_TEMPLATE, appID)
}