package com.mocoding.pokedex.data.di

import com.mocoding.pokedex.data.repository.EvolutionRepository
import com.mocoding.pokedex.data.repository.EvolutionRepositoryImpl
import com.mocoding.pokedex.data.repository.LocationsRepository
import com.mocoding.pokedex.data.repository.LocationsRepositoryImpl
import com.mocoding.pokedex.data.repository.MovesRepository
import com.mocoding.pokedex.data.repository.MovesRepositoryImpl
import com.mocoding.pokedex.data.repository.PokemonRepository
import com.mocoding.pokedex.data.repository.PokemonRepositoryImpl
import com.mocoding.pokedex.data.repository.WatchRepository
import com.mocoding.pokedex.data.repository.WatchRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single<PokemonRepository> { PokemonRepositoryImpl() }
    single<MovesRepository> { MovesRepositoryImpl() }
    single<EvolutionRepository> { EvolutionRepositoryImpl() }
    single<LocationsRepository> { LocationsRepositoryImpl() }
    single<WatchRepository> { WatchRepositoryImpl() }
}
