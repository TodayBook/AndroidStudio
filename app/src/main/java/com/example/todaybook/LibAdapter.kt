package com.example.todaybook

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
class ViewAdapter(val context: Context, private val imageDataModelList: ArrayList<ImageDataModel>,val itemClick: (ImageDataModel) -> Unit) : RecyclerView.Adapter<ViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.lib_book, parent, false)
        return ViewHolder(view,itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(imageDataModelList[position])
    }

    override fun getItemCount(): Int {
        return imageDataModelList.size
    }

    inner class ViewHolder(itemView: View, itemClick : (ImageDataModel)-> Unit ): RecyclerView.ViewHolder(itemView) {

         fun bindItems(imageDataModel: ImageDataModel) {
            val titleView = itemView.findViewById<TextView>(R.id.title)
            val authorView = itemView.findViewById<TextView>(R.id.author)
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            titleView.text = imageDataModel.title
            authorView.text = imageDataModel.author
            Glide.with(itemView.context).load(imageDataModel.url).into(imageView)
            itemView.setOnClickListener { itemClick(imageDataModel) }
        }
    }
}