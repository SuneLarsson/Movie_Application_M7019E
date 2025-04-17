package com.example.moviedbapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.moviedbapplication.ui.screens.DetailScreen
import com.example.moviedbapplication.ui.screens.MainScreen
import com.example.moviedbapplication.ui.MovieViewModel
import com.example.moviedbapplication.ui.screens.ThirdScreen


enum class MovieScreen {
    Main,
    Details,
    Third
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreenContent()
        }
    }
}

@Composable
fun MainScreenContent() {
    val movieViewModel: MovieViewModel = viewModel()

    val uiState by movieViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.movies.isEmpty()) {
        if (uiState.movies.isEmpty()) {
            movieViewModel.getMovies(movieType = "popular")
            movieViewModel.setCategory("popular")
        }
    }

    MovieApp(movieViewModel = movieViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieApp(
    navController: NavHostController = rememberNavController(),
    movieViewModel: MovieViewModel = MovieViewModel(),
) {


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentScreen = MovieScreen.entries.find { route ->
        currentRoute?.startsWith(route.name) == true
    } ?: MovieScreen.Main


    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(35.dp) // Optional: Add padding to position the icon
            ) {
                if (navController.previousBackStackEntry != null) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.align(Alignment.TopEnd) // Align the back button to the top right
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack
                            , contentDescription = "Back",
                            modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = MovieScreen.Main.name,
            modifier = Modifier.padding(innerPadding)

        ) {
            composable(MovieScreen.Main.name) {
                movieViewModel.resetMovie()
                MainScreen(movieViewModel,navController)
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


