package com.example.moviedbapplication.models

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

//    val adult: Boolean = false,
//    @SerializedName("original_language")
//    val originalLanguage: String = "en",  // defaulting to "en" if it's missing
//
//    @SerializedName("original_title")
//    val originalTitle: String? = null,
//
//    val popularity: Float = 0f,
//
//    @SerializedName("vote_average")
//    val voteAverage: Float = 0f,
//
//    @SerializedName("vote_count")
//    val voteCount: Int = 0,
//
//    val video: Boolean = false
)
