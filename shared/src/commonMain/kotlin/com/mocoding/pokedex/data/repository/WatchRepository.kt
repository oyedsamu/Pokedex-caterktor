package com.mocoding.pokedex.data.repository

import com.mocoding.pokedex.core.model.Video

interface WatchRepository {
    suspend fun getWatchList(): Result<List<Video>>
    suspend fun getWatchById(id: String): Result<Video>
}
