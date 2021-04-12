//package com.example.multimediaplayer.database
//
//import androidx.room.TypeConverter
//import com.example.multimediaplayer.model.FileType
//
//class Converters {
//
//    companion object {
//        @TypeConverter
//        @JvmStatic
//        fun fromFileType(fileType: FileType): String {
//            return when(fileType) {
//                FileType.PHOTO -> "PHOTO"
//                FileType.AUDIO -> "AUDIO"
//                FileType.VIDEO -> "VIDEO"
//                else -> "OTHER"
//            }
//        }
//
//        @TypeConverter
//        @JvmStatic
//        fun toFileType(fileType: String): FileType {
//            return when(fileType) {
//                "PHOTO" -> FileType.PHOTO
//                "AUDIO" -> FileType.AUDIO
//                "VIDEO" -> FileType.VIDEO
//                else -> FileType.OTHER
//            }
//        }
//    }
//}