package com.example.moviedbapplication.api

import com.example.moviedbapplication.models.ReviewResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiReview {
    @GET("movie/{movie_id}/reviews")
    suspend fun getReviews(
        @Path("movie_id") movieId: Long,
        @Header("Authorization") authHeader: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<ReviewResponse>
}