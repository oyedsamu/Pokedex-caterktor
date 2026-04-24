package com.mocoding.pokedex.ui.moves.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.mocoding.pokedex.core.model.MoveInfo
import com.mocoding.pokedex.data.repository.MovesRepository
import com.mocoding.pokedex.pokedexDispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class MoveDetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val moveName: String,
) : KoinComponent {
    private val movesRepository by inject<MovesRepository>()

    fun create(): MoveDetailsStore =
        object : MoveDetailsStore, Store<Nothing, MoveDetailsStore.State, Nothing> by storeFactory.create(
            name = "MoveDetailsStore",
            initialState = MoveDetailsStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed class Msg {
        object Loading : Msg()
        data class Loaded(val moveInfo: MoveInfo) : Msg()
        data class Failed(val error: String?) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Nothing, Unit, MoveDetailsStore.State, Msg, Nothing>(
        pokedexDispatchers.main
    ) {
        override fun executeAction(action: Unit, getState: () -> MoveDetailsStore.State) {
            scope.launch {
                dispatch(Msg.Loading)
                movesRepository.getMoveByName(moveName)
                    .onSuccess { dispatch(Msg.Loaded(it)) }
                    .onFailure { dispatch(Msg.Failed(it.message)) }
            }
        }
    }

    private object ReducerImpl : Reducer<MoveDetailsStore.State, Msg> {
        override fun MoveDetailsStore.State.reduce(msg: Msg): MoveDetailsStore.State =
            when (msg) {
                Msg.Loading -> copy(isLoading = true, error = null)
                is Msg.Loaded -> copy(isLoading = false, moveInfo = msg.moveInfo, error = null)
                is Msg.Failed -> copy(isLoading = false, error = msg.error)
            }
    }
}
