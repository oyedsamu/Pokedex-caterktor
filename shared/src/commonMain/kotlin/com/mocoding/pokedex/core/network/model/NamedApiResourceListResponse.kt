package com.mocoding.pokedex.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NamedApiResourceListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NamedApiResource>,
)
