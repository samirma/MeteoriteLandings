package com.antonio.samir.meteoritelandingsspots.data.location

import android.location.Location

/**
 * GPS location as a data source, per the architecture guide's "GPS location providers" — nothing
 * above the data layer talks to the location APIs directly.
 */
interface LocationRepository {

    /**
     * A single fix for "sort meteorites near me".
     *
     * Returns null when permission is missing, location is switched off, or no fix can be
     * obtained — the list falls back to alphabetical ordering in all three cases.
     */
    suspend fun currentLocation(): Location?
}
