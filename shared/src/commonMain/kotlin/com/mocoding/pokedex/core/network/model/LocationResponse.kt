package com.mocoding.pokedex.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val name: String,
    val region: NamedApiResource? = null,
    val areas: List<NamedApiResource> = emptyList(),
)
