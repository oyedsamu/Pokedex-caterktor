@file:OptIn(ExperimentalCaterktor::class)

package com.mocoding.pokedex.core.network.client

import com.mocoding.pokedex.core.model.PokemonInfo
import com.mocoding.pokedex.core.network.NetworkConstants
import com.mocoding.pokedex.core.network.model.PokemonResponse
import io.github.oyedsamu.caterktor.CaterKtorBuilder
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.serialization.json.KotlinxJsonConverter
import io.github.oyedsamu.caterktor.testing.FakeNetworkClient
import io.github.oyedsamu.caterktor.testing.jsonResponse
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class PokemonClientTest {

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
    fun getPokemonList_requests_expected_url_and_decodes_response() = runTest {
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "count": 1302,
                          "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
                          "previous": null,
                          "results": [
                            {
                              "name": "bulbasaur",
                              "url": "https://pokeapi.co/api/v2/pokemon/1/"
                            }
                          ]
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = PokemonClient(networkClient = networkClient.client)

        val result: PokemonResponse = client.getPokemonList(page = 2)

        assertEquals(1302, result.count)
        assertEquals("bulbasaur", result.results.single().name)
        assertEquals(
            "${NetworkConstants.baseUrl}pokemon?limit=20&offset=40",
            networkClient.requests.single().url
        )
    }

    @Test
    fun getPokemonByName_expands_path_parameter_and_decodes_response() = runTest {
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "id": 25,
                          "name": "pikachu",
                          "height": 4,
                          "weight": 60,
                          "base_experience": 112,
                          "types": [
                            {
                              "slot": 1,
                              "type": {
                                "name": "electric"
                              }
                            }
                          ],
                          "stats": [
                            {
                              "base_stat": 35,
                              "stat": {
                                "name": "hp"
                              }
                            }
                          ],
                          "ignored_by_lenient_json": true
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = PokemonClient(networkClient = networkClient.client)

        val result: PokemonInfo = client.getPokemonByName(name = "pikachu")

        assertEquals("pikachu", result.name)
        assertEquals("electric", result.types.single().type.name)
        assertEquals(
            "${NetworkConstants.baseUrl}pokemon/pikachu",
            networkClient.requests.single().url
        )
    }
}
