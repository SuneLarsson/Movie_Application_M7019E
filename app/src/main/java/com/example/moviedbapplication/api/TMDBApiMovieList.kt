package com.example.moviedbapplication.api

import com.example.moviedbapplication.models.MovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiMovieList {
    @GET("movie/{movie_type}")
    suspend fun getMovies(
        @Path("movie_type") movieType: String, // Use @Path for dynamic URL path
        @Header("Authorization") authHeader: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<MovieListResponse>
}
