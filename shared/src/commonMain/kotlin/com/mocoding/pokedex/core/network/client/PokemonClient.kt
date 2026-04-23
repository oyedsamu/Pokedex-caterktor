package com.mocoding.pokedex.core.network.client

import com.mocoding.pokedex.core.model.PokemonInfo
import com.mocoding.pokedex.core.network.helper.handleErrors
import com.mocoding.pokedex.core.network.model.PokemonResponse
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.NetworkClient
import io.github.oyedsamu.caterktor.get

@OptIn(ExperimentalCaterktor::class)
class PokemonClient(
    private val networkClient: NetworkClient
) {

    suspend fun getPokemonList(
        page: Long,
    ): PokemonResponse {
        return handleErrors {
            val offset = page * PageSize
            networkClient.get("pokemon?limit=$PageSize&offset=$offset")
        }
    }

    suspend fun getPokemonByName(
        name: String,
    ): PokemonInfo {
        return handleErrors {
            networkClient.get("pokemon/{name}", pathParams = mapOf("name" to name))
        }
    }

    companion object {
        private const val PageSize = 20
    }

}
