package com.mocoding.pokedex

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val pokedexDispatchers: PokedexDispatchers = object: PokedexDispatchers {
    override val main: CoroutineDispatcher = try {
        Dispatchers.Main.immediate
    } catch (_: Throwable) {
        Dispatchers.Default
    }
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
