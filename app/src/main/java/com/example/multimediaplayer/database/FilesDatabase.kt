//package com.example.multimediaplayer.database
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//import com.example.multimediaplayer.model.MediaFile
//
//@Database(entities = [MediaFile::class], version = 1)
////@TypeConverters(Converters::class)
//public abstract class FilesDatabase : RoomDatabase() {
//
//    abstract fun filesDao(): FilesDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: FilesDatabase? = null
//
//        fun getDatabase(context: Context): FilesDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    FilesDatabase::class.java,
//                    "files_database"
//                ).build()
//                INSTANCE = instance
//                // return instance
//                instance
//            }
//        }
//    }
//}