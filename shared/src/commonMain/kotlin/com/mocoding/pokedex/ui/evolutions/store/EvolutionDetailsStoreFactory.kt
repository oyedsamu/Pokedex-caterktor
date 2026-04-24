package com.mocoding.pokedex.ui.evolutions.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.mocoding.pokedex.core.model.EvolutionInfo
import com.mocoding.pokedex.data.repository.EvolutionRepository
import com.mocoding.pokedex.pokedexDispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class EvolutionDetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val pokemonName: String,
) : KoinComponent {
    private val evolutionRepository by inject<EvolutionRepository>()

    fun create(): EvolutionDetailsStore =
        object : EvolutionDetailsStore, Store<Nothing, EvolutionDetailsStore.State, Nothing> by storeFactory.create(
            name = "EvolutionDetailsStore",
            initialState = EvolutionDetailsStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed class Msg {
        object Loading : Msg()
        data class Loaded(val info: EvolutionInfo) : Msg()
        data class Failed(val error: String?) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Nothing, Unit, EvolutionDetailsStore.State, Msg, Nothing>(
        pokedexDispatchers.main
    ) {
        override fun executeAction(action: Unit, getState: () -> EvolutionDetailsStore.State) {
            scope.launch {
                dispatch(Msg.Loading)
                evolutionRepository.getEvolutionInfoByPokemonName(pokemonName)
                    .onSuccess { dispatch(Msg.Loaded(it)) }
                    .onFailure { dispatch(Msg.Failed(it.message)) }
            }
        }
    }

    private object ReducerImpl : Reducer<EvolutionDetailsStore.State, Msg> {
        override fun EvolutionDetailsStore.State.reduce(msg: Msg): EvolutionDetailsStore.State =
            when (msg) {
                Msg.Loading -> copy(isLoading = true, error = null)
                is Msg.Loaded -> copy(isLoading = false, evolutionInfo = msg.info)
                is Msg.Failed -> copy(isLoading = false, error = msg.error)
            }
    }
}
