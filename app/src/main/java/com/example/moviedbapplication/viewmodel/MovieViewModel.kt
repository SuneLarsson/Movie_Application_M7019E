package com.example.moviedbapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedbapplication.api.RetrofitInstance
import com.example.moviedbapplication.database.MovieDao
import com.example.moviedbapplication.database.FavoriteMovieEntity
import com.example.moviedbapplication.database.Movies
import com.example.moviedbapplication.database.UserPreferencesRepository
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.models.Video
import com.example.moviedbapplication.ui.state.MovieUiState
import com.example.moviedbapplication.utils.SECRETS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieViewModel (
    private val movieDao: MovieDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel(){



    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()


    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

//    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
//    val reviews: StateFlow<List<Review>> = _reviews

    fun getFavoriteMovies(movieType: String) {
        viewModelScope.launch {
            movieDao.getAllFavoritesMovies().collect { entityList ->
                val movieList = entityList.map { it.toMovie() }
                _uiState.update { currentState ->
                    currentState.copy(
                        movies = movieList,
                        latestMovies = movieList,
                        movieType = movieType
                    )
                }
                setCategory(movieType)
            }
        }
    }

    fun addFavoriteMovie(movie: Movie) {
        viewModelScope.launch {
            movieDao.insert(movie.toEntity())
        }
    }

    fun removeFavoriteMovie(movie: Movie) {
        viewModelScope.launch {
            movieDao.delete(movie.toEntity())
        }
    }


    fun Movie.toEntity(): FavoriteMovieEntity = FavoriteMovieEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        overview = overview,
        genreIds = genreIds ?: emptyList(),
        homepage = homepage,
        imdbId = imdbId
    )

    fun FavoriteMovieEntity.toMovie(): Movie = Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        overview = overview,
        genreIds = genreIds,
        homepage = homepage,
        imdbId = imdbId
    )




    fun setVideos(movieId: Long){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.videoApi.getVideos(
                    movieId = movieId.toString() ,
                    authHeader = "Bearer ${SECRETS.API_KEY}"
                )

                if (response.isSuccessful){
                    val videos = response.body()?.results ?: emptyList<Video>()

                    _uiState.update { currentState ->
                        currentState.copy(
                            videos = videos
                        )
                    }
                }
                else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            videos = emptyList()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error occurred during API call", e)
            }
        }
    }

    fun setMovieById(movieId: Long) {
        if (_uiState.value.movie?.id == movieId) return


        viewModelScope.launch {
            try {
                _uiState.update { currentState ->
                    currentState.copy(
                        loading = true,  // Set loading to true while fetching movie
                        movie = null     // Clear the previous movie data
                    )
                }

                val response = RetrofitInstance.movieApi.getMovie(
                    movieId = movieId.toString(),
                    authHeader = "Bearer ${SECRETS.API_KEY}"
                )

                if (response.isSuccessful) {
                    Log.d("MovieViewModel", "API call successful. Movie data: ${response.body()}")

                    _uiState.update { currentState ->
                        currentState.copy(
                            movie = response.body(),
                            loading = false // Set loading to false after receiving the movie data
                        )
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            movie = null,
                            loading = false // Set loading to false after failure
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error occurred during API call", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        movie = null,
                        loading = false // Set loading to false after failure
                    )
                }
            }
        }
    }



    fun getMovieById(movieId: Long) : Movie?{
        return _uiState.value.movie
    }

    fun setMovieId(movieId: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                movieId = movieId
            )
        }
    }

    fun toggleGrid() {
        _uiState.update { it.copy(isGrid = !it.isGrid) }
    }

    fun setCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        viewModelScope.launch {
            userPreferencesRepository.saveSelectedList(category)
        }
    }

    fun loadSelectedCategory() {
        viewModelScope.launch {
            userPreferencesRepository.selectedListFlow.collect { saved ->
                getMovies(movieType = saved)
                setCategory(saved)
            }
        }
    }

    fun getCategory() : String? {
        return _uiState.value.selectedCategory
    }


    fun getMovies(apiKey: String = SECRETS.API_KEY, movieType: String ) {

        if (_uiState.value.movieType == movieType) {
            _uiState.update {
                it.copy(movies = _uiState.value.latestMovies)
            }
            return
        } else {
            viewModelScope.launch {
                try {
                    Log.d("MovieViewModel", "API call started with movieType: $movieType")

                    val response = RetrofitInstance.api.getMovies(
                        movieType = movieType,
                        authHeader = "Bearer ${SECRETS.API_KEY}"

                    )


                    if (response.isSuccessful) {

                        Log.d(
                            "MovieViewModel",
                            "API call successful. Number of movies: ${response.body()?.results?.size}"
                        )

                        val movies = response.body()?.results ?: emptyList<Movie>()


                        _uiState.update { currentState ->
                            currentState.copy(
                                movies = movies,
                                latestMovies = movies,
                                movieType = movieType
                            )
                        }
                        setCategory(movieType)
                    } else {

                        Log.e(
                            "MovieViewModel",
                            "API call failed with status code: ${response.code()}"
                        )
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
    }

    fun getMovieReviews(movieId: Long, apiKey: String = SECRETS.API_KEY) {
        viewModelScope.launch {
            _loading.value = true

            try {
                Log.d("MovieViewModel", "Fetching reviews for movieId: $movieId")

                val response = RetrofitInstance.reviewApi.getReviews(
                    movieId = movieId,
                    authHeader = "Bearer $apiKey"
                )

                if (response.isSuccessful) {
                    val reviews = response.body()?.results ?: emptyList()
                    Log.d("MovieViewModel", "Fetched ${reviews.size} reviews")

                    _uiState.update { currentState ->
                        currentState.copy(
                            reviews = reviews
                        )
                    }

                } else {
                    Log.e("MovieViewModel", "Failed to get reviews. Code: ${response.code()}")

                    _uiState.update { currentState ->
                        currentState.copy(
                            reviews = emptyList()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching reviews", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        reviews = emptyList()
                    )
                }
            } finally {
                _loading.value = false
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

    fun resetMovie() {
        _uiState.update { currentState ->
            currentState.copy(movie = null, loading = false)
        }
    }



}