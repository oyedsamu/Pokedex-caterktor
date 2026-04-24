package com.mocoding.pokedex.ui.locations.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.mocoding.pokedex.core.model.LocationAreaSummary
import com.mocoding.pokedex.data.repository.LocationsRepository
import com.mocoding.pokedex.pokedexDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class LocationsStoreFactory(
    private val storeFactory: StoreFactory,
) : KoinComponent {
    private val locationsRepository by inject<LocationsRepository>()

    fun create(): LocationsStore =
        object : LocationsStore, Store<LocationsStore.Intent, LocationsStore.State, Nothing> by storeFactory.create(
            name = "LocationsStore",
            initialState = LocationsStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed class Msg {
        object Loading : Msg()
        data class Loaded(val locations: List<LocationAreaSummary>) : Msg()
        data class Failed(val error: String?) : Msg()
        data class SearchUpdated(val value: String) : Msg()
        object LastPageLoaded : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<LocationsStore.Intent, Unit, LocationsStore.State, Msg, Nothing>(
        pokedexDispatchers.main
    ) {
        override fun executeAction(action: Unit, getState: () -> LocationsStore.State) {
            loadLocationsByPage(0)
        }

        override fun executeIntent(intent: LocationsStore.Intent, getState: () -> LocationsStore.State) {
            when (intent) {
                is LocationsStore.Intent.LoadLocationsByPage -> loadLocationsByPage(intent.page, getState().isLastPageLoaded)
                is LocationsStore.Intent.UpdateSearchValue -> dispatch(Msg.SearchUpdated(intent.searchValue))
            }
        }

        private var job: Job? = null
        private fun loadLocationsByPage(page: Long, isLastPageLoaded: Boolean = false) {
            if (job?.isActive == true || isLastPageLoaded) return

            job = scope.launch {
                dispatch(Msg.Loading)
                locationsRepository.getLocationAreaList(page)
                    .onSuccess {
                        if (it.isEmpty()) dispatch(Msg.LastPageLoaded)
                        else dispatch(Msg.Loaded(it))
                    }
                    .onFailure { dispatch(Msg.Failed(it.message)) }
            }
        }
    }

    private object ReducerImpl : Reducer<LocationsStore.State, Msg> {
        override fun LocationsStore.State.reduce(msg: Msg): LocationsStore.State =
            when (msg) {
                Msg.Loading -> copy(isLoading = true, error = null)
                is Msg.Loaded -> copy(isLoading = false, locations = locations + msg.locations)
                is Msg.Failed -> copy(isLoading = false, error = msg.error)
                is Msg.SearchUpdated -> copy(searchValue = msg.value)
                Msg.LastPageLoaded -> copy(isLoading = false, isLastPageLoaded = true)
            }
    }
}
