package com.mocoding.pokedex.ui.evolutions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.mocoding.pokedex.ui.evolutions.store.EvolutionDetailsStore
import com.mocoding.pokedex.ui.evolutions.store.EvolutionDetailsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class EvolutionDetailsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    pokemonName: String,
    private val output: (Output) -> Unit,
) : ComponentContext by componentContext {
    private val store =
        instanceKeeper.getStore {
            EvolutionDetailsStoreFactory(storeFactory, pokemonName).create()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<EvolutionDetailsStore.State> = store.stateFlow

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        object NavigateBack : Output()
    }
}
