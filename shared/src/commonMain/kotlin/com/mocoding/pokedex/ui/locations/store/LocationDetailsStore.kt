package com.mocoding.pokedex.ui.locations.store

import com.arkivanov.mvikotlin.core.store.Store
import com.mocoding.pokedex.core.model.LocationAreaInfo

interface LocationDetailsStore : Store<Nothing, LocationDetailsStore.State, Nothing> {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val locationAreaInfo: LocationAreaInfo? = null,
    )
}
