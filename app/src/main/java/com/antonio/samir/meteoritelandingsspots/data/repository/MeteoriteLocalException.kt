package com.antonio.samir.meteoritelandingsspots.data.repository

/**
 * Local Exception created in order to prevent the ui layer be aware the exception of service layer
 */
class MeteoriteLocalException(exception: Exception) : Exception(exception)
