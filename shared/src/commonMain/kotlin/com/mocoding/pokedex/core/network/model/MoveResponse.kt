package com.mocoding.pokedex.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoveResponse(
    val id: Long,
    val name: String,
    val accuracy: Int? = null,
    @SerialName("effect_chance") val effectChance: Int? = null,
    val pp: Int? = null,
    val priority: Int,
    val power: Int? = null,
    @SerialName("damage_class") val damageClass: NamedApiResource,
    val type: NamedApiResource,
    val target: NamedApiResource,
    @SerialName("effect_entries") val effectEntries: List<VerboseEffectEntry>,
    @SerialName("flavor_text_entries") val flavorTextEntries: List<MoveFlavorTextEntry>,
) {
    @Serializable
    data class VerboseEffectEntry(
        val effect: String,
        @SerialName("short_effect") val shortEffect: String,
        val language: NamedApiResource,
    )

    @Serializable
    data class MoveFlavorTextEntry(
        @SerialName("flavor_text") val flavorText: String,
        val language: NamedApiResource,
        @SerialName("version_group") val versionGroup: NamedApiResource,
    )
}
