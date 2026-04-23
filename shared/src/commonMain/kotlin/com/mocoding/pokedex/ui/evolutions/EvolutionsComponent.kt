package com.mocoding.pokedex.ui.evolutions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.mocoding.pokedex.ui.evolutions.store.EvolutionsStore
import com.mocoding.pokedex.ui.evolutions.store.EvolutionsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class EvolutionsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
) : ComponentContext by componentContext {
    private val store = instanceKeeper.getStore { EvolutionsStoreFactory(storeFactory).create() }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<EvolutionsStore.State> = store.stateFlow

    fun onEvent(event: EvolutionsStore.Intent) {
        store.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        object NavigateBack : Output()
        data class NavigateToEvolutionDetails(val pokemonName: String) : Output()
    }
}
