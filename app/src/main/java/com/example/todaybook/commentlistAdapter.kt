package com.example.todaybook

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class commentlistAdapter(val context : Context, val comment_time_array:ArrayList<String>, val comment_array:ArrayList<String>,val comment_writer_array:ArrayList<String>): BaseAdapter() {
    var database = FirebaseDatabase.getInstance().reference
    val storage = FirebaseStorage.getInstance()
    override fun getCount(): Int {
        return comment_time_array.size
    }

    override fun getItem(idx: Int): Any {
        return comment_time_array[idx]
    }

    override fun getItemId(idx: Int): Long {
        return idx.toLong()
    }


    override fun getView(idx: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.comments_list, parent, false) as View
        view.findViewById<TextView>(R.id.comments_item).text = comment_array[idx]
        val namelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if (key == "UserId")
                        view.findViewById<TextView>(R.id.comment_writer).text = value
                    break
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(comment_writer_array[idx]).addValueEventListener(namelistener)

        view.findViewById<TextView>(R.id.comment_time).text = comment_time_array[idx]
        val friend_profileimg=view.findViewById<ImageView>(R.id.commenter_img)
        val storageRef = storage.reference
        var ref = storageRef.child("profileimg/"+comment_writer_array[idx])
        ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
            val imageURL = uri.toString()
            Glide.with(view.context).load(imageURL).into(friend_profileimg)
        }).addOnFailureListener(OnFailureListener {
            print("download failed")
        })
        return view
    }

}