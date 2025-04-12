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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.moviedbapplication.database.Movies
import com.example.moviedbapplication.ui.DetailScreen
import com.example.moviedbapplication.ui.MainScreen
import com.example.moviedbapplication.ui.MovieViewModel
import com.example.moviedbapplication.ui.ThirdScreen
import com.example.moviedbapplication.ui.fetchMovies


enum class MovieScreen {
    Main,
    Details,
    Third
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieApp()
        }
    }
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
                MainScreen(movieViewModel,navController)
            }
            composable(
                "${MovieScreen.Details.name}/{movieId}",
                arguments = listOf(navArgument("movieId") {
                    type = NavType.LongType
                }) // Use NavType.LongType
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getLong("movieId")

                if (movieId == null) {
                    Log.e("NavHost", "movieId is null or not passed in route!")
                    return@composable
                } else {
                    Log.d("NavHost", "Received movieId: $movieId")
                }
                movieViewModel.setMovieById(movieId)
                val movie = movieViewModel.getMovieById(movieId)

                if (movie == null) {
                    Log.e("NavHost", "No movie found for movieId: $movieId")
                    return@composable
                } else {
                    Log.d("NavHost", "Found movie: ${movie.title}")
                }
                DetailScreen(
                    navController, movie,
                    movieViewModel = movieViewModel
                )
            }
            composable(MovieScreen.Third.name) {
                ThirdScreen(navController)
            }

        }
    }
}


