package com.example.moviedbapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviedbapplication.database.MovieDao
import com.example.moviedbapplication.database.MovieDatabase
import com.example.moviedbapplication.database.MovieEntity
import com.example.moviedbapplication.ui.navigation.MovieScreen
import com.example.moviedbapplication.database.Movies
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.viewmodel.MovieViewModel
import com.example.moviedbapplication.utils.Constants

private lateinit var movieDatabase: MovieDatabase
private lateinit var movieDao: MovieDao

@Composable
fun MainScreen(movieViewModel: MovieViewModel, navController: NavController) {
    Scaffold { innerPadding ->

        MovieDBApp(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            movieViewModel = movieViewModel
        )
    }
}

enum class Title {
    Popular,
    Top,
    Favorites
}


@Composable
fun MovieDBApp(
    modifier: Modifier = Modifier,
    navController: NavController,
    movieViewModel: MovieViewModel
) {

    val uiState by movieViewModel.uiState.collectAsState()
    val isGrid = uiState.isGrid
    val movies = uiState.movies
    val selectedCategory = uiState.selectedCategory

    var expanded by remember { mutableStateOf(false) }
    var genreMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val fullGenreMap = Movies().getGenreMap()
    val usedGenreIds = movies.flatMap { it.genreIds!! }.toSet()
    val genreMap = fullGenreMap.filterKeys { it in usedGenreIds }



    Column{
        MovieDBAppTopRow(
            movieViewModel = movieViewModel,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onGenreClick = { genreMenuExpanded = true },
            onCategoryClick = { categoryMenuExpanded = true },
            genreMenuExpanded = genreMenuExpanded,
            onGenreMenuChange = { genreMenuExpanded = it },
            categoryMenuExpanded = categoryMenuExpanded,
            onCategoryMenuChange = { categoryMenuExpanded = it },
            genreMap = genreMap,
            selectedCategory = selectedCategory
        )




        if (!isGrid) {
            MovieList(
                movies = movies,
                modifier = modifier,
                navController = navController,

            )
        } else {
            MovieGrid(
                sectionedMovies = Movies().createSectionedMoviesByGenre(movies),
                modifier = modifier,
                navController = navController
            )
        }
    }
}

@Composable
fun MovieDBAppTopRow(
    movieViewModel: MovieViewModel,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onGenreClick: () -> Unit,
    onCategoryClick: () -> Unit,
    genreMenuExpanded: Boolean,
    onGenreMenuChange: (Boolean) -> Unit,
    categoryMenuExpanded: Boolean,
    onCategoryMenuChange: (Boolean) -> Unit,
    genreMap: Map<Int, String>,
    selectedCategory: String?

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MovieCategoryTitle(movieViewModel)

        Spacer(modifier = Modifier.weight(1f))

        Box {
            Button(
                onClick = { onExpandedChange(true) }
            ) {
                Text("Options")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                DropdownMenuItem(
                    text = { Text("Toggle Grid") },
                    onClick = {
                        movieViewModel.toggleGrid()
                        onExpandedChange(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Filter by Genre") },
                    onClick = {
                        onGenreClick()
                        onExpandedChange(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Filter by Category") },
                    onClick = {
                        onCategoryClick()
                        onExpandedChange(false)
                    }
                )
            }
            GenreDropdownMenu(
                expanded = genreMenuExpanded,
                onDismissRequest = { onGenreMenuChange(false) },
                genreMap = genreMap,
                movieViewModel = movieViewModel
            )

            CategoryDropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = { onCategoryMenuChange(false) },
                selectedCategory = selectedCategory,
                movieViewModel = movieViewModel
            )
        }
    }


}


@Composable
fun GenreDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    genreMap: Map<Int, String>,
    movieViewModel: MovieViewModel
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text("Show All") },
            onClick = {
                movieViewModel.showAllMovies()
                onDismissRequest()
            }
        )

        genreMap.values.forEach { genreName ->
            DropdownMenuItem(
                text = { Text(genreName) },
                onClick = {
                    movieViewModel.getMoviesByGenre(genreName)
                    onDismissRequest()
                })
        }
    }
}

@Composable
fun CategoryDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    selectedCategory: String?,
    movieViewModel: MovieViewModel
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        if (selectedCategory != "popular") {
            DropdownMenuItem(
                text = { Text("Popular") },
                onClick = {
                    fetchMovies(movieViewModel = movieViewModel, movieType = "popular")
                    onDismissRequest()
                }
            )
        }

        if (selectedCategory != "top_rated") {
            DropdownMenuItem(
                text = { Text("Top Rated") },
                onClick = {
                    fetchMovies(movieViewModel = movieViewModel, movieType = "top_rated")
                    onDismissRequest()
                }
            )
        }

        if (selectedCategory != "favorites") {

            DropdownMenuItem(
                text = { Text("Favorites") },
                onClick = {
                    fetchFavoriteMovies(movieViewModel = movieViewModel, movieType = "favorites")
                    onDismissRequest()
                }
            )
        }
    }
}






@Composable
fun MovieCategoryTitle(movieViewModel: MovieViewModel) {
    val uiState by movieViewModel.uiState.collectAsState()

    val category = uiState.selectedCategory?.lowercase()

    val title = when (category) {
        "popular" -> Title.Popular
        "top_rated" -> Title.Top
        "favorites" -> Title.Favorites
        else -> null
    }

    title?.let {
        Text(
            text = "${it.name} Movies",
            modifier = Modifier,
            style = MaterialTheme.typography.headlineLarge
        )
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

fun fetchMovies(movieType: String, movieViewModel: MovieViewModel) {
    movieViewModel.getMovies(movieType = movieType)
}

fun fetchFavoriteMovies(movieType: String, movieViewModel: MovieViewModel) {
    movieViewModel.getFavoriteMovies(movieType = movieType)
}