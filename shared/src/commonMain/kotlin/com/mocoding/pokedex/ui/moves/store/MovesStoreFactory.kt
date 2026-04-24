package com.mocoding.pokedex.ui.moves.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.mocoding.pokedex.core.model.MoveSummary
import com.mocoding.pokedex.data.repository.MovesRepository
import com.mocoding.pokedex.pokedexDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class MovesStoreFactory(
    private val storeFactory: StoreFactory,
) : KoinComponent {
    private val movesRepository by inject<MovesRepository>()

    fun create(): MovesStore =
        object : MovesStore, Store<MovesStore.Intent, MovesStore.State, Nothing> by storeFactory.create(
            name = "MovesStore",
            initialState = MovesStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed class Msg {
        object Loading : Msg()
        data class Loaded(val moves: List<MoveSummary>) : Msg()
        data class Failed(val error: String?) : Msg()
        data class SearchUpdated(val value: String) : Msg()
        object LastPageLoaded : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<MovesStore.Intent, Unit, MovesStore.State, Msg, Nothing>(
        pokedexDispatchers.main
    ) {
        override fun executeAction(action: Unit, getState: () -> MovesStore.State) {
            loadMovesByPage(page = 0)
        }

        override fun executeIntent(intent: MovesStore.Intent, getState: () -> MovesStore.State) {
            when (intent) {
                is MovesStore.Intent.LoadMovesByPage -> loadMovesByPage(intent.page, getState().isLastPageLoaded)
                is MovesStore.Intent.UpdateSearchValue -> dispatch(Msg.SearchUpdated(intent.searchValue))
            }
        }

        private var loadMovesJob: Job? = null
        private fun loadMovesByPage(page: Long, isLastPageLoaded: Boolean = false) {
            if (loadMovesJob?.isActive == true || isLastPageLoaded) return

            loadMovesJob = scope.launch {
                dispatch(Msg.Loading)
                movesRepository.getMoveList(page)
                    .onSuccess { moves ->
                        if (moves.isEmpty()) dispatch(Msg.LastPageLoaded)
                        else dispatch(Msg.Loaded(moves))
                    }
                    .onFailure { dispatch(Msg.Failed(it.message)) }
            }
        }
    }

    private object ReducerImpl : Reducer<MovesStore.State, Msg> {
        override fun MovesStore.State.reduce(msg: Msg): MovesStore.State =
            when (msg) {
                Msg.Loading -> copy(isLoading = true, error = null)
                is Msg.Loaded -> copy(
                    isLoading = false,
                    moveList = moveList + msg.moves,
                    error = null,
                )
                is Msg.Failed -> copy(isLoading = false, error = msg.error)
                is Msg.SearchUpdated -> copy(searchValue = msg.value)
                Msg.LastPageLoaded -> copy(isLoading = false, isLastPageLoaded = true)
            }
    }
}
