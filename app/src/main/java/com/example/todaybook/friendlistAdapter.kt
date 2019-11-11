package com.example.todaybook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class friendlistAdapter(val context : Context, val friendlist:ArrayList<String>): BaseAdapter() {
    override fun getCount(): Int {
        return friendlist.size
    }

    override fun getItem(idx: Int): Any {
        return friendlist[idx]
    }

    override fun getItemId(idx: Int): Long {
        return idx.toLong()
    }

    override fun getView(idx: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.friendlist, parent, false) as View
        view.findViewById<TextView>(R.id.FriendId).text = friendlist[idx]
        return view
    }
}