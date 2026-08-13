package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.remote.model.MeteoriteLandingsResponse
import retrofit2.http.GET

/**
 * NASA's open-data endpoint for the meteorite landings dataset.
 *
 * The export is a single document rather than a paginated collection, so there is no offset or
 * limit to pass — see [MeteoriteLandingsResponse] for why the old Socrata API is gone. NASA's
 * portal no longer offers a queryable API for this dataset on any host: CKAN's DataStore
 * extension is installed but unpopulated portal-wide, so `datastore_search` 404s for every
 * resource.
 *
 * The path addresses the file by its **CKAN resource UUID** rather than by its storage location.
 * CKAN answers with a 302 to wherever the file currently lives
 * (`/docs/legacy/meteorite_landings/Meteorite_Landings.json` today), so if NASA relocates it the
 * redirect follows while a hardcoded path would break. OkHttp follows the redirect, and the
 * eventual target is a presigned S3 URL — signed for GET only, so never probe it with HEAD.
 *
 * Do not be tempted by `docs/legacy/meteorite_landings/gh4g-9sfh.json`: it is object-shaped with
 * exactly the field names this app wants, but it holds only 1,000 of the 45,716 records — a
 * frozen Socrata first page that would look like a successful sync.
 */
interface NasaServerEndPoint {

    @GET("dataset/$DATASET_ID/resource/$JSON_RESOURCE_ID/download")
    suspend fun meteoriteLandings(): MeteoriteLandingsResponse

    companion object {
        const val URL = "https://data.nasa.gov/"
    }
}

private const val DATASET_ID = "b2dd98a1-4489-4e04-8618-25908cd5face"
private const val JSON_RESOURCE_ID = "5d286d0c-88c0-4ed7-a8a7-5988098c762b"
