package com.example.moviedbapplication.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun DetailScreen(navController: NavController, movie: Movie) {
    Log.d("DetailScreen", "Displaying details for movieId: ${movie.id}")
    Scaffold { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {
            DetailsCard(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                movie = movie
            )

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



@Composable
fun DetailsCard(modifier: Modifier = Modifier, navController: NavController, movie: Movie){
    val context = LocalContext.current

    Column(modifier = modifier) {
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

                    movie.releaseDate?.let {
                        Text(text = it,
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.padding(8.dp))

                    movie.homepage?.let { HomepageHyperlink(homepageUrl = it) }
                    Spacer(modifier = Modifier.padding(8.dp))

//                    LazyRow {
//                        Text(text = movie.genres.joinToString(", "),
//                            style = MaterialTheme.typography.bodySmall)
//                        Spacer(modifier = Modifier.padding(8.dp))
//                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between items
                    ) {
                        items(movie.genreIds ?: emptyList()) { genreId ->
                            val genreName = genreMap[genreId] ?: "Unknown Genre"  // Default to "Unknown Genre" if not found in the map
                            Text(
                                text = genreName,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }


                }
            }
        Row (modifier = modifier.align(Alignment.CenterHorizontally)){
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
                onClick = { movie.imdbId?.let { openImdbPage(context, it) } },
                modifier = Modifier.size(92.dp, 35.dp)
                    .padding(vertical = 2.dp)
            ) {
                Text("IMDB",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 0.dp))
            }
        }

        movie.overview?.let {
            Text(text = it,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
    }





}

val genreMap = mapOf(
    28 to "Action",
    12 to "Adventure",
    16 to "Animation",
    35 to "Comedy",
    80 to "Crime",
    99 to "Documentary",
    18 to "Drama",
    10751 to "Family",
    14 to "Fantasy",
    36 to "History",
    27 to "Horror",
    10402 to "Music",
    9648 to "Mystery",
    10749 to "Romance",
    878 to "Science Fiction",
    10770 to "TV Movie",
    53 to "Thriller",
    10752 to "War",
    37 to "Western"
)

