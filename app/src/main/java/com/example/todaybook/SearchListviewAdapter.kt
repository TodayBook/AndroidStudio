package com.example.todaybook

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.annotations.SerializedName





class SearchListviewAdapter (val context: Context, val bookList: List<book>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.search_listview_item, null)
        val bookimage = view.findViewById<ImageView>(R.id.bookimageImg)
        val booktitle = view.findViewById<TextView>(R.id.booktitleTv)
        val bookpublisher = view.findViewById<TextView>(R.id.bookpublisherTv)
        val bookauthor = view.findViewById<TextView>(R.id.bookauthorTv)


        val book = bookList[position]
       /* val resourceId = context.resources.getIdentifier(book.thumbnail, "drawable", context.packageName)*/
        Glide.with(view.context).load(book.thumbnail).into(bookimage)

        /*bookimage.setImageResource(resourceId)*/
        booktitle.text = book.title
        bookpublisher.text = book.publisher
        bookauthor.text = (book.authors).toString()

        return view
    }

    override fun getItem(position: Int): Any {
        return bookList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return bookList.size
    }


}