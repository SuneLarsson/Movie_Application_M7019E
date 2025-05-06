package com.example.moviedbapplication.ui.navigation



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moviedbapplication.ui.screens.DetailScreen
import com.example.moviedbapplication.ui.screens.MainScreen
import com.example.moviedbapplication.ui.screens.ThirdScreen
import com.example.moviedbapplication.viewmodel.MovieViewModel
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
    movieViewModel: MovieViewModel,
    isOnline: Boolean
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentScreen = MovieScreen.entries.find { route ->
        currentRoute?.startsWith(route.name) == true
    } ?: MovieScreen.Main


    NavHost(
        navController = navController,
        startDestination = MovieScreen.Main.name,
        modifier = Modifier
    ) {
        composable(MovieScreen.Main.name) {
            MainScreen(movieViewModel, navController,isOnline)
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
                    movieViewModel = movieViewModel,
                    isOnline
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
            ThirdScreen(navController = navController, movieViewModel = movieViewModel, movieId = movieId,isOnline = isOnline)
        }
    }

}
