package com.example.moviedbapplication.models

import com.google.android.libraries.places.api.model.Review
import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("genre_ids")
    val genreIds: List<Int>? = emptyList(),

    @SerializedName("homepage")
    val homepage: String? = null,  // not available in all endpoints

    @SerializedName("imdb_id")
    val imdbId: String? = null,

    @SerializedName("genres")
    val genres: List<Genre>? = emptyList()

)

data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

