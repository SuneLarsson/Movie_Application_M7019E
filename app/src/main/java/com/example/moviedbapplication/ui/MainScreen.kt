package com.example.moviedbapplication.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.moviedbapplication.MovieScreen
import com.example.moviedbapplication.database.Movies
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.utils.Constants

@Composable
fun MainScreen(movieViewModel: MovieViewModel = MovieViewModel(), navController: NavController) {
    Scaffold { innerPadding ->
        fetchMovies("popular", movieViewModel)
        MovieDBApp(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            movieViewModel = movieViewModel
        )
    }
}


@Composable
fun MovieDBApp(modifier: Modifier = Modifier, navController: NavController, movieViewModel: MovieViewModel) {
    val uiState by movieViewModel.uiState.collectAsState()
    val isGrid = uiState.isGrid
    val movies = uiState.movies
    Column {
        Button(

            onClick = { movieViewModel.toggleGrid() }
        ) {
            Text("PROV")
        }
        //Change to Option button
        Button(
            onClick = {movieViewModel.getMoviesByGenre("Action")}
        ) { Text("Action")}

        if (!isGrid) {

            MovieList(
                movies = movies,
                modifier = modifier,
                navController = navController
            )
        } else {
            MovieGrid(
                sectionedMovies = Movies().createSectionedMoviesByGenre(
                    movies = movies
                ),
                modifier = modifier,
                navController = navController
            )
        }
    }
}




@Composable
fun MovieList(movies: List<Movie>, modifier: Modifier = Modifier, navController: NavController){

    LazyColumn(){
        items(movies){ movie ->
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

                movie.releaseDate?.let {
                    Text(text = it,
                        style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.padding(8.dp))

                movie.overview?.let {
                    Text(text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))

            }
        }

    }


}

@Composable
fun MovieGrid(
    sectionedMovies: Map<String, List<Movie>>,
    navController: NavController,
    modifier: Modifier = Modifier,

) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        sectionedMovies.forEach { (category, movieList) ->
            item {
                Column {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(movieList) { movie ->
                            MovieGridCard(movie = movie, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieGridCard(movie: Movie, modifier: Modifier = Modifier, navController: NavController){
    val context = LocalContext.current

    Card (modifier = modifier,
        onClick = {
            navController.navigate("${MovieScreen.Details.name}/${movie.id}")
        }
    ) {
        Box {
            AsyncImage(
                model = Constants.POSTER_IMAGE_BASE_URL + Constants.GRID_IMAGE_BASE_WIDTH + movie.posterPath,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(154.dp)
                    .height(231.dp),

                contentScale = ContentScale.Crop
            )
        }
    }
}

fun fetchMovies(movieType: String, movieViewModel: MovieViewModel){
    movieViewModel.getMovies(movieType)
}

