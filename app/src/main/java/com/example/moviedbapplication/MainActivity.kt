package com.example.moviedbapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moviedbapplication.ui.DetailScreen
import com.example.moviedbapplication.ui.MainScreen
import com.example.moviedbapplication.ui.ThirdScreen

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
fun MovieApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MovieScreen.valueOf(
        backStackEntry?.destination?.route ?: MovieScreen.Main.name
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie App") },
                navigationIcon = {
                    // Only show the back icon if there's a previous screen in the stack
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")

                        }
                    }
                }

            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MovieScreen.Main.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MovieScreen.Main.name) {
                MainScreen(navController)
            }
            composable(MovieScreen.Details.name) {
                DetailScreen(navController)
            }
            composable(MovieScreen.Third.name) {
                ThirdScreen(navController)
            }
        }
    }
}


@Composable
fun MovieAppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("details") { DetailScreen(navController) }
        composable("third") { ThirdScreen(navController) }
    }
}