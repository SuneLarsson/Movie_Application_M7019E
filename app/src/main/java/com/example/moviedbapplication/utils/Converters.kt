package com.example.moviedbapplication.utils


import androidx.room.TypeConverter
import com.example.moviedbapplication.models.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromGenresList(genres: List<Genre>?): String? {
        return Gson().toJson(genres)
    }

    @TypeConverter
    fun toGenresList(genresString: String?): List<Genre>? {
        val type = object : TypeToken<List<Genre>>() {}.type
        return Gson().fromJson(genresString, type)
    }

    @TypeConverter
    fun fromIntList(intList: List<Int>?): String? {
        return intList?.joinToString(",")
    }

    @TypeConverter
    fun toIntList(intListString: String?): List<Int>? {
        return intListString?.split(",")?.map { it.toInt() }
    }
}
