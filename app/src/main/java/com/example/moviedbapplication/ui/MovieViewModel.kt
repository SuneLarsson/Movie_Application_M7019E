package com.example.moviedbapplication.ui

import com.example.moviedbapplication.database.Movies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MovieViewModel {
    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    fun getMovieById(movieId: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                movie = Movies().getMovieById(movieId)
            )
        }
    }

}