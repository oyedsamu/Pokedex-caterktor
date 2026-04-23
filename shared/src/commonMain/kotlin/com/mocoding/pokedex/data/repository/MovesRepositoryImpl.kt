package com.mocoding.pokedex.data.repository

import com.mocoding.pokedex.core.model.MoveInfo
import com.mocoding.pokedex.core.model.MoveSummary
import com.mocoding.pokedex.core.network.client.MovesClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MovesRepositoryImpl : MovesRepository, KoinComponent {
    private val movesClient by inject<MovesClient>()

    override suspend fun getMoveList(page: Long): Result<List<MoveSummary>> = runCatching {
        movesClient.getMoveList(page).results.map { MoveSummary(it.name, it.url) }
    }

    override suspend fun getMoveByName(name: String): Result<MoveInfo> = runCatching {
        val move = movesClient.getMoveByName(name)
        MoveInfo(
            id = move.id,
            name = move.name,
            accuracy = move.accuracy,
            effectChance = move.effectChance,
            pp = move.pp,
            priority = move.priority,
            power = move.power,
            damageClass = move.damageClass.name,
            type = move.type.name,
            target = move.target.name,
            effect = move.effectEntries.firstOrNull { it.language.name == English }?.shortEffect,
            flavorText = move.flavorTextEntries.firstOrNull { it.language.name == English }?.flavorText
                ?.replace('\n', ' ')
                ?.replace('\u000c', ' ')
        )
    }

    private companion object {
        const val English = "en"
    }
}
