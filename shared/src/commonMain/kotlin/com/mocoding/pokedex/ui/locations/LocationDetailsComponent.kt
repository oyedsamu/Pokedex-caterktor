package com.mocoding.pokedex.ui.locations

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.mocoding.pokedex.ui.locations.store.LocationDetailsStore
import com.mocoding.pokedex.ui.locations.store.LocationDetailsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class LocationDetailsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    locationAreaName: String,
    private val output: (Output) -> Unit,
) : ComponentContext by componentContext {
    private val store =
        instanceKeeper.getStore {
            LocationDetailsStoreFactory(storeFactory, locationAreaName).create()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<LocationDetailsStore.State> = store.stateFlow

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        object NavigateBack : Output()
    }
}
