package com.example.multimediaplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var filesRecyclerView: RecyclerView
    private lateinit var filesListAdapter: FilesListAdapter
    private var filesList: ArrayList<File> = ArrayList<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingButton = findViewById(R.id.floatingActionButton)
        filesRecyclerView = findViewById(R.id.filesRecyclerView)

    }

    override fun onStart() {
        super.onStart()

        floatingButton.setOnClickListener {
            createMenu(floatingButton)
        }

        getFiles()
        filesListAdapter = FilesListAdapter(baseContext, filesList)


        val layoutManager = LinearLayoutManager(baseContext)

        filesRecyclerView.layoutManager = layoutManager
        filesRecyclerView.adapter = filesListAdapter
    }

    private fun createMenu(view: View) {
        val popup = PopupMenu(view.context, view)
        val inflater = popup.menuInflater
        popup.setOnMenuItemClickListener(this)
        inflater.inflate(R.menu.image_menu, popup.menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_photo -> {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_item_video -> {
                Toast.makeText(this, "Video recorder", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_item_record -> {
                val intent = Intent(this, VoiceRecorder::class.java)
                startActivity(intent)
                true
            }
            else -> false
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

        filesList = files.toCollection(ArrayList())
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}