package com.mocoding.pokedex.core.network

import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.Transport

@OptIn(ExperimentalCaterktor::class)
expect fun createPlatformTransport(): Transport
