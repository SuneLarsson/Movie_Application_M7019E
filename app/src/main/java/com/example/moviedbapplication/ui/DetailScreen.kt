package com.example.moviedbapplication.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviedbapplication.database.Movies
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.utils.Constants

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
    val context = LocalContext.current
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
                    onClick = { openImdbPage(context, movie.imdb_id) },
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

                HomepageHyperlink(homepageUrl = movie.homepage)
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

fun openImdbPage(context: Context, imdbId: String) {

    // Intent for IMDb app (deep link)
    val imdbAppIntent = Intent(Intent.ACTION_VIEW).apply {
        data = "imdb:///title/$imdbId".toUri()
        setPackage("com.imdb.mobile")
    }

    // Fallback intent for browser
    val imdbWebIntent = Intent(Intent.ACTION_VIEW).apply {
        data = (Constants.IMDB_BASE_URL + imdbId).toUri()
    }

    try {
        context.startActivity(imdbAppIntent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(imdbWebIntent)
    }
}

@Composable
fun HomepageHyperlink(homepageUrl: String) {
    val context = LocalContext.current

    Text(
        text = homepageUrl,
        color = Color(0xFF1E88E5),
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.clickable {
            val intent = Intent(Intent.ACTION_VIEW, homepageUrl.toUri())
            context.startActivity(intent)
        }
    )
}