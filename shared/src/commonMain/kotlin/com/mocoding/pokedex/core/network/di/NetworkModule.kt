package com.mocoding.pokedex.core.network.di

import com.mocoding.pokedex.core.network.client.PokemonClient
import com.mocoding.pokedex.core.network.createNetworkClient
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalCaterktor::class)
val networkModule: (enableLogging: Boolean) -> Module get() = { enableLogging ->
    module {
        single { createNetworkClient(enableLogging) }
        single { PokemonClient(networkClient = get()) }
    }
}
