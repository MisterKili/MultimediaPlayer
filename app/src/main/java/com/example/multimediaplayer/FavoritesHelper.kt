package com.example.multimediaplayer

import java.io.*
import java.util.*

class FavoritesHelper(val outputDirectory: File) {

    val outputDirectoryPath: String = outputDirectory.absolutePath
    var favoriteFile = File("$outputDirectoryPath/favorite.txt")

    fun getFavoriteFiles(): Set<String> {
        var favoritesFileNamesSet = emptySet<String>()
        if (favoriteFile.exists()) {
            val scanner = Scanner(favoriteFile)

            if (favoriteFile.canRead()) {
                while (scanner.hasNextLine()) {
                    val filename = scanner.nextLine()
                    favoritesFileNamesSet = favoritesFileNamesSet.plus(filename)
                }
            }

        }
        return favoritesFileNamesSet
    }

    fun addFileToFavorite(mediaFile: File) {
        if (!favoriteFile.exists())
            favoriteFile.createNewFile()

        favoriteFile.appendText("${mediaFile.name}\n")

        println("AAAAAA " + getFavoriteFiles())
    }

    fun removeFromFavorite(mediaFile: File) {
        if (favoriteFile.exists()) {
            val inputFile = favoriteFile
            val tempFile = File("$outputDirectoryPath/favorite_temp.txt")

            val reader = BufferedReader(FileReader(inputFile))
            val writer = BufferedWriter(FileWriter(tempFile))

            val lineToRemove = mediaFile.name
            var currentLine: String?

            while (reader.readLine().also { currentLine = it } != null) {
                // trim newline when comparing with lineToRemove
                val trimmedLine = currentLine?.trim()
                if (trimmedLine == lineToRemove) continue
                writer.write(currentLine + System.getProperty("line.separator"))
            }
            writer.close()
            reader.close()
            val successful: Boolean = tempFile.renameTo(inputFile)
        }
    }

}