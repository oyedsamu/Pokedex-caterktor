package com.mocoding.pokedex.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonSpeciesResponse(
    val name: String,
    @SerialName("evolves_from_species") val evolvesFromSpecies: NamedApiResource? = null,
    @SerialName("evolution_chain") val evolutionChain: EvolutionChainResource,
) {
    @Serializable
    data class EvolutionChainResource(
        val url: String,
    )
}
