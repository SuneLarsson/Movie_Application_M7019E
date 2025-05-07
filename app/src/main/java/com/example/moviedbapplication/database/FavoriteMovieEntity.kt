package com.example.moviedbapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moviedbapplication.models.Genre

@Entity(tableName = "favorites")
data class FavoriteMovieEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val overview: String?,

    val genreIds: List<Int>? = emptyList(),

    val homepage: String?,
    val imdbId: String?,

    val genres: List<Genre>? = emptyList()

)