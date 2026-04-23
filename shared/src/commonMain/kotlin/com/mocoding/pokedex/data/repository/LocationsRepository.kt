package com.mocoding.pokedex.data.repository

import com.mocoding.pokedex.core.model.LocationAreaInfo
import com.mocoding.pokedex.core.model.LocationAreaSummary

interface LocationsRepository {
    suspend fun getLocationAreaList(page: Long): Result<List<LocationAreaSummary>>
    suspend fun getLocationAreaByName(name: String): Result<LocationAreaInfo>
}
