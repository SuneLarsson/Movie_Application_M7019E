package com.example.moviedbapplication.database

import com.example.moviedbapplication.models.Movie

class Movies {

    private val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )
    private val movies = listOf(
        Movie(
            id = 950387,
            title = "A Minecraft Movie",
            posterPath = "/yFHHfHcUgGAxziP1C3lLt0q2T4s.jpg",
            backdropPath = "/2Nti3gYAX513wvhp8IiLL6ZDyOm.jpg",
            releaseDate = "2025-03-31",
            overview = "Four misfits find themselves struggling with ordinary problems when they are suddenly pulled through a mysterious portal into the Overworld: a bizarre, cubic wonderland that thrives on imagination. To get back home, they'll have to master this world while embarking on a magical quest with an unexpected, expert crafter, Steve.",
            genreIds = listOf(10751, 35, 12, 14),
            homepage = "https://www.minecraft-movie.com",
            imdbId = "tt3566834"
        ),
        Movie(
            id = 324544,
            title = "In the Lost Lands",
            posterPath = "/iHf6bXPghWB6gT8kFkL1zo00x6X.jpg",
            backdropPath = "/op3qmNhvwEvyT7UFyPbIfQmKriB.jpg",
            releaseDate = "2025-02-27",
            overview = "A queen sends the powerful and feared sorceress Gray Alys to the ghostly wilderness of the Lost Lands in search of a magical power, where the sorceress and her guide, the drifter Boyce must outwit and outfight man and demon.",
            genreIds = listOf(14, 12, 28),
            homepage = "https://inthelostlands.com",
            imdbId = "tt4419684"
        ),
        Movie(
            id = 1195506,
            title = "Novocaine",
            posterPath = "/xmMHGz9dVRaMY6rRAlEX4W0Wdhm.jpg",
            backdropPath = "/sNx1A3822kEbqeUxvo5A08o4N7o.jpg",
            releaseDate = "2025-03-12",
            overview = "When the girl of his dreams is kidnapped, everyman Nate turns his inability to feel pain into an unexpected strength in his fight to get her back.",
            genreIds = listOf(28, 35, 53),
            homepage = "https://www.novocainemovie.com",
            imdbId = "tt29603959"
        ),
        Movie(
            id = 1229730,
            title = "Carjackers",
            posterPath = "/wbkPMTz2vVai7Ujyp0ag7AM9SrO.jpg",
            backdropPath = "/is9bmV6uYXu7LjZGJczxrjJDlv8.jpg",
            releaseDate = "2025-03-27",
            overview = "By day, they're invisible—valets, hostesses, and bartenders at a luxury hotel. By night, they're the Carjackers, a crew of skilled drivers who track and rob wealthy clients on the road. As they plan their ultimate heist, the hotel director hires a ruthless hitman, to stop them at all costs. With danger closing in, can Nora, Zoe, Steve, and Prestance pull off their biggest score yet?",
            genreIds = listOf(28, 12),
            homepage =  "https://www.amazon.com/gp/video/detail/B0DCYJL1GN",
            imdbId = "tt35683795"
        ),
        Movie(
            id = 822119,
            title = "Captain America: Brave New World",
            posterPath = "/pzIddUEMWhWzfvLI3TwxUG2wGoi.jpg",
            backdropPath = "/ce3prrjh9ZehEl5JinNqr4jIeaB.jpg",
            releaseDate = "2025-02-12",
            overview = "After meeting with newly elected U.S. President Thaddeus Ross, Sam finds himself in the middle of an international incident. He must discover the reason behind a nefarious global plot before the true mastermind has the entire world seeing red.",
            genreIds = listOf(28, 53, 878),
            homepage = "https://www.marvel.com/movies/captain-america-brave-new-world",
            imdbId = "tt14513804"
        )

    )

    fun getMovies(): List<Movie> = movies

    fun getMovieById(id: Long): Movie? {
        return movies.find { it.id == id }
    }

    fun moviesByGenre(genre: String): List<Movie> {
        return movies.filter { movie ->
            movie.genreIds?.any { genreMap[it] == genre } ?: false
        }
    }


    fun createSectionedMoviesByGenre(movies: List<Movie>): MutableMap<String, MutableList<Movie>> {
        val sectionMap = mutableMapOf<String, MutableList<Movie>>()

        for (movie in movies) {
            // For each genre ID in the movie, find the corresponding genre name
            for (genreId in movie.genreIds!!) {
                val genre = genreMap[genreId] // Get genre name from the genreMap
                if (genre != null) {
                    val list = sectionMap.getOrPut(genre) { mutableListOf() }
                    list.add(movie)
                }
            }
        }

        return sectionMap
    }

    fun getGenreMap(): Map<Int, String> {
        return genreMap
    }

}
