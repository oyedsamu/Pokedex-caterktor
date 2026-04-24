@file:OptIn(ExperimentalCaterktor::class)

package com.mocoding.pokedex.core.network.client

import com.mocoding.pokedex.core.network.NetworkConstants
import com.mocoding.pokedex.core.network.model.LocationAreaResponse
import com.mocoding.pokedex.core.network.model.NamedApiResourceListResponse
import io.github.oyedsamu.caterktor.CaterKtorBuilder
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.serialization.json.KotlinxJsonConverter
import io.github.oyedsamu.caterktor.testing.FakeNetworkClient
import io.github.oyedsamu.caterktor.testing.jsonResponse
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationsClientTest {

    private val configureClient: CaterKtorBuilder.() -> Unit = {
        baseUrl = NetworkConstants.baseUrl
        addConverter(
            KotlinxJsonConverter(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        )
    }

    @Test
    fun getLocationAreaList_requests_expected_url_and_decodes_response() = runTest {
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "count": 1054,
                          "next": "https://pokeapi.co/api/v2/location-area?offset=60&limit=20",
                          "previous": "https://pokeapi.co/api/v2/location-area?offset=20&limit=20",
                          "results": [
                            {
                              "name": "pallet-town-area",
                              "url": "https://pokeapi.co/api/v2/location-area/1/"
                            }
                          ]
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = LocationsClient(networkClient = networkClient.client)

        val result: NamedApiResourceListResponse = client.getLocationAreaList(page = 2)

        assertEquals(1054, result.count)
        assertEquals("pallet-town-area", result.results.single().name)
        assertEquals(
            "${NetworkConstants.baseUrl}location-area?limit=20&offset=40",
            networkClient.requests.single().url
        )
    }

    @Test
    fun getLocationAreaByName_expands_path_parameter_and_decodes_response() = runTest {
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "id": 1,
                          "name": "pallet-town-area",
                          "game_index": 1,
                          "location": {
                            "name": "pallet-town",
                            "url": "https://pokeapi.co/api/v2/location/1/"
                          },
                          "encounter_method_rates": [],
                          "pokemon_encounters": []
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = LocationsClient(networkClient = networkClient.client)

        val result: LocationAreaResponse = client.getLocationAreaByName(name = "pallet-town-area")

        assertEquals("pallet-town-area", result.name)
        assertEquals(1L, result.id)
        assertEquals("pallet-town", result.location?.name)
        assertEquals(
            "${NetworkConstants.baseUrl}location-area/pallet-town-area",
            networkClient.requests.single().url
        )
    }
}
