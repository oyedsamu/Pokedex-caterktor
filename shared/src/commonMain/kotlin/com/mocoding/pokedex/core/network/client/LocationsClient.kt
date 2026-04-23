package com.mocoding.pokedex.core.network.client

import com.mocoding.pokedex.core.network.helper.handleErrors
import com.mocoding.pokedex.core.network.model.LocationAreaResponse
import com.mocoding.pokedex.core.network.model.LocationResponse
import com.mocoding.pokedex.core.network.model.NamedApiResourceListResponse
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.NetworkClient
import io.github.oyedsamu.caterktor.get

@OptIn(ExperimentalCaterktor::class)
class LocationsClient(
    private val networkClient: NetworkClient,
) {
    suspend fun getLocationAreaList(page: Long): NamedApiResourceListResponse = handleErrors {
        val offset = page * PageSize
        networkClient.get("location-area?limit=$PageSize&offset=$offset")
    }

    suspend fun getLocationAreaByName(name: String): LocationAreaResponse = handleErrors {
        networkClient.get("location-area/{name}", pathParams = mapOf("name" to name))
    }

    suspend fun getLocationByUrl(url: String): LocationResponse = handleErrors {
        networkClient.get(url)
    }

    companion object {
        const val PageSize = 20
    }
}
