package com.mocoding.pokedex.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NamedApiResource(
    val name: String,
    val url: String,
)
