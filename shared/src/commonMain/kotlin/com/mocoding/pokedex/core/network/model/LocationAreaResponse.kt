package com.mocoding.pokedex.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationAreaResponse(
    val id: Long,
    val name: String,
    @SerialName("game_index") val gameIndex: Int,
    val location: NamedApiResource? = null,
    @SerialName("encounter_method_rates") val encounterMethodRates: List<EncounterMethodRate>,
    @SerialName("pokemon_encounters") val pokemonEncounters: List<PokemonEncounter>,
) {
    @Serializable
    data class EncounterMethodRate(
        @SerialName("encounter_method") val encounterMethod: NamedApiResource,
        @SerialName("version_details") val versionDetails: List<VersionDetail>,
    )

    @Serializable
    data class VersionDetail(
        val rate: Int,
        val version: NamedApiResource,
    )

    @Serializable
    data class PokemonEncounter(
        val pokemon: NamedApiResource,
        @SerialName("version_details") val versionDetails: List<PokemonVersionDetail>,
    )

    @Serializable
    data class PokemonVersionDetail(
        @SerialName("max_chance") val maxChance: Int,
        val version: NamedApiResource,
        @SerialName("encounter_details") val encounterDetails: List<EncounterDetail>,
    )

    @Serializable
    data class EncounterDetail(
        @SerialName("min_level") val minLevel: Int,
        @SerialName("max_level") val maxLevel: Int,
        val chance: Int,
        val method: NamedApiResource,
        @SerialName("condition_values") val conditionValues: List<NamedApiResource>,
    )
}
