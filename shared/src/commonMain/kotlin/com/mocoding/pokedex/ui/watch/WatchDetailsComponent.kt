package com.mocoding.pokedex.ui.watch

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.mocoding.pokedex.ui.watch.store.WatchDetailsStore
import com.mocoding.pokedex.ui.watch.store.WatchDetailsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class WatchDetailsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    videoId: String,
    private val output: (Output) -> Unit,
) : ComponentContext by componentContext {
    private val store =
        instanceKeeper.getStore {
            WatchDetailsStoreFactory(storeFactory, videoId).create()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<WatchDetailsStore.State> = store.stateFlow

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        object NavigateBack : Output()
    }
}
