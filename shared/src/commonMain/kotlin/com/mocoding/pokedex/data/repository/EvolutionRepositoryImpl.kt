package com.mocoding.pokedex.data.repository

import com.mocoding.pokedex.core.model.EvolutionInfo
import com.mocoding.pokedex.core.network.client.EvolutionClient
import com.mocoding.pokedex.core.network.model.EvolutionChainResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EvolutionRepositoryImpl : EvolutionRepository, KoinComponent {
    private val evolutionClient by inject<EvolutionClient>()

    override suspend fun getEvolutionInfoByPokemonName(name: String): Result<EvolutionInfo> = runCatching {
        val species = evolutionClient.getPokemonSpeciesByName(name)
        val chain = evolutionClient.getEvolutionChainByUrl(species.evolutionChain.url)
        val nodes = mutableListOf<EvolutionInfo.EvolutionNode>()
        flattenChain(chain.chain, 0, nodes)

        EvolutionInfo(
            pokemonName = name,
            rootName = chain.chain.species.name,
            previousEvolutionName = species.evolvesFromSpecies?.name,
            nodes = nodes,
        )
    }

    private fun flattenChain(
        link: EvolutionChainResponse.ChainLink,
        stage: Int,
        nodes: MutableList<EvolutionInfo.EvolutionNode>,
    ) {
        nodes += EvolutionInfo.EvolutionNode(
            pokemonName = link.species.name,
            stage = stage,
            isBaby = link.isBaby,
            conditions = link.evolutionDetails.map { detail ->
                EvolutionInfo.EvolutionCondition(
                    trigger = detail.trigger.name,
                    item = detail.item?.name,
                    heldItem = detail.heldItem?.name,
                    minLevel = detail.minLevel,
                    minHappiness = detail.minHappiness,
                    minBeauty = detail.minBeauty,
                    minAffection = detail.minAffection,
                    location = detail.location?.name,
                    timeOfDay = detail.timeOfDay,
                    knownMove = detail.knownMove?.name,
                    knownMoveType = detail.knownMoveType?.name,
                    partySpecies = detail.partySpecies?.name,
                    partyType = detail.partyType?.name,
                    tradeSpecies = detail.tradeSpecies?.name,
                    needsOverworldRain = detail.needsOverworldRain,
                    turnUpsideDown = detail.turnUpsideDown,
                    gender = detail.gender,
                    relativePhysicalStats = detail.relativePhysicalStats,
                )
            },
            evolvesTo = link.evolvesTo.map { it.species.name },
        )

        link.evolvesTo.forEach { next ->
            flattenChain(next, stage + 1, nodes)
        }
    }
}
