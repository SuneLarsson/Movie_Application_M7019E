package com.example.moviedbapplication.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviedbapplication.MovieScreen
import com.example.moviedbapplication.database.Movies
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.utils.Constants

@Composable
fun MainScreen(navController: NavController) {
    Scaffold { innerPadding ->
        MovieDBApp(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}


@Composable
fun MovieDBApp(modifier: Modifier = Modifier, navController: NavController) {
    MovieList(movieList = Movies().getMovies(), modifier = modifier, navController = navController)
}

@Composable
fun MovieList(movieList: List<Movie>, modifier: Modifier = Modifier, navController: NavController){
    LazyColumn(){
        items(movieList){ movie ->
            MovieCard(movie = movie, modifier = Modifier.padding(8.dp), navController = navController)
        }
    }
}

@Composable
fun MovieCard(movie: Movie, modifier: Modifier = Modifier, navController: NavController){
    val context = LocalContext.current

    Card (modifier = modifier,
        onClick = {
            navController.navigate("${MovieScreen.Details.name}/${movie.id}")
        }
    ) {
        Row {
            Box {

                AsyncImage(
                    model = Constants.POSTER_IMAGE_BASE_URL + Constants.POSTER_IMAGE_BASE_WIDTH + movie.posterPath,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .width(92.dp)
                        .height(138.dp),

                    contentScale = ContentScale.Crop
                )
            }

            Column {
                Text(text = movie.title,
                    style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.padding(8.dp))

                Text(text = movie.releaseDate,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.padding(8.dp))

                Text(text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.padding(8.dp))

            }
        }

    }


}