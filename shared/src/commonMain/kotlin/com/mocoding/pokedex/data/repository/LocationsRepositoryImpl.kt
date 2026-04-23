package com.mocoding.pokedex.data.repository

import com.mocoding.pokedex.core.model.LocationAreaInfo
import com.mocoding.pokedex.core.model.LocationAreaSummary
import com.mocoding.pokedex.core.network.client.LocationsClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationsRepositoryImpl : LocationsRepository, KoinComponent {
    private val locationsClient by inject<LocationsClient>()

    override suspend fun getLocationAreaList(page: Long): Result<List<LocationAreaSummary>> = runCatching {
        locationsClient.getLocationAreaList(page).results.map { LocationAreaSummary(it.name, it.url) }
    }

    override suspend fun getLocationAreaByName(name: String): Result<LocationAreaInfo> = runCatching {
        val area = locationsClient.getLocationAreaByName(name)
        val location = area.location?.url?.let { locationsClient.getLocationByUrl(it) }
        LocationAreaInfo(
            id = area.id,
            name = area.name,
            gameIndex = area.gameIndex,
            locationName = location?.name ?: area.location?.name,
            regionName = location?.region?.name,
            areaNames = location?.areas?.map { it.name } ?: emptyList(),
            encounterMethods = area.encounterMethodRates.map { method ->
                LocationAreaInfo.EncounterMethodRate(
                    name = method.encounterMethod.name,
                    versionRates = method.versionDetails.map { detail ->
                        LocationAreaInfo.VersionRate(
                            versionName = detail.version.name,
                            rate = detail.rate,
                        )
                    },
                )
            },
            pokemonEncounters = area.pokemonEncounters.map { encounter ->
                LocationAreaInfo.PokemonEncounter(
                    pokemonName = encounter.pokemon.name,
                    versions = encounter.versionDetails.map { version ->
                        LocationAreaInfo.EncounterVersion(
                            versionName = version.version.name,
                            maxChance = version.maxChance,
                            encounters = version.encounterDetails.map { detail ->
                                LocationAreaInfo.EncounterDetail(
                                    chance = detail.chance,
                                    minLevel = detail.minLevel,
                                    maxLevel = detail.maxLevel,
                                    methodName = detail.method.name,
                                    conditionValues = detail.conditionValues.map { it.name },
                                )
                            },
                        )
                    },
                )
            },
        )
    }
}
