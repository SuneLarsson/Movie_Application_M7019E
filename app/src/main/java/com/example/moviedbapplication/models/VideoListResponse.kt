package com.example.moviedbapplication.models

import com.google.gson.annotations.SerializedName

data class VideoListResponse(
    @SerializedName("results")
    val results: List<Video>
)
