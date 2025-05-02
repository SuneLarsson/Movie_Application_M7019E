package com.example.moviedbapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moviedbapplication.models.Genre
import androidx.room.TypeConverters

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val overview: String?,

    val genreIds: List<Int>? = emptyList(),

    val homepage: String? = null,
    val imdbId: String? = null,

    val genres: List<Genre>? = emptyList()
)

