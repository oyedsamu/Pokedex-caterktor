package com.mocoding.pokedex.ui.locations.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.mocoding.pokedex.core.model.LocationAreaInfo
import com.mocoding.pokedex.data.repository.LocationsRepository
import com.mocoding.pokedex.pokedexDispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class LocationDetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val locationAreaName: String,
) : KoinComponent {
    private val locationsRepository by inject<LocationsRepository>()

    fun create(): LocationDetailsStore =
        object : LocationDetailsStore, Store<Nothing, LocationDetailsStore.State, Nothing> by storeFactory.create(
            name = "LocationDetailsStore",
            initialState = LocationDetailsStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed class Msg {
        object Loading : Msg()
        data class Loaded(val info: LocationAreaInfo) : Msg()
        data class Failed(val error: String?) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Nothing, Unit, LocationDetailsStore.State, Msg, Nothing>(
        pokedexDispatchers.main
    ) {
        override fun executeAction(action: Unit, getState: () -> LocationDetailsStore.State) {
            scope.launch {
                dispatch(Msg.Loading)
                locationsRepository.getLocationAreaByName(locationAreaName)
                    .onSuccess { dispatch(Msg.Loaded(it)) }
                    .onFailure { dispatch(Msg.Failed(it.message)) }
            }
        }
    }

    private object ReducerImpl : Reducer<LocationDetailsStore.State, Msg> {
        override fun LocationDetailsStore.State.reduce(msg: Msg): LocationDetailsStore.State =
            when (msg) {
                Msg.Loading -> copy(isLoading = true, error = null)
                is Msg.Loaded -> copy(isLoading = false, locationAreaInfo = msg.info)
                is Msg.Failed -> copy(isLoading = false, error = msg.error)
            }
    }
}
