package com.example.moviedbapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedbapplication.api.RetrofitInstance
import com.example.moviedbapplication.database.Movies
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.utils.SECRETS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.mutableStateOf

class MovieViewModel : ViewModel(){



    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()


    init {
        getMovies(movieType = "popular")
        setCategory("popular")
    }


    fun setMovieById(movieId: Long) {
        viewModelScope.launch {
            try {

                val movie = RetrofitInstance.movieApi.getMovie(
                    movieId = movieId.toString(),
                    authHeader = "Bearer ${SECRETS.API_KEY}"
                )
                if (movie.isSuccessful) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            movie = movie.body()
                        )

                    }
                }else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            movie = null
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error occurred during API call", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        movie = null
                    )
                }
            }

        }
    }

    fun getMovieById(movieId: Long) : Movie?{
        return _uiState.value.movie
    }


    fun toggleGrid() {
        _uiState.update { it.copy(isGrid = !it.isGrid) }
    }

    fun setCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun getCategory() : String? {
        return _uiState.value.selectedCategory
    }


    fun getMovies(apiKey: String = SECRETS.API_KEY, movieType: String ) {
        viewModelScope.launch {
            try {
                Log.d("MovieViewModel", "API call started with movieType: $movieType")

                val response = RetrofitInstance.api.getMovies(
                    movieType = movieType,
                    authHeader = "Bearer ${SECRETS.API_KEY}"

                )


                if (response.isSuccessful) {

                    Log.d("MovieViewModel", "API call successful. Number of movies: ${response.body()?.results?.size}")

                    val movies = response.body()?.results ?: emptyList<Movie>()


                    _uiState.update { currentState ->
                        currentState.copy(
                            movies = movies,
                            latestMovies = movies,
                        )
                    }
                    setCategory(movieType)
                } else {

                    Log.e("MovieViewModel", "API call failed with status code: ${response.code()}")
                    _uiState.update { currentState ->
                        currentState.copy(
                            movies = emptyList()

                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error occurred during API call", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        movies = emptyList()
                    )
                }
            }
        }
    }



    fun getMoviesByGenre(genre: String) {
        val genreMap = Movies().getGenreMap()
        _uiState.update { currentState ->
            // Filter the movies from the current state using the genre
            val filteredMovies = currentState.movies.filter { movie ->
                movie.genreIds?.any { genreMap[it] == genre } == true
            }

            currentState.copy(
                movies = filteredMovies
            )
        }
    }

    fun showAllMovies() {
        _uiState.update { currentState ->
            currentState.copy(
                movies = currentState.latestMovies
            )

        }
    }


}