package com.mocoding.pokedex.ui.evolutions.store

import com.arkivanov.mvikotlin.core.store.Store
import com.mocoding.pokedex.core.model.EvolutionInfo

interface EvolutionDetailsStore : Store<Nothing, EvolutionDetailsStore.State, Nothing> {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val evolutionInfo: EvolutionInfo? = null,
    )
}
