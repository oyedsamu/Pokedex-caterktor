package com.mocoding.pokedex.core.network.client

import com.mocoding.pokedex.core.network.helper.handleErrors
import com.mocoding.pokedex.core.network.model.MoveResponse
import com.mocoding.pokedex.core.network.model.NamedApiResourceListResponse
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.NetworkClient
import io.github.oyedsamu.caterktor.get

@OptIn(ExperimentalCaterktor::class)
class MovesClient(
    private val networkClient: NetworkClient,
) {
    suspend fun getMoveList(page: Long): NamedApiResourceListResponse = handleErrors {
        val offset = page * PageSize
        networkClient.get("move?limit=$PageSize&offset=$offset")
    }

    suspend fun getMoveByName(name: String): MoveResponse = handleErrors {
        networkClient.get("move/{name}", pathParams = mapOf("name" to name))
    }

    companion object {
        const val PageSize = 20
    }
}
