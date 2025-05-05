package com.example.moviedbapplication.ui.state

import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.models.Review
import com.example.moviedbapplication.models.Video

data class MovieUiState(
    val movieId: Long = 0L,
    val movie: Movie? = null,
    val movies: List<Movie> = emptyList(),
    val movieType: String = "",
    val latestMovies: List<Movie> = emptyList(),
    val isGrid: Boolean = false,
    val selectedCategory: String? = null,
    val loading: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val videos: List<Video> = emptyList(),
    val showNoConnection: Boolean = false
)