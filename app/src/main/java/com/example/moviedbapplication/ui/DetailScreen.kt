package com.example.moviedbapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moviedbapplication.database.Movies
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.utils.Constants

import coil.compose.AsyncImage

@Composable
fun DetailScreen(navController: NavController) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {
            MovieDBApp(
                modifier = Modifier.padding(innerPadding),
                NavController = navController
            )

        }
    }
}




@Composable
fun MovieDBApp(modifier: Modifier = Modifier, NavController: NavController) {
    MovieList(movieList = Movies().getMovies(), modifier = modifier, NavController = NavController)
}

@Composable
fun MovieList(movieList: List<Movie>, modifier: Modifier = Modifier, NavController: NavController){
    LazyColumn(){
        items(movieList){ movie ->
            MovieCard(movie = movie, modifier = Modifier.padding(8.dp), navController = NavController)
        }
    }
}

@Composable
fun MovieCard(movie: Movie, modifier: Modifier = Modifier, navController: NavController){
    Card (modifier = modifier) {
        Row {
            Column {

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
                Button(
                    onClick = { navController.navigate("third") },
                    modifier = Modifier.size(92.dp, 35.dp)
                        .padding(vertical = 2.dp)
                ) {
                    Text("Review",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 0.dp))
                }
                Button(
                    onClick = { "open"},
                    modifier = Modifier.size(92.dp, 35.dp)
                        .padding(vertical = 2.dp)
                ) {
                    Text("IMDB",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 0.dp))
                }

            }
            Column {
                Text(text = movie.title,
                    style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.padding(8.dp))

                Text(text = movie.releaseDate,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.padding(8.dp))

                Text(text = movie.homepage,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.padding(8.dp))

                Text(text = movie.genres.joinToString(", "),
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