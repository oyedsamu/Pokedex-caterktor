package com.mocoding.pokedex.core.network.helper

import com.mocoding.pokedex.core.network.errors.PokedexError
import com.mocoding.pokedex.core.network.errors.PokedexException
import com.mocoding.pokedex.pokedexDispatchers
import io.github.oyedsamu.caterktor.NetworkError
import io.github.oyedsamu.caterktor.NetworkResult
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

@Suppress("CyclomaticComplexMethod")
suspend fun <T> handleErrors(
    response: suspend () -> NetworkResult<T>
): T = withContext(pokedexDispatchers.io) {
    when (val result = response()) {
        is NetworkResult.Success -> result.body
        is NetworkResult.Failure -> throw result.error.toPokedexException()
    }
}

private fun NetworkError.toPokedexException(): PokedexException {
    val error = when (this) {
        is NetworkError.ConnectionFailed,
        is NetworkError.Timeout -> PokedexError.ServiceUnavailable
        is NetworkError.Http -> when {
            status.isClientError -> PokedexError.ClientError
            status.isServerError -> PokedexError.ServerError
            else -> PokedexError.UnknownError
        }
        is NetworkError.Serialization -> PokedexError.ServerError
        is NetworkError.Protocol -> PokedexError.ServerError
        is NetworkError.CircuitOpen -> PokedexError.ServiceUnavailable
        is NetworkError.Unknown -> when (cause) {
            is CancellationException -> throw cause
            else -> PokedexError.UnknownError
        }
    }

    return PokedexException(error)
}
