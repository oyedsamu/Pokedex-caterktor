package com.mocoding.pokedex.ui.locations.store

import com.arkivanov.mvikotlin.core.store.Store
import com.mocoding.pokedex.core.model.LocationAreaSummary

interface LocationsStore : Store<LocationsStore.Intent, LocationsStore.State, Nothing> {
    sealed class Intent {
        data class LoadLocationsByPage(val page: Long) : Intent()
        data class UpdateSearchValue(val searchValue: String) : Intent()
    }

    data class State(
        val isLoading: Boolean = false,
        val isLastPageLoaded: Boolean = false,
        val error: String? = null,
        val locations: List<LocationAreaSummary> = emptyList(),
        val searchValue: String = "",
    )
}
