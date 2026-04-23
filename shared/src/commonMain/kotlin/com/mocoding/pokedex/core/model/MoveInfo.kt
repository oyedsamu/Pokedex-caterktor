package com.mocoding.pokedex.core.model

data class MoveInfo(
    val id: Long,
    val name: String,
    val accuracy: Int?,
    val effectChance: Int?,
    val pp: Int?,
    val priority: Int,
    val power: Int?,
    val damageClass: String,
    val type: String,
    val target: String,
    val effect: String?,
    val flavorText: String?,
)
