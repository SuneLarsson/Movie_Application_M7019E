package com.example.moviedbapplication.models

import com.google.android.libraries.places.api.model.Review
import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    @SerializedName("results")
    val results: List<Movie>
)

