package com.example.moviedbapplication.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviedbapplication.ui.navigation.MovieScreen
import com.example.moviedbapplication.models.Genre
import com.example.moviedbapplication.ui.MovieViewModel
import com.example.moviedbapplication.utils.Constants
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.moviedbapplication.database.MovieDao
import com.example.moviedbapplication.database.MovieDatabase
import com.example.moviedbapplication.models.Movie
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private lateinit var movieDatabase: MovieDatabase
private lateinit var movieDao: MovieDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    movieViewModel: MovieViewModel) {
    val context = LocalContext.current

    val uiState = movieViewModel.uiState.collectAsState()
    val movieId = uiState.value.movieId
    movieViewModel.setMovieById(movieId)
    val movie = movieViewModel.getMovieById(movieId) ?: return

    val isFavorited = remember { mutableStateOf(false) }

    LaunchedEffect(movie.id) {
        val db = MovieDatabase.getDatabase(context)
        val dao = db.movieDao()
        isFavorited.value = dao.getMovieById(movie.id) != null
    }
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = movie.title )},
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    if(navController.previousBackStackEntry != null) {
                        IconButton(
                            onClick = { navController.navigateUp() },
                        ){
                            Icon(Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(32.dp))
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            saveToFavorite(context, movie)
                            isFavorited.value = !isFavorited.value
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorited.value) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (isFavorited.value) "Unfavorite" else "Favorite",
                            tint = if (isFavorited.value) Color.Yellow else Color.White
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)) {
            DetailsCard(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                movieViewModel = movieViewModel,
                movie = movie,
                context = context
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


fun saveToFavorite(context: Context, movie: Movie) {
    val movieDatabase = MovieDatabase.getDatabase(context)
    val movieDao = movieDatabase.movieDao()

    CoroutineScope(Dispatchers.IO).launch {
        val existingMovie = movieDao.getMovieById(movie.id)
        if (existingMovie != null) {
            movieDao.delete(existingMovie)
        } else {
            val movieEntity = movie.toMovieEntity()
            movieDao.insert(movieEntity)
        }
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
fun DetailsCard(modifier: Modifier = Modifier, navController: NavController, movieViewModel: MovieViewModel, movie: Movie, context: Context){
//    val uiState = movieViewModel.uiState.collectAsState()
//    val movieId = uiState.value.movieId
//    movieViewModel.setMovieById(movieId)
//    val movie = movieViewModel.getMovieById(movieId) ?: return
//    val isFavorited = remember { mutableStateOf(false) }
//
//    LaunchedEffect(movie.id) {
//        val db = MovieDatabase.getDatabase(context)
//        val dao = db.movieDao()
//        isFavorited.value = dao.getMovieById(movie.id) != null
//    }


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

                    movie.genres?.let { MovieGenresList(genres = it) }



                }
            }
        Row (modifier = modifier.align(Alignment.CenterHorizontally)){
            Button(
                onClick = { navController.navigate("${MovieScreen.Third.name}/${movie.id}") },
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
//            FavoriteToggle(
//                movie = movie,
//                isFavorited = isFavorited.value,
//                onToggleFavorite = {
//                    saveToFavorite(context, movie)
//                    isFavorited.value = !isFavorited.value
//                }
//            )

//            Button(
//                onClick = { movie.let { saveToFavorite(context, it)} },
//                modifier = Modifier.size(92.dp, 35.dp)
//                    .padding(vertical = 2.dp)
//            ) {                 Text("Favorite",
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.padding(vertical = 0.dp))}
        }

        movie.overview?.let {
            Text(text = it,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        VideoList(movieViewModel = movieViewModel)
    }

}

@Composable
fun FavoriteToggle(
    movie: Movie,
    isFavorited: Boolean,
    onToggleFavorite: () -> Unit
) {
    IconButton(
        onClick = onToggleFavorite,
        modifier = Modifier
            .size(35.dp)
            .padding(2.dp)
    ) {
        Icon(
            imageVector = if (isFavorited) Icons.Filled.Star else Icons.Outlined.Star,
            contentDescription = if (isFavorited) "Unfavorite" else "Favorite",
            tint = if (isFavorited) Color.Yellow else Color.Black
        )
    }
}


@Composable
fun MovieGenresList(genres: List<Genre>) {
    LazyRow (horizontalArrangement = Arrangement.spacedBy(8.dp) ){
        items(genres) { genre ->
            Text(
                text = genre.name,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)  // Add some padding for spacing
            )
        }
    }
}

@Composable
fun VideoList(movieViewModel: MovieViewModel ) {
    val uiState = movieViewModel.uiState.collectAsState()
    val movieId = uiState.value.movieId

    movieViewModel.setVideos(movieId)
    val videos = uiState.value.videos


    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
            .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        items(videos) { video ->
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = video.name.replace("\"", "") ?: "Video",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                VideoPlayer(videoKey = video.key, site = video.site)
            }
        }
    }
}

@Composable
fun ExoVideoPlayer(videoKey: String, site: String) {
    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context).build()
    val videoUrl = getVideoUrl(site=site, key = videoKey) !!
    val mediaSource = remember(videoUrl) {
        MediaItem.fromUri(videoUrl)
    }

    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Composable
fun VideoPlayer(videoKey: String, site: String) {
    when (site) {
        "YouTube" -> YouTubePlayer(videoKey)
        else -> ExoVideoPlayer(videoKey, site)
    }
}
@Composable
fun YouTubePlayer(videoKey: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var playRequested by remember { mutableStateOf(false) }

    val thumbnailUrl = "https://img.youtube.com/vi/$videoKey/hqdefault.jpg"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (playRequested) {
            AndroidView(
                factory = { ctx ->
                    YouTubePlayerView(ctx).apply {
                        enableAutomaticInitialization = false

                        lifecycleOwner.lifecycle.addObserver(this)

                        addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.loadVideo(videoKey, 0f)
                            }
                        })
                    }
                },
                modifier = Modifier.matchParentSize()
            )
        } else {
            AsyncImage( // from Coil
                model = thumbnailUrl,
                contentDescription = "Video thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
            )

            IconButton(
                onClick = { playRequested = true },
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

private fun getVideoUrl(site: String, key: String): String? {
    return when (site.lowercase()) {
        "youtube" -> "https://www.youtube.com/embed/$key"
        "vimeo" -> "https://player.vimeo.com/video/$key"
        "dailymotion" -> "https://www.dailymotion.com/embed/video/$key"
        else -> null
    }
}
