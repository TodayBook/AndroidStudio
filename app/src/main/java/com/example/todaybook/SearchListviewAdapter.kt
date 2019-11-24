package com.example.todaybook

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.search_listview_item.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_listview_item.view.*


class SearchListviewAdapter (val context: Context, val bookList: List<book>) : BaseAdapter() {



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.search_listview_item, null)
        val bookimage = view.findViewById<ImageView>(R.id.bookimageImg)
        val booktitle = view.findViewById<TextView>(R.id.booktitleTv)
        val bookpublisher = view.findViewById<TextView>(R.id.bookpublisherTv)
        val bookauthor = view.findViewById<TextView>(R.id.bookauthorTv)
        var button:Button = view.findViewById(R.id.bt_addtolib)


        val book = bookList[position]
        /* val resourceId = context.resources.getIdentifier(book.thumbnail, "drawable", context.packageName)*/
        Glide.with(view.context).load(book.thumbnail).into(bookimage)

        /*bookimage.setImageResource(resourceId)*/
        booktitle.text = book.title
        bookpublisher.text = book.publisher
        bookauthor.text = (book.authors).toString()
        button.setOnClickListener{
                 val intent = Intent(context, ReadOrRead::class.java)
                 intent.putExtra("Info",BookInfo2(book.thumbnail,book.title,(book.authors).toString(),book.publisher,book.contents))
                 context.startActivity(intent)
        }

        return view
    }


    override fun getItem(position: Int): Any {
        return bookList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return bookList.size
    }





}