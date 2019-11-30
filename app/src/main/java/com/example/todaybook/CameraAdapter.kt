package com.example.todaybook


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CameraAdapter(val context: Context, private val CameraDataModelList: ArrayList<CameraDataModel>,val itemClick: (CameraDataModel) -> Unit) : RecyclerView.Adapter<CameraAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.camera_listview_item, parent, false)
        return ViewHolder(view,itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(CameraDataModelList[position])
    }

    override fun getItemCount(): Int {
        return CameraDataModelList.size
    }

    inner class ViewHolder(itemView: View, itemClick : (CameraDataModel)-> Unit ): RecyclerView.ViewHolder(itemView) {

        fun bindItems(CameraDataModel: CameraDataModel) {
            val imageView = itemView.findViewById<ImageView>(R.id.cameraimg)
            Glide.with(itemView.context).load(CameraDataModel.url).into(imageView)
            itemView.setOnClickListener { itemClick(CameraDataModel) }
        }
    }
}