package com.mocoding.pokedex.core.network.helper

import com.mocoding.pokedex.core.network.errors.PokedexError
import com.mocoding.pokedex.core.network.errors.PokedexException
import io.github.oyedsamu.caterktor.Headers
import io.github.oyedsamu.caterktor.HttpStatus
import io.github.oyedsamu.caterktor.NetworkError
import io.github.oyedsamu.caterktor.NetworkResult
import io.github.oyedsamu.caterktor.SerializationPhase
import io.github.oyedsamu.caterktor.TimeoutKind
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ErrorHandlerTest {

    @Test
    fun handleErrors_returns_body_for_success() = runTest {
        val result = handleErrors {
            NetworkResult.Success(
                body = "ok",
                status = HttpStatus.OK,
                headers = Headers.Empty,
                durationMs = 1L,
                attempts = 1,
                requestId = "request-1"
            )
        }

        assertEquals("ok", result)
    }

    @Test
    fun handleErrors_maps_http_client_errors() = runTest {
        val exception = assertFailsWith<PokedexException> {
            handleErrors<String> {
                NetworkResult.Failure(
                    error = NetworkError.Http(
                        status = HttpStatus.NotFound,
                        headers = Headers.Empty,
                        body = io.github.oyedsamu.caterktor.ErrorBody.Empty
                    ),
                    durationMs = 1L,
                    attempts = 1,
                    requestId = "request-2"
                )
            }
        }

        assertEquals(PokedexError.ClientError, exception.error)
    }

    @Test
    fun handleErrors_maps_timeouts_to_service_unavailable() = runTest {
        val exception = assertFailsWith<PokedexException> {
            handleErrors<String> {
                NetworkResult.Failure(
                    error = NetworkError.Timeout(kind = TimeoutKind.Request),
                    durationMs = 1L,
                    attempts = 1,
                    requestId = "request-3"
                )
            }
        }

        assertEquals(PokedexError.ServiceUnavailable, exception.error)
    }

    @Test
    fun handleErrors_maps_serialization_failures_to_server_error() = runTest {
        val exception = assertFailsWith<PokedexException> {
            handleErrors<String> {
                NetworkResult.Failure(
                    error = NetworkError.Serialization(
                        phase = SerializationPhase.Decoding,
                        cause = IllegalStateException("bad payload")
                    ),
                    durationMs = 1L,
                    attempts = 1,
                    requestId = "request-4"
                )
            }
        }

        assertEquals(PokedexError.ServerError, exception.error)
    }

    @Test
    fun handleErrors_propagates_cancellation() = runTest {
        assertFailsWith<CancellationException> {
            handleErrors<String> {
                NetworkResult.Failure(
                    error = NetworkError.Unknown(CancellationException("cancelled")),
                    durationMs = 1L,
                    attempts = 1,
                    requestId = "request-5"
                )
            }
        }
    }
}
