package com.mocoding.pokedex.core.model

data class Video(
    val id: String,
    val title: String,
    val imageUrl: String,
    val year: Int,
    val category: String,
    val details: String,
    val synopsis: String,
) {
    companion object {
        val demoList = listOf(
            Video(
                id = "01",
                title = "Pokemon Master Journeys: The Series",
                imageUrl = "https://orgoglionerd.it/wp-content/uploads/2022/05/Esplorazioni-Pokemon-Super-nuova-serie-tv-trailer.jpg",
                year = 2021,
                category = "Series",
                details = "10 EP",
                synopsis = "Ash and Goh continue traveling the world, meeting Pokemon across regions while training toward their next big battles.",
            ),
            Video(
                id = "02",
                title = "Pokémon: Mewtwo Strikes Back—Evolution",
                imageUrl = "https://assets.pokemon.com/assets/cms2/img/watch-pokemon-tv/movies/movie22/movie22_ss01.jpg",
                year = 2020,
                category = "Series",
                details = "12 EP",
                synopsis = "A remade adventure centered on Mewtwo, cloned Pokemon, and a clash over what it means to exist and belong.",
            ),
            Video(
                id = "03",
                title = "Pokémon the Movie: The Power of Us",
                imageUrl = "https://assets.pokemon.com/assets/cms2/img/watch-pokemon-tv/movies/movie21/movie21_ss01.jpg",
                year = 2018,
                category = "Movie",
                details = "1h 52m",
                synopsis = "Several strangers in Fula City become unlikely allies when they must protect the city and the mythical Pokemon Zeraora.",
            ),
            Video(
                id = "04",
                title = "Pokémon the Movie: Hoopa and the Clash of Ages",
                imageUrl = "https://assets.pokemon.com/assets/cms2/img/watch-pokemon-tv/movies/movie18/movie18_ss03.jpg",
                year = 2015,
                category = "Movie",
                details = "1h 46m",
                synopsis = "Ash and friends help Hoopa while legendary Pokemon appear in a large-scale battle that threatens an entire desert city.",
            ),
            Video(
                id = "05",
                title = "Pokémon—Zoroark: Master of Illusions",
                imageUrl = "https://assets.pokemon.com/assets/cms2/img/watch-pokemon-tv/movies/movie13/movie13_ss01.jpg",
                year = 2011,
                category = "Series",
                details = "8 EP",
                synopsis = "A movie adventure involving Zoroark, illusion powers, and a city caught in conflict between powerful forces.",
            ),
        )
    }
}
