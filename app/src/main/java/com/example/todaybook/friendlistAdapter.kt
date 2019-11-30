package com.example.todaybook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage

class friendlistAdapter(val context : Context, val friendlist:ArrayList<String>,val friendprofilelist:ArrayList<String>): BaseAdapter() {
    val storage = FirebaseStorage.getInstance()
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
        val friend_profileimg=view.findViewById<ImageView>(R.id.friend_profileimg)
        val storageRef = storage.reference
        var ref = storageRef.child("profileimg/"+friendprofilelist[idx])
        ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
            val imageURL = uri.toString()
            Glide.with(view.context).load(imageURL).into(friend_profileimg)
        }).addOnFailureListener(OnFailureListener {
            print("download failed")
        })
        return view
    }

}