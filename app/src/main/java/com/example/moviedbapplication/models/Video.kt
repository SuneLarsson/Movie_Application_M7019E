package com.example.moviedbapplication.models

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("iso_639_1")
    val iso639_1: String,

    @SerializedName("iso_3166_1")
    val iso3166_1: String,

    val name: String,
    val key: String,
    val site: String,
    val size: Int,
    val type: String,
    val official: Boolean,

    @SerializedName("published_at")
    val publishedAt: String,

    val id: String
)