package com.mocoding.pokedex.ui.moves.store

import com.arkivanov.mvikotlin.core.store.Store
import com.mocoding.pokedex.core.model.MoveSummary

interface MovesStore : Store<MovesStore.Intent, MovesStore.State, Nothing> {
    sealed class Intent {
        data class LoadMovesByPage(val page: Long) : Intent()
        data class UpdateSearchValue(val searchValue: String) : Intent()
    }

    data class State(
        val isLoading: Boolean = false,
        val isLastPageLoaded: Boolean = false,
        val error: String? = null,
        val moveList: List<MoveSummary> = emptyList(),
        val searchValue: String = "",
    )
}
