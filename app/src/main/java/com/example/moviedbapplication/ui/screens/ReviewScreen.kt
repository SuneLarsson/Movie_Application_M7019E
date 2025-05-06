package com.example.moviedbapplication.ui.screens

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moviedbapplication.models.Review
import com.example.moviedbapplication.viewmodel.MovieViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdScreen(
    navController: NavController,
    movieId: Long,
    movieViewModel: MovieViewModel) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "Reviews")},
                modifier =
                    Modifier
                        .fillMaxWidth(),
                navigationIcon = {
                    if(navController.previousBackStackEntry != null) {
                        IconButton(
                            onClick = { navController.navigateUp() },
                        ){
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(32.dp))
                        }
                    }
                })

        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            val uiState by movieViewModel.uiState.collectAsState()
            val loading by movieViewModel.loading.collectAsState()
            LaunchedEffect(movieId) {
                movieViewModel.getMovieReviews(movieId)
            }
            val reviews = uiState.reviews
            Column(modifier = Modifier.padding(16.dp)) {

                if (loading) {
                    CircularProgressIndicator()
                } else if (uiState.reviews.isEmpty()) {
                    Text("No reviews available.")
                } else {
                    LazyColumn (){
                        items(reviews) { review ->
                            ReviewItem(review = review)
                        }
                    }
                }
                }

        }
    }
}


@Composable
fun ReviewItem(review: Review) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar (optional)
                val avatarUrl = review.author_details.avatar_path?.let { path ->
                    if (path.startsWith("/")) "https://image.tmdb.org/t/p/w185$path" else path
                }

                if (!avatarUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = avatarUrl),
                        contentDescription = "Author Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = review.author_details.name.ifBlank { review.author },
                        style = MaterialTheme.typography.titleMedium
                    )
                    review.author_details.rating?.let {
                        Text("⭐ $it", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        text = Html.fromHtml(review.content, Html.FROM_HTML_MODE_LEGACY)
                        textSize = 14f
                        setLineSpacing(0f, 1.2f)
                    }
                },
                update = {
                    it.text = Html.fromHtml(review.content, Html.FROM_HTML_MODE_LEGACY)
                    it.maxLines = if (expanded) Int.MAX_VALUE else 6
                    it.ellipsize = if (expanded) null else android.text.TextUtils.TruncateAt.END
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🕒 ${review.created_at.take(10)}", // just the date
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


