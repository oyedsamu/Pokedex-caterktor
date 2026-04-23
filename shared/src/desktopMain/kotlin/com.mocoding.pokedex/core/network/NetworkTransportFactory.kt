package com.mocoding.pokedex.core.network

import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.Transport
import io.github.oyedsamu.caterktor.engine.cio.CioTransport

@OptIn(ExperimentalCaterktor::class)
actual fun createPlatformTransport(): Transport {
    return CioTransport()
}
