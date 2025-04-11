package com.example.moviedbapplication.ui

import com.example.moviedbapplication.database.Movies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MovieViewModel {
    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        getMovies()
    }


    fun getMovieById(movieId: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                movie = Movies().getMovieById(movieId)
            )
        }
    }
    fun flipGrid(){
        _uiState.update { currentState ->
            currentState.copy(
                isGrid = !currentState.isGrid
            )
        }
    }
    fun toggleGrid() {
        _uiState.update { it.copy(isGrid = !it.isGrid) }
    }
    fun getMovies(){
        _uiState.update { currentState ->
            currentState.copy(
                movies = Movies().getMovies()
            )
        }
    }
    fun getMoviesByGenre(genre: String){
        _uiState.update { currentState ->
            currentState.copy(
               movies = Movies().moviesByGenre(genre)
            )
        }
    }
//    fun getGridStatus() : Boolean{
//        return uiState.isGrid
//
//    }

}