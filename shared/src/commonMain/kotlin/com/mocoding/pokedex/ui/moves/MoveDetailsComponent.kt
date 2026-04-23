package com.mocoding.pokedex.ui.moves

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.mocoding.pokedex.ui.moves.store.MoveDetailsStore
import com.mocoding.pokedex.ui.moves.store.MoveDetailsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class MoveDetailsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    moveName: String,
    private val output: (Output) -> Unit,
) : ComponentContext by componentContext {
    private val moveDetailsStore =
        instanceKeeper.getStore {
            MoveDetailsStoreFactory(storeFactory, moveName).create()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MoveDetailsStore.State> = moveDetailsStore.stateFlow

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        object NavigateBack : Output()
    }
}
