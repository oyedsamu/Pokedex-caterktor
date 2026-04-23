package com.mocoding.pokedex.core.model

data class EvolutionInfo(
    val pokemonName: String,
    val rootName: String,
    val previousEvolutionName: String?,
    val nodes: List<EvolutionNode>,
) {
    data class EvolutionNode(
        val pokemonName: String,
        val stage: Int,
        val isBaby: Boolean,
        val conditions: List<EvolutionCondition>,
        val evolvesTo: List<String>,
    )

    data class EvolutionCondition(
        val trigger: String,
        val item: String?,
        val heldItem: String?,
        val minLevel: Int?,
        val minHappiness: Int?,
        val minBeauty: Int?,
        val minAffection: Int?,
        val location: String?,
        val timeOfDay: String?,
        val knownMove: String?,
        val knownMoveType: String?,
        val partySpecies: String?,
        val partyType: String?,
        val tradeSpecies: String?,
        val needsOverworldRain: Boolean,
        val turnUpsideDown: Boolean,
        val gender: Int?,
        val relativePhysicalStats: Int?,
    ) {
        val description: String = buildList {
            add(trigger.replace('-', ' '))
            minLevel?.let { add("level $it") }
            item?.let { add("item: ${it.replace('-', ' ')}") }
            heldItem?.let { add("held: ${it.replace('-', ' ')}") }
            minHappiness?.let { add("happiness $it") }
            minBeauty?.let { add("beauty $it") }
            minAffection?.let { add("affection $it") }
            location?.let { add("at ${it.replace('-', ' ')}") }
            timeOfDay?.takeIf { it.isNotBlank() }?.let { add(it) }
            knownMove?.let { add("knows ${it.replace('-', ' ')}") }
            knownMoveType?.let { add("move type ${it.replace('-', ' ')}") }
            partySpecies?.let { add("party ${it.replace('-', ' ')}") }
            partyType?.let { add("party type ${it.replace('-', ' ')}") }
            tradeSpecies?.let { add("trade for ${it.replace('-', ' ')}") }
            if (needsOverworldRain) add("rain")
            if (turnUpsideDown) add("turn upside down")
            gender?.let {
                add(
                    when (it) {
                        1 -> "female"
                        2 -> "male"
                        else -> "gender $it"
                    }
                )
            }
            relativePhysicalStats?.let {
                add(
                    when {
                        it > 0 -> "attack > defense"
                        it < 0 -> "attack < defense"
                        else -> "attack = defense"
                    }
                )
            }
        }.joinToString(" • ")
    }
}
