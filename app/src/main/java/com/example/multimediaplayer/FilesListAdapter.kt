package com.example.multimediaplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FilesListAdapter(context: Context, filesList: ArrayList<File>) : RecyclerView.Adapter<FilesListAdapter.FilesViewHolder>() {

    private var filesList: ArrayList<File> = filesList
    private var context = context
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val view = mInflater.inflate(R.layout.image_item, parent, false)
        return FilesViewHolder(view, filesList, context, this)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.fileName.text = filesList[position].name
        if (filesList[position].path.endsWith(".mp3")) {
            holder.image.setImageResource(R.drawable.microphone)
        } else {
            holder.image.setImageURI(filesList[position].path.toUri())
        }
    }

    override fun getItemCount(): Int {
        return filesList.size
    }


    class FilesViewHolder(view: View, filesList: MutableList<File>, context: Context, adapter: FilesListAdapter) :
        RecyclerView.ViewHolder(view), View.OnLongClickListener, View.OnClickListener {
        var context: Context
        var image: ImageView
        var fileName: TextView
        var filesList: MutableList<File>
        private var adapter: FilesListAdapter


        override fun onLongClick(view: View): Boolean {
            deleteFile(view)
            return false
        }

        private fun deleteFile(view: View) {
            val position = adapterPosition
            Toast.makeText(context, "Usunieto", Toast.LENGTH_SHORT).show()
            if (filesList[position].delete()) {
                filesList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val filePath = filesList[position].absolutePath
            if (isImageFromPath(filePath)) {
                val intent = Intent(view.context, PhotoDetailsActivity::class.java)
                intent.putExtra("photoURI", filesList[position].absolutePath)
                context.startActivity(intent)
            } else if (isSoundRecordFromPath(filePath)) {
                Toast.makeText(context, "To sound player!", Toast.LENGTH_SHORT).show()
                val intent = Intent(view.context, PlayerActivity::class.java)
                intent.putExtra("fileURI", filesList[position].absolutePath)
                context.startActivity(intent)
            }



        }

        private fun isImageFromPath(path: String): Boolean {
            return path.endsWith(".jpg") or path.endsWith(".png")
        }

        private fun isSoundRecordFromPath(path: String): Boolean {
            return path.endsWith(".mp3")
        }

        private fun isVideoFromPath(path: String): Boolean {
            return path.endsWith(".mp4")
        }

        init {
            view.setOnLongClickListener(this)
            view.setOnClickListener(this)
            this.context = context
            this.image = view.findViewById(R.id.item_imageView)
            this.fileName = view.findViewById(R.id.item_name_textView)
            this.filesList = filesList
            this.adapter = adapter
        }
    }


}