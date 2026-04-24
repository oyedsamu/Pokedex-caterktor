package com.mocoding.pokedex.core.network.client

import com.mocoding.pokedex.core.network.helper.handleErrors
import com.mocoding.pokedex.core.network.model.EvolutionChainResponse
import com.mocoding.pokedex.core.network.model.PokemonSpeciesResponse
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.NetworkClient
import io.github.oyedsamu.caterktor.get

@OptIn(ExperimentalCaterktor::class)
class EvolutionClient(
    private val networkClient: NetworkClient,
) {
    suspend fun getPokemonSpeciesByName(name: String): PokemonSpeciesResponse = handleErrors {
        networkClient.get("pokemon-species/{name}", pathParams = mapOf("name" to name))
    }

    suspend fun getEvolutionChainByUrl(url: String): EvolutionChainResponse = handleErrors {
        networkClient.get(url)
    }
}
