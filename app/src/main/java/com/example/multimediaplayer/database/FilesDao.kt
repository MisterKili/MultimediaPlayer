//package com.example.multimediaplayer.database
//
//import androidx.room.*
//import com.example.multimediaplayer.model.MediaFile
//
//@Dao
//public interface FilesDao {
//
//    @Query("SELECT * FROM files")
//    fun getAllFiles(): List<MediaFile>
//
//    @Query("SELECT * FROM files WHERE isFavorite")
//    fun getAllFavoriteFiles(): List<MediaFile>
//
//    @Insert
//    fun insert(file: MediaFile)
//
//    @Delete
//    fun delete(file: MediaFile)
//}