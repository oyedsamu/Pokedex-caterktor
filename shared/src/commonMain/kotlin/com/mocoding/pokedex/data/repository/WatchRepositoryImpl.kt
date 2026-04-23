package com.mocoding.pokedex.data.repository

import com.mocoding.pokedex.core.model.Video

class WatchRepositoryImpl : WatchRepository {
    override suspend fun getWatchList(): Result<List<Video>> =
        Result.success(Video.demoList)

    override suspend fun getWatchById(id: String): Result<Video> =
        Video.demoList.firstOrNull { it.id == id }?.let { Result.success(it) }
            ?: Result.failure(IllegalArgumentException("Video not found"))
}
