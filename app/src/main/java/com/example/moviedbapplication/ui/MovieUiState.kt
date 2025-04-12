package com.example.moviedbapplication.ui

import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.models.MovieListResponse
import retrofit2.Response

data class MovieUiState(
    val movieId: Long = 0L,
    val movie: Movie? = null,
    val movies: List<Movie> = emptyList(),  // Change from Response<MovieListResponse> to List<Movie>
    val isGrid: Boolean = false
)
