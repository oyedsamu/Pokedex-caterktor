package com.mocoding.pokedex.ui.watch.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.mocoding.pokedex.core.model.Video
import com.mocoding.pokedex.data.repository.WatchRepository
import com.mocoding.pokedex.pokedexDispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class WatchDetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val videoId: String,
) : KoinComponent {
    private val watchRepository by inject<WatchRepository>()

    fun create(): WatchDetailsStore =
        object : WatchDetailsStore, Store<Nothing, WatchDetailsStore.State, Nothing> by storeFactory.create(
            name = "WatchDetailsStore",
            initialState = WatchDetailsStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed class Msg {
        object Loading : Msg()
        data class Loaded(val video: Video) : Msg()
        data class Failed(val error: String?) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Nothing, Unit, WatchDetailsStore.State, Msg, Nothing>(
        pokedexDispatchers.main
    ) {
        override fun executeAction(action: Unit, getState: () -> WatchDetailsStore.State) {
            scope.launch {
                dispatch(Msg.Loading)
                watchRepository.getWatchById(videoId)
                    .onSuccess { dispatch(Msg.Loaded(it)) }
                    .onFailure { dispatch(Msg.Failed(it.message)) }
            }
        }
    }

    private object ReducerImpl : Reducer<WatchDetailsStore.State, Msg> {
        override fun WatchDetailsStore.State.reduce(msg: Msg): WatchDetailsStore.State =
            when (msg) {
                Msg.Loading -> copy(isLoading = true, error = null)
                is Msg.Loaded -> copy(isLoading = false, video = msg.video)
                is Msg.Failed -> copy(isLoading = false, error = msg.error)
            }
    }
}
