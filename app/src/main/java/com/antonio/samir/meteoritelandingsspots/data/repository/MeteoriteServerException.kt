package com.antonio.samir.meteoritelandingsspots.data.repository

/**
 * Server Exception created in order to prevent the ui layer be aware the exception of service layer
 */
class MeteoriteServerException(exception: Exception) : Exception(exception)
