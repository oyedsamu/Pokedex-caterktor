package com.mocoding.pokedex.data.repository

import com.mocoding.pokedex.core.model.MoveInfo
import com.mocoding.pokedex.core.model.MoveSummary

interface MovesRepository {
    suspend fun getMoveList(page: Long): Result<List<MoveSummary>>
    suspend fun getMoveByName(name: String): Result<MoveInfo>
}
