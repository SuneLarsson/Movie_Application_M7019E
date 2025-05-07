package com.example.moviedbapplication.models

import android.util.Log
import com.example.moviedbapplication.database.CachedMovieEntity
import com.example.moviedbapplication.database.FavoriteMovieEntity
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
    val homepage: String? = null,

    @SerializedName("imdb_id")
    val imdbId: String? = null,

    @SerializedName("genres")
    val genres: List<Genre>? = emptyList()

) {
    fun toMovieEntity(): FavoriteMovieEntity {
        Log.d("Movie", "Converting Movie to MovieEntity: $this")
        val genreIds = this.genreIds?.takeIf { it.isNotEmpty() }
            ?: this.genres?.map { it.id } ?: emptyList()

        return FavoriteMovieEntity(
            id = this.id,
            title = this.title,
            posterPath = this.posterPath,
            backdropPath = this.backdropPath,
            releaseDate = this.releaseDate,
            overview = this.overview,
            genreIds = genreIds,
            homepage = this.homepage,
            imdbId = this.imdbId,
            genres = this.genres
        )
    }
    fun toCachedMovieEntity(position: Int): CachedMovieEntity {
        val genreIds = this.genreIds?.takeIf { it.isNotEmpty() }
            ?: this.genres?.map { it.id } ?: emptyList()

        return CachedMovieEntity(
            id = this.id,
            title = this.title,
            posterPath = this.posterPath,
            backdropPath = this.backdropPath,
            releaseDate = this.releaseDate,
            overview = this.overview,
            genreIds = genreIds,
            position = position,
            homepage = this.homepage,
            imdbId = this.imdbId,
            genres = this.genres
        )
    }
}



data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
