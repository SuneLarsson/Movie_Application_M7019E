package com.example.moviedbapplication.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


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
    MovieNavHost(navController = navController, movieViewModel = movieViewModel)

}