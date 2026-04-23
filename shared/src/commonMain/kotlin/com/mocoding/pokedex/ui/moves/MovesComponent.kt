package com.mocoding.pokedex.ui.moves

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.mocoding.pokedex.ui.moves.store.MovesStore
import com.mocoding.pokedex.ui.moves.store.MovesStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class MovesComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
) : ComponentContext by componentContext {
    private val movesStore =
        instanceKeeper.getStore {
            MovesStoreFactory(storeFactory).create()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MovesStore.State> = movesStore.stateFlow

    fun onEvent(event: MovesStore.Intent) {
        movesStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        object NavigateBack : Output()
        data class NavigateToMoveDetails(val moveName: String) : Output()
    }
}
