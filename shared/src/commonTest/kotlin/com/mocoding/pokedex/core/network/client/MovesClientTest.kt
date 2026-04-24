@file:OptIn(ExperimentalCaterktor::class)

package com.mocoding.pokedex.core.network.client

import com.mocoding.pokedex.core.network.NetworkConstants
import com.mocoding.pokedex.core.network.model.MoveResponse
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

class MovesClientTest {

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
    fun getMoveList_requests_expected_url_and_decodes_response() = runTest {
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "count": 920,
                          "next": "https://pokeapi.co/api/v2/move?offset=60&limit=20",
                          "previous": "https://pokeapi.co/api/v2/move?offset=20&limit=20",
                          "results": [
                            {
                              "name": "pound",
                              "url": "https://pokeapi.co/api/v2/move/1/"
                            }
                          ]
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = MovesClient(networkClient = networkClient.client)

        val result: NamedApiResourceListResponse = client.getMoveList(page = 2)

        assertEquals(920, result.count)
        assertEquals("pound", result.results.single().name)
        assertEquals(
            "${NetworkConstants.baseUrl}move?limit=20&offset=40",
            networkClient.requests.single().url
        )
    }

    @Test
    fun getMoveByName_expands_path_parameter_and_decodes_response() = runTest {
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "id": 1,
                          "name": "pound",
                          "accuracy": 100,
                          "effect_chance": null,
                          "pp": 35,
                          "priority": 0,
                          "power": 40,
                          "damage_class": {
                            "name": "physical",
                            "url": "https://pokeapi.co/api/v2/move-damage-class/2/"
                          },
                          "type": {
                            "name": "normal",
                            "url": "https://pokeapi.co/api/v2/type/1/"
                          },
                          "target": {
                            "name": "selected-pokemon",
                            "url": "https://pokeapi.co/api/v2/move-target/10/"
                          },
                          "effect_entries": [
                            {
                              "effect": "Inflicts regular damage.",
                              "short_effect": "Inflicts regular damage with no additional effect.",
                              "language": {
                                "name": "en",
                                "url": "https://pokeapi.co/api/v2/language/9/"
                              }
                            }
                          ],
                          "flavor_text_entries": []
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = MovesClient(networkClient = networkClient.client)

        val result: MoveResponse = client.getMoveByName(name = "pound")

        assertEquals("pound", result.name)
        assertEquals(1L, result.id)
        assertEquals("physical", result.damageClass.name)
        assertEquals(
            "${NetworkConstants.baseUrl}move/pound",
            networkClient.requests.single().url
        )
    }
}
