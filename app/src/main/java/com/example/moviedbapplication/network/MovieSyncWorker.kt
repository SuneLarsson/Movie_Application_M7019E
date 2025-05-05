package com.example.moviedbapplication.network

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moviedbapplication.api.RetrofitInstance
import com.example.moviedbapplication.database.MovieDatabase
import com.example.moviedbapplication.database.FavoriteMovieEntity
import com.example.moviedbapplication.database.UserPreferencesRepository
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.utils.SECRETS
import kotlinx.coroutines.flow.first

class MovieSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = MovieDatabase.getDatabase(applicationContext)
        val dao = database.movieDao()
        val prefs = UserPreferencesRepository(applicationContext)

        return try {
            // 1. Get last selected list type from DataStore
            val movieType = prefs.selectedListFlow.first()
            Log.d("MovieSyncWorker", "Syncing list: $movieType")

            // 2. Fetch from API
            val response = RetrofitInstance.api.getMovies(
                movieType = movieType,
                authHeader = "Bearer ${SECRETS.API_KEY}"
            )

            if (response.isSuccessful) {
                val movies = response.body()?.results ?: emptyList<Movie>()

                // 3. Clear existing cached list
                dao.clearMovies()

                // 4. Insert the new list
                val entities = movies.map { it.toCachedMovieEntity() }
                dao.insertAll(entities)

                Log.d("MovieSyncWorker", "Synced ${entities.size} movies to Room")
                Result.success()
            } else {
                Log.e("MovieSyncWorker", "API error: ${response.code()}")
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e("MovieSyncWorker", "Error syncing movies", e)
            Result.retry()
        }
    }

//    private fun Movie.toEntity(): FavoriteMovieEntity {
//        return FavoriteMovieEntity(
//            id = id,
//            title = title,
//            posterPath = posterPath,
//            backdropPath = backdropPath,
//            releaseDate = releaseDate,
//            overview = overview,
//            genreIds = genreIds ?: emptyList(),
//            homepage = homepage,
//            imdbId = imdbId
//        )
//    }
}