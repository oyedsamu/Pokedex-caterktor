package com.mocoding.pokedex.ui.watch.store

import com.arkivanov.mvikotlin.core.store.Store
import com.mocoding.pokedex.core.model.Video

interface WatchDetailsStore : Store<Nothing, WatchDetailsStore.State, Nothing> {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val video: Video? = null,
    )
}
