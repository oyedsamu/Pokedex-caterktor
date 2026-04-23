package com.mocoding.pokedex.core.model

data class LocationAreaInfo(
    val id: Long,
    val name: String,
    val gameIndex: Int,
    val locationName: String?,
    val regionName: String?,
    val areaNames: List<String>,
    val encounterMethods: List<EncounterMethodRate>,
    val pokemonEncounters: List<PokemonEncounter>,
) {
    data class EncounterMethodRate(
        val name: String,
        val versionRates: List<VersionRate>,
    )

    data class VersionRate(
        val versionName: String,
        val rate: Int,
    )

    data class PokemonEncounter(
        val pokemonName: String,
        val versions: List<EncounterVersion>,
    )

    data class EncounterVersion(
        val versionName: String,
        val maxChance: Int,
        val encounters: List<EncounterDetail>,
    )

    data class EncounterDetail(
        val chance: Int,
        val minLevel: Int,
        val maxLevel: Int,
        val methodName: String,
        val conditionValues: List<String>,
    )
}
