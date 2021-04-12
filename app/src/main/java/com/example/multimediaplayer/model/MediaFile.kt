//package com.example.multimediaplayer.model
//
//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import java.util.*
//
//@Entity(tableName = "files")
//data class MediaFile(
//    @PrimaryKey @ColumnInfo(name = "fileName") var fileName: String,
//    var filePath: String,
//    var fileType: String,
//    var isFavorite: Boolean = false,
//    var tag1: String = "",
//    var tag2: String = "",
//    var tag3: String = ""
//) {
////    override fun equals(other: Any?): Boolean {
////        if (other == null)
////            return false // null check
////
////        if (javaClass != other.javaClass)
////            return false // type check
////
////
////        val mOther = other as MediaFile
////        return  fileName == mOther.fileName
////                && filePath == mOther.filePath
////                && fileType == mOther.fileType
////                && isFavorite == mOther.isFavorite
////                && tag1 == mOther.tag1
////                && tag2 == mOther.tag2
////                && tag3 == mOther.tag3
////    }
////
////    override fun hashCode(): Int {
////        return Objects.hash(fileName, filePath, fileType, isFavorite, tag1, tag2, tag3)
////    }
//}