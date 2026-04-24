package com.mocoding.pokedex.data.repository

import com.mocoding.pokedex.core.model.EvolutionInfo

interface EvolutionRepository {
    suspend fun getEvolutionInfoByPokemonName(name: String): Result<EvolutionInfo>
}
