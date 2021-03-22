package com.example.multimediaplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var filesRecyclerView: RecyclerView
    private lateinit var filesListAdapter: FilesListAdapter
    private var filesList: List<File> = ArrayList<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getFiles()

        floatingButton = findViewById(R.id.floatingActionButton)
        filesRecyclerView = findViewById(R.id.filesRecyclerView)
        filesListAdapter = FilesListAdapter(baseContext, filesList as MutableList<File>)


        val layoutManager = LinearLayoutManager(baseContext)

        filesRecyclerView.layoutManager = layoutManager
        filesRecyclerView.adapter = filesListAdapter
    }

    override fun onStart() {
        super.onStart()

        floatingButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getFiles() {

        val path: String = getOutputDirectory().absolutePath
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files = directory.listFiles()
        Log.d("Files", "Size: " + files.size)
        for (i in files.indices) {
            Log.d("Files", "FileName:" + files[i].name)
        }

        filesList = files.toList()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}