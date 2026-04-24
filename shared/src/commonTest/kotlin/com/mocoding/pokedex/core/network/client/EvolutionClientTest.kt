@file:OptIn(ExperimentalCaterktor::class)

package com.mocoding.pokedex.core.network.client

import com.mocoding.pokedex.core.network.NetworkConstants
import com.mocoding.pokedex.core.network.model.EvolutionChainResponse
import com.mocoding.pokedex.core.network.model.PokemonSpeciesResponse
import io.github.oyedsamu.caterktor.CaterKtorBuilder
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.serialization.json.KotlinxJsonConverter
import io.github.oyedsamu.caterktor.testing.FakeNetworkClient
import io.github.oyedsamu.caterktor.testing.jsonResponse
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EvolutionClientTest {

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
    fun getPokemonSpeciesByName_expands_path_parameter_and_decodes_response() = runTest {
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "name": "pikachu",
                          "evolves_from_species": {
                            "name": "pichu",
                            "url": "https://pokeapi.co/api/v2/pokemon-species/172/"
                          },
                          "evolution_chain": {
                            "url": "https://pokeapi.co/api/v2/evolution-chain/10/"
                          }
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = EvolutionClient(networkClient = networkClient.client)

        val result: PokemonSpeciesResponse = client.getPokemonSpeciesByName(name = "pikachu")

        assertEquals("pikachu", result.name)
        assertEquals("pichu", result.evolvesFromSpecies?.name)
        assertEquals("https://pokeapi.co/api/v2/evolution-chain/10/", result.evolutionChain.url)
        assertEquals(
            "${NetworkConstants.baseUrl}pokemon-species/pikachu",
            networkClient.requests.single().url
        )
    }

    @Test
    fun getPokemonSpeciesByName_handles_missing_evolves_from_species() = runTest {
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "name": "bulbasaur",
                          "evolution_chain": {
                            "url": "https://pokeapi.co/api/v2/evolution-chain/1/"
                          }
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = EvolutionClient(networkClient = networkClient.client)

        val result: PokemonSpeciesResponse = client.getPokemonSpeciesByName(name = "bulbasaur")

        assertEquals("bulbasaur", result.name)
        assertNull(result.evolvesFromSpecies)
    }

    @Test
    fun getEvolutionChainByUrl_uses_absolute_url_and_decodes_response() = runTest {
        val absoluteUrl = "https://pokeapi.co/api/v2/evolution-chain/10/"
        val networkClient = FakeNetworkClient(configure = configureClient)
            .apply {
                enqueue(
                    jsonResponse(
                        """
                        {
                          "id": 10,
                          "baby_trigger_item": null,
                          "chain": {
                            "is_baby": false,
                            "species": {
                              "name": "pichu",
                              "url": "https://pokeapi.co/api/v2/pokemon-species/172/"
                            },
                            "evolution_details": [],
                            "evolves_to": [
                              {
                                "is_baby": false,
                                "species": {
                                  "name": "pikachu",
                                  "url": "https://pokeapi.co/api/v2/pokemon-species/25/"
                                },
                                "evolution_details": [
                                  {
                                    "trigger": {
                                      "name": "level-up",
                                      "url": "https://pokeapi.co/api/v2/evolution-trigger/1/"
                                    },
                                    "min_level": 5
                                  }
                                ],
                                "evolves_to": []
                              }
                            ]
                          }
                        }
                        """.trimIndent()
                    )
                )
            }

        val client = EvolutionClient(networkClient = networkClient.client)

        val result: EvolutionChainResponse = client.getEvolutionChainByUrl(url = absoluteUrl)

        assertEquals(10L, result.id)
        assertEquals("pichu", result.chain.species.name)
        assertEquals("pikachu", result.chain.evolvesTo.single().species.name)
        assertEquals(absoluteUrl, networkClient.requests.single().url)
    }
}
