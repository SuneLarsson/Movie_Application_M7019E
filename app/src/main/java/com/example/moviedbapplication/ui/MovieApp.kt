package com.example.moviedbapplication.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.moviedbapplication.ui.navigation.MovieNavHost


@Composable
fun MovieApp() {
    val movieViewModel: MovieViewModel = viewModel()

    val uiState by movieViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.movies.isEmpty()) {
        if (uiState.movies.isEmpty()) {
            movieViewModel.getMovies(movieType = "popular")
            movieViewModel.setCategory("popular")
        }
    }
    val navController = rememberNavController()
    MovieNavHost(navController = navController, movieViewModel = movieViewModel)

}