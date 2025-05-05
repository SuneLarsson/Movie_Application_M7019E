package com.example.moviedbapplication.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moviedbapplication.database.MovieDatabase
import com.example.moviedbapplication.database.UserPreferencesRepository
import com.example.moviedbapplication.network.ConnectivityObserver
import com.example.moviedbapplication.network.MovieSyncWorker
import com.example.moviedbapplication.ui.navigation.MovieNavHost
import com.example.moviedbapplication.viewmodel.MovieViewModel
import com.example.moviedbapplication.ui.screens.NoConnectionScreen


@Composable
fun MovieApp() {
    val context = LocalContext.current

    val movieDao = remember {
        MovieDatabase.getDatabase(context).movieDao()
    }
    val movieViewModel = remember { MovieViewModel(movieDao, UserPreferencesRepository(context)).apply{
        loadSelectedCategory()
    } }

//    movieViewModel.loadSelectedCategory()
//
//    val uiState by movieViewModel.uiState.collectAsState()
//
//    LaunchedEffect(uiState.movies.isEmpty()) {
//        if (uiState.movies.isEmpty() && uiState.selectedCategory == null) {
//            movieViewModel.getMovies(movieType = "popular")
//            movieViewModel.setCategory("popular")
//        }
//    }
    val uiState by movieViewModel.uiState.collectAsState()

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
        }
    }

    val navController = rememberNavController()
    if (uiState.showNoConnection) {
        NoConnectionScreen(
            onRetry = {
                movieViewModel.loadSelectedCategory() // retry logic
            }
        )
    } else {
        MovieNavHost(navController = navController, movieViewModel = movieViewModel)
    }

//    MovieNavHost(navController = navController, movieViewModel = movieViewModel)

}