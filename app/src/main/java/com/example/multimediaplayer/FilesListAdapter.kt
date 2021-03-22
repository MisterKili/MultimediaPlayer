package com.example.multimediaplayer

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FilesListAdapter(context: Context, filesList: MutableList<File>) : RecyclerView.Adapter<FilesListAdapter.FilesViewHolder>() {

    private var filesList: MutableList<File> = filesList
    private var context = context
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val view = mInflater.inflate(R.layout.image_item, parent, false)
        return FilesViewHolder(view, filesList, context, this)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.fileName.text = filesList[position].name
        holder.image.setImageURI(filesList[position].path.toUri())
    }

    override fun getItemCount(): Int {
        return filesList.size
    }


    class FilesViewHolder(view: View, filesList: MutableList<File>, context: Context, adapter: FilesListAdapter) :
        RecyclerView.ViewHolder(view), View.OnLongClickListener, PopupMenu.OnMenuItemClickListener,
        View.OnClickListener {
        var context: Context
        var image: ImageView
        var fileName: TextView
        var filesList: MutableList<File>
        private var adapter: FilesListAdapter


        override fun onLongClick(view: View): Boolean {
            createMenu(view)
            return false
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
                R.id.menu_item_delete -> {
                    println("Usuwanaie")
                    deleteFile(itemView.rootView)
                    true
                }
                R.id.menu_item_modify -> {
//                    modifyQuote(itemView.rootView)
                    println("Modyfikacja")
                    true
                }
                else -> false
            }
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
            val intent = Intent(view.context, PhotoDetailsActivity::class.java)
            intent.putExtra("photoURI", filesList[position].absolutePath)
            context.startActivity(intent)
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