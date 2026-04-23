package com.mocoding.pokedex.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EvolutionChainResponse(
    val id: Long,
    @SerialName("baby_trigger_item") val babyTriggerItem: NamedApiResource? = null,
    val chain: ChainLink,
) {
    @Serializable
    data class ChainLink(
        @SerialName("is_baby") val isBaby: Boolean,
        val species: NamedApiResource,
        @SerialName("evolution_details") val evolutionDetails: List<EvolutionDetail> = emptyList(),
        @SerialName("evolves_to") val evolvesTo: List<ChainLink> = emptyList(),
    )

    @Serializable
    data class EvolutionDetail(
        val item: NamedApiResource? = null,
        val trigger: NamedApiResource,
        val gender: Int? = null,
        @SerialName("held_item") val heldItem: NamedApiResource? = null,
        @SerialName("known_move") val knownMove: NamedApiResource? = null,
        @SerialName("known_move_type") val knownMoveType: NamedApiResource? = null,
        val location: NamedApiResource? = null,
        @SerialName("min_level") val minLevel: Int? = null,
        @SerialName("min_happiness") val minHappiness: Int? = null,
        @SerialName("min_beauty") val minBeauty: Int? = null,
        @SerialName("min_affection") val minAffection: Int? = null,
        @SerialName("needs_overworld_rain") val needsOverworldRain: Boolean = false,
        @SerialName("party_species") val partySpecies: NamedApiResource? = null,
        @SerialName("party_type") val partyType: NamedApiResource? = null,
        @SerialName("relative_physical_stats") val relativePhysicalStats: Int? = null,
        @SerialName("time_of_day") val timeOfDay: String = "",
        @SerialName("trade_species") val tradeSpecies: NamedApiResource? = null,
        @SerialName("turn_upside_down") val turnUpsideDown: Boolean = false,
    )
}
