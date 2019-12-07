package com.example.todaybook

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class CameraAdapter2 (val context: Context, val pictureList: List<Camera>) : BaseAdapter() {



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.camera_listview_item2, null)
        val picture = view.findViewById<ImageView>(R.id.cameraimg)



        val pic = pictureList[position]
        /* val resourceId = context.resources.getIdentifier(book.thumbnail, "drawable", context.packageName)*/
        /*picture.setImageBitmap(pic.picurl)*/
        /*Glide.with(view.context).load(pic.picurl).into(picture)*/


        return view
    }


    override fun getItem(position: Int): Any {
        return pictureList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return pictureList.size
    }





}