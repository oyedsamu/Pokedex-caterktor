package com.mocoding.pokedex.ui.locations

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.mocoding.pokedex.ui.locations.store.LocationsStore
import com.mocoding.pokedex.ui.locations.store.LocationsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class LocationsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
) : ComponentContext by componentContext {
    private val store = instanceKeeper.getStore { LocationsStoreFactory(storeFactory).create() }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<LocationsStore.State> = store.stateFlow

    fun onEvent(event: LocationsStore.Intent) {
        store.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        object NavigateBack : Output()
        data class NavigateToLocationDetails(val locationAreaName: String) : Output()
    }
}
