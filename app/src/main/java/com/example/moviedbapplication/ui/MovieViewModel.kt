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

class MovieViewModel : ViewModel(){

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

//    fun getMovies(apiKey: String = SECRETS.API_KEY, movieType: String = "popular"){
//        _uiState.update { currentState ->
//            currentState.copy(
//                movies = RetrofitInstance.api.getMovies(apiKey = apiKey, movieType = movieType)
//            )
//        }
//    }

    fun getMovies(apiKey: String = SECRETS.API_KEY, movieType: String = "popular") {
        viewModelScope.launch {
            try {
                Log.d("MovieViewModel", "API call started with movieType: $movieType")
                // Make the API call and get the response
                val response = RetrofitInstance.api.getMovies(
                    movieType = movieType,
                    authHeader = "Bearer ${SECRETS.API_KEY}"

                )

                // Check if the response is successful
                if (response.isSuccessful) {

                    Log.d("MovieViewModel", "API call successful. Number of movies: ${response.body()?.results?.size}")
                    // Extract the list of movies from the response body
                    val movies = response.body()?.results ?: emptyList<Movie>()

                    _uiState.update { currentState ->
                        currentState.copy(
                            movies = movies // Now update the UI state with the List<Movie>
                        )
                    }
                } else {
                    // Handle error scenario (optional)
                    Log.e("MovieViewModel", "API call failed with status code: ${response.code()}")
                    _uiState.update { currentState ->
                        currentState.copy(
                            movies = emptyList() // If an error occurs, set the list to empty
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle network errors or other exceptions
                Log.e("MovieViewModel", "Error occurred during API call", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        movies = emptyList() // If an exception occurs, set the list to empty
                    )
                }
            }
        }
    }


    //    fun getMoviesByGenre(genre: String){
//        _uiState.update { currentState ->
//            currentState.copy(
//               movies = Movies().moviesByGenre(genre)
//            )
//        }
//    }
    fun getMoviesByGenre(genre: String) {
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

//    fun getGridStatus() : Boolean{
//        return uiState.isGrid
//
//    }

}