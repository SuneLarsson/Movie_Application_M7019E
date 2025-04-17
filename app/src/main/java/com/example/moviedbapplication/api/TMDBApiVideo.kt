package com.example.moviedbapplication.api

import com.example.moviedbapplication.models.Video
import com.example.moviedbapplication.models.VideoListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiVideo {
    @GET("movie/{movie_id}/videos")
    suspend fun getVideos(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String,
        @Query("language") language: String = "en-US",

        ): Response<VideoListResponse>
}