package com.example.moviedbapplication.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moviedbapplication.database.MovieDao
import com.example.moviedbapplication.database.MovieDatabase
import com.example.moviedbapplication.database.UserPreferencesRepository
import com.example.moviedbapplication.network.ConnectivityObserver
import com.example.moviedbapplication.network.MovieSyncWorker
import com.example.moviedbapplication.ui.navigation.MovieNavHost
import com.example.moviedbapplication.viewmodel.MovieViewModel

class MovieViewModelFactory(
    private val movieDao: MovieDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieViewModel(movieDao, userPreferencesRepository) as T
    }
}

@Composable
fun MovieApp() {
    val context = LocalContext.current

    val movieDao = remember {
        MovieDatabase.getDatabase(context).movieDao()
    }
    val movieViewModel: MovieViewModel = viewModel(
        factory = MovieViewModelFactory(movieDao, UserPreferencesRepository(context))
    )

    LaunchedEffect(Unit) {
        movieViewModel.loadSelectedCategory()
    }


    val connectivityObserver = remember { ConnectivityObserver(context) }


    val isOnline = connectivityObserver.isOnline.collectAsState(initial = false).value

    LaunchedEffect(isOnline) {
        if (isOnline) {
            val workRequest = OneTimeWorkRequestBuilder<MovieSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                ).build()
            WorkManager.getInstance(context).enqueue(workRequest)
            movieViewModel.loadSelectedCategory()
            val movidID = movieViewModel.getMovieId()
            movieViewModel.setVideos(movidID)
        }
    }

    val navController = rememberNavController()
    MovieNavHost(navController = navController, movieViewModel = movieViewModel, isOnline = isOnline)


}