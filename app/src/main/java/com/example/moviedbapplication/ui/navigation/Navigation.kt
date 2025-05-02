package com.example.moviedbapplication.ui.navigation


import android.os.Bundle
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviedbapplication.ui.screens.DetailScreen
import com.example.moviedbapplication.ui.screens.MainScreen
import com.example.moviedbapplication.ui.screens.ThirdScreen
import com.example.moviedbapplication.ui.MovieViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState

enum class MovieScreen {
    Main,
    Details,
    Third
}

@Composable
fun MovieNavHost(
    navController: NavHostController,
    movieViewModel: MovieViewModel
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentScreen = MovieScreen.entries.find { route ->
        currentRoute?.startsWith(route.name) == true
    } ?: MovieScreen.Main

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MovieScreen.Main.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MovieScreen.Main.name) {
                MainScreen(movieViewModel, navController)
            }
            composable(
                "${MovieScreen.Details.name}/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.LongType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getLong("movieId")
                movieViewModel.setMovieId(movieId!!)

                if (movieId != null) {
                    DetailScreen(
                        navController,
                        movieViewModel = movieViewModel
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No movie found.")
                    }
                }
            }

            composable(
                "${MovieScreen.Third.name}/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.LongType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getLong("movieId") ?: return@composable
                ThirdScreen(navController = navController, movieViewModel = movieViewModel, movieId = movieId)
            }
        }
    }
}
