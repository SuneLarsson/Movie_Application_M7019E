package com.example.moviedbapplication.api

import com.example.moviedbapplication.models.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiMovie {
    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String,
        @Query("language") language: String = "en-US",

    ): Response<Movie>
}