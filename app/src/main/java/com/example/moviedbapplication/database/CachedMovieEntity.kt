package com.example.moviedbapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_movies")
data class CachedMovieEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val overview: String?,

    val genreIds: List<Int>? = emptyList(),
    val position: Int
)