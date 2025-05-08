package com.example.moviedbapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedbapplication.api.RetrofitInstance
import com.example.moviedbapplication.database.CachedMovieEntity
import com.example.moviedbapplication.database.MovieDao
import com.example.moviedbapplication.database.FavoriteMovieEntity
import com.example.moviedbapplication.database.GenreMap
import com.example.moviedbapplication.database.UserPreferencesRepository
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.models.Video
import com.example.moviedbapplication.ui.state.MovieUiState
import com.example.moviedbapplication.utils.SECRETS
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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


    private var lastFetchedMovieType: String? = null



    private var favoriteMoviesJob: Job? = null
    fun getFavoriteMovies() {
        favoriteMoviesJob?.cancel()
        favoriteMoviesJob = viewModelScope.launch {
            movieDao.getAllFavoritesMovies().collect { entityList ->

                if (_uiState.value.selectedCategory == "favorites") {
                    val movieList = entityList.map { it.toMovie() }
                    _uiState.update {
                        it.copy(
                            movies = movieList,
                            latestMovies = movieList,
                            movieType = "favorites"
                        )
                    }
                setCategory("favorites")
                }
            }
        }
    }


    fun FavoriteMovieEntity.toMovie(): Movie = Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        overview = overview,
        genreIds = genreIds,
        homepage = homepage,
        imdbId = imdbId,
        genres = genres
    )

    fun CachedMovieEntity.toMovie(): Movie = Movie(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            overview = overview,
            genreIds = genreIds,
            homepage = homepage,
            imdbId = imdbId,
            genres = genres
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
                _uiState.update { currentState ->
                    currentState.copy(
                        videos = emptyList()
                    )
                }
            }
        }
    }

    fun setMovieById(movieId: Long) {
        if (_uiState.value.movie?.id == movieId) return

        viewModelScope.launch {
            try {
                _uiState.update { currentState ->
                    currentState.copy(
                        loading = true,
                        movie = null
                    )
                }

                val response = RetrofitInstance.movieApi.getMovie(
                    movieId = movieId.toString(),
                    authHeader = "Bearer ${SECRETS.API_KEY}"
                )

                if (response.isSuccessful) {
                    Log.d("MovieViewModel", "API call successful. Movie data: ${response.body()}")
                    if (response.body() != null) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                movie = response.body(),
                                loading = false
                            )
                        }
                    } else {
                        setMovieByCacheId(movieId)
                    }

                } else {
                    setMovieByCacheId(movieId)
                }

            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error occurred during API call", e)
                setMovieByCacheId(movieId)
            }
        }
    }

    fun setMovieByCacheId(movieId: Long) {
        viewModelScope.launch {
            val movie = movieDao.getCachedMovieById(movieId)
            val favMovie  = movieDao.getFavoriteMovieById(movieId)

            if (movie != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        movie = movie.toMovie(),
                        loading = false
                    )
                }
            } else if (favMovie != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        movie = favMovie.toMovie(),
                        loading = false
                    )
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        movie = null,
                        loading = false
                    )
                }
            }
        }
    }



    fun getMovie() : Movie?{
        return _uiState.value.movie
    }

    fun getMovieId() : Long{
        return _uiState.value.movieId
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
        if (_uiState.value.selectedCategory != category) {
            Log.d("MovieViewModel", "Setting category: $category")
            _uiState.value = _uiState.value.copy(selectedCategory = category)
            viewModelScope.launch {
                userPreferencesRepository.saveSelectedList(category)
            }
        }

    }


    fun loadSelectedCategory() {
        Log.d("MovieViewModel", "Loading selected category")
        viewModelScope.launch {
            userPreferencesRepository.selectedListFlow
                .distinctUntilChanged()
                .collect { saved ->
                    if (saved != lastFetchedMovieType) {
                        getMovies(movieType = saved)
                    }
                }
        }
    }



    fun getCachedMovies(movieType: String) {
        Log.d("MovieViewModel", "Getting cached movies")
        viewModelScope.launch {
            movieDao.getAllCachedMovies().collect { entityList ->
                val movieList = entityList.map { it.toMovie() }
                _uiState.update { currentState ->
                    currentState.copy(
                        movies = movieList,
                    )
                }
            }
        }
        setCategory(movieType)
    }



    fun setCachedMovies(movies: List<Movie>) {
        Log.d("MovieViewModel", "Setting cached movies: $movies")
        viewModelScope.launch {
            val entities = movies.mapIndexed{ index, movie -> GenreMap().mapMovieGenreIdsToGenre(movie).toCachedMovieEntity(index) }
            movieDao.clearMovies()
            movieDao.insertAll(entities)
        }
    }

    fun getMovies(movieType: String ) {

        if(movieType == "favorites") {
            setCategory("favorites")
            getFavoriteMovies()
            return
        } else {
            favoriteMoviesJob?.cancel()
            favoriteMoviesJob = null
        }

        if (lastFetchedMovieType == movieType) {
            Log.d("MovieViewModel", "Get Cache: $movieType")
            getCachedMovies(movieType)
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


                        setCachedMovies(movies)
                        _uiState.update { currentState ->
                            currentState.copy(
                                movies = movies,
                                latestMovies = movies,
                                movieType = movieType
                            )
                        }
                        setCategory(movieType)
                        lastFetchedMovieType = movieType
                    } else {

                        Log.e(
                            "MovieViewModel",
                            "API call failed with status code: ${response.code()}"
                        )
                        _uiState.update { currentState ->
                            currentState.copy(
                                movies = emptyList(),
                                movieType = movieType


                            )
                        }
                        setCategory(movieType)
                    }
                } catch (e: Exception) {
                    Log.e("MovieViewModel", "Error occurred during API call", e)
                    _uiState.update { currentState ->
                        currentState.copy(
                            movies = emptyList(),
                            movieType = movieType

                        )
                    }
                    setCategory(movieType)
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
        val genreMap = GenreMap().getGenreMap()
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