package com.example.moviedbapplication.database

import com.example.moviedbapplication.models.Genre
import com.example.moviedbapplication.models.Movie

class GenreMap {

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

    fun mapMovieGenreIdsToGenre(movie: Movie): Movie {
        val genres = movie.genreIds?.mapNotNull { id ->
            genreMap[id]?.let { Genre(id, it) }
        }
        return movie.copy(genres = genres)

    }

}