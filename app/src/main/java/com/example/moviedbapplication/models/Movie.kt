package com.example.moviedbapplication.models

data class Movie(
    var id: Long = 0L,
    var title: String,
    var posterPath: String,
    var backdropPath: String,
    val releaseDate: String,
    var overview : String,
    var genres : List<String>,
    var homepage : String,
    var imdb_id : String

)
