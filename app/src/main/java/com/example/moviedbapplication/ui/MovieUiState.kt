package com.example.moviedbapplication.ui

import com.example.moviedbapplication.models.Movie


data class MovieUiState(
    val movieId: Long = 0L,
    val movie: Movie? = null,
    val movies: List<Movie> = emptyList(),
    val movieType: String = "",
    val latestMovies: List<Movie> = emptyList(),
    val isGrid: Boolean = false,
    val selectedCategory: String? = null,
    val loading: Boolean = false
)
