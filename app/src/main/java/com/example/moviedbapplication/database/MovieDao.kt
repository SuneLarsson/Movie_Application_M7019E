package com.example.moviedbapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert
    suspend fun insert(movie: FavoriteMovieEntity)

    @Update
    suspend fun update(movie: FavoriteMovieEntity)

    @Delete
    suspend fun delete(movie: FavoriteMovieEntity)

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getFavoriteMovieById(id: Long): FavoriteMovieEntity?

    @Query("SELECT * FROM favorites")
    fun getAllFavoritesMovies(): Flow<List<FavoriteMovieEntity>>

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavoritesMovies()

    // =======================
    // 📦 Cached Movies (API list)
    // =======================
    @Query("SELECT * FROM cached_movies ORDER BY position ASC")
    fun getAllCachedMovies():  Flow<List<CachedMovieEntity>>

    @Query("SELECT * FROM cached_movies WHERE id = :id")
    suspend fun getCachedMovieById(id: Long): CachedMovieEntity?

//    @Insert
//    suspend fun insertAllCachedMovies(movies: List<CachedMovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<CachedMovieEntity>)

    @Query("DELETE FROM cached_movies")
    suspend fun clearMovies()



//    @Insert
//    suspend fun insert(movie: MovieEntity)
//
//    @Update
//    suspend fun update(movie: MovieEntity)
//
//    @Delete
//    suspend fun delete(movie: MovieEntity)
//
//    @Query("SELECT * FROM movies WHERE id = :id")
//    suspend fun getMovieById(id: Long): MovieEntity?
//
//    @Query("SELECT * FROM movies")
//    fun getAllMovies(): Flow<List<MovieEntity>>
//
//    @Query("DELETE FROM movies")
//    suspend fun deleteAllMovies()
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(movies: List<MovieEntity>)
//
//    @Query("DELETE FROM movies")
//    suspend fun clearMovies()
}