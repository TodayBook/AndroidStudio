package com.example.todaybook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


class SearchListviewAdapter (val context: Context, val bookList: ArrayList<book>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.search_listview_item, null)
        val bookimage = view.findViewById<ImageView>(R.id.bookimageImg)
        val booktitle = view.findViewById<TextView>(R.id.booktitleTv)
        val bookpublisher = view.findViewById<TextView>(R.id.bookpublisherTv)
        val bookauthor = view.findViewById<TextView>(R.id.bookauthorTv)


        val book = bookList[position]
        val resourceId = context.resources.getIdentifier(book.image, "drawable", context.packageName)
        bookimage.setImageResource(resourceId)
        booktitle.text = book.title
        bookpublisher.text = book.publisher
        bookauthor.text = book.author

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
