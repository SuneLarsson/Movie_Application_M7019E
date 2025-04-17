package com.example.moviedbapplication.ui.screens

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.moviedbapplication.MovieScreen
import com.example.moviedbapplication.models.Genre
import com.example.moviedbapplication.models.Movie
import com.example.moviedbapplication.ui.MovieViewModel
import com.example.moviedbapplication.utils.Constants
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


@Composable
fun DetailScreen(
    navController: NavController,
    movieViewModel: MovieViewModel) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {
            DetailsCard(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                movieViewModel = movieViewModel
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
fun DetailsCard(modifier: Modifier = Modifier, navController: NavController, movieViewModel: MovieViewModel){
    val context = LocalContext.current
    val uiState = movieViewModel.uiState.collectAsState()
    val movieId = uiState.value.movieId
    movieViewModel.setMovieById(movieId)
    val movie = movieViewModel.getMovieById(movieId) ?: return


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

//    LazyRow {
//        items(videos) { video ->
//            Log.d("YOUTUBE", "Video key: ${video.key}")
//            VideoPlayer(videoKey = video.key, site =video.site)
//        }
//    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(videos) { video ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = video.name ?: "Video",
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
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}



private fun getVideoUrl(site: String, key: String): String? {
    return when (site) {
        "YouTube" -> "https://www.youtube.com/embed/$key"
        "Vimeo" -> "https://player.vimeo.com/video/$key"
        "Apple" -> null // No public embed support
        else -> null // Unknown or unsupported
    }
}