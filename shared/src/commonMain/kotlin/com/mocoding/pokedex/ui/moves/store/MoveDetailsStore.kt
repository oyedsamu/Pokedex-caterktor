package com.mocoding.pokedex.ui.moves.store

import com.arkivanov.mvikotlin.core.store.Store
import com.mocoding.pokedex.core.model.MoveInfo

interface MoveDetailsStore : Store<Nothing, MoveDetailsStore.State, Nothing> {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val moveInfo: MoveInfo? = null,
    )
}
