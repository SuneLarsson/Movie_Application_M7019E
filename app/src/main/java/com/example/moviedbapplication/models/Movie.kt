package com.example.moviedbapplication.models

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
        return FavoriteMovieEntity(
            id = this.id,
            title = this.title,
            posterPath = this.posterPath,
            backdropPath = this.backdropPath,
            releaseDate = this.releaseDate,
            overview = this.overview,
            genreIds = this.genreIds,
            homepage = this.homepage,
            imdbId = this.imdbId,
            genres = this.genres
        )
    }
    fun toCachedMovieEntity(): CachedMovieEntity {
        return CachedMovieEntity(
            id = this.id,
            title = this.title,
            posterPath = this.posterPath,
            backdropPath = this.backdropPath,
            releaseDate = this.releaseDate,
            overview = this.overview,
            genreIds = this.genreIds
        )
    }
}



data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
